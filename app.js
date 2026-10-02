(() => {
  'use strict';
  const DAYS=['일','월','화','수','목','금','토'];
  const COLORS=['#e7e7e7','#91b7e5','#d9bb70','#93c98a','#b8a5da','#edaa87','#df91ae','#68b5b9','#e3c852','#8b9bf0','#c381cf','#ed686e','#6dbbfa','#91d6c8'];
  const START=420,END=1260,STEP=30,SLOTS=(END-START)/STEP,KEY='timetable.events.v1';
  const $=id=>document.getElementById(id);
  const r={schedule:$('schedule'),weekLabel:$('weekLabel'),tabs:$('tabs'),editor:$('editor'),form:$('eventForm'),day:$('dayInput'),start:$('startInput'),end:$('endInput'),title:$('titleInput'),suggestions:$('titleSuggestions'),memo:$('memoInput'),reminder:$('reminderInput'),lead:$('reminderLeadInput'),leadRow:$('reminderLeadRow'),colors:$('colors'),error:$('formError'),modalTitle:$('modalTitle'),del:$('deleteEvent'),copy:$('copyEvent'),info:$('infoDialog')};
  let events=readEvents(),titles=readTitles(),weekOffset=0,view='week',editingId=null,selectedColor=COLORS[1],suggestionIndex=-1;
  function readEvents(){try{const json=window.Android?.getEvents?.()||localStorage.getItem(KEY)||'[]';const data=JSON.parse(json);return Array.isArray(data)?data.filter(e=>Number.isInteger(e.day)&&e.day>=0&&e.day<7&&e.start>=START&&e.end<=END&&e.end>e.start&&typeof e.title==='string'):[]}catch{return []}}
  function persist(){const json=JSON.stringify(events);try{localStorage.setItem(KEY,json)}catch{}if(window.Android?.syncEvents)window.Android.syncEvents(json)}
  function readTitles(){let history=[];try{const data=JSON.parse(window.Android?.getTitles?.()||localStorage.getItem('timetable.titles.v1')||'[]');if(Array.isArray(data))history=data.filter(x=>typeof x==='string')}catch{}return [...new Set([...history,...events.map(e=>e.title)].map(x=>x.trim()).filter(Boolean))].slice(0,100)}
  function recordTitle(title){titles=[title,...titles.filter(x=>x.toLocaleLowerCase('ko')!==title.toLocaleLowerCase('ko'))].slice(0,100);const json=JSON.stringify(titles);try{localStorage.setItem('timetable.titles.v1',json)}catch{}if(window.Android?.syncTitles)window.Android.syncTitles(json)}
  function hideSuggestions(){r.suggestions.hidden=true;r.suggestions.replaceChildren();r.title.setAttribute('aria-expanded','false');r.title.removeAttribute('aria-activedescendant');suggestionIndex=-1}
  function renderSuggestions(){const query=r.title.value.trim().toLocaleLowerCase('ko');r.suggestions.replaceChildren();suggestionIndex=-1;if(!query){hideSuggestions();return}const matches=titles.filter(x=>x.toLocaleLowerCase('ko').startsWith(query)&&x.toLocaleLowerCase('ko')!==query).slice(0,5);matches.forEach((title,i)=>{const button=document.createElement('button');button.type='button';button.role='option';button.id=`suggestion-${i}`;button.textContent=title;button.addEventListener('pointerdown',event=>event.preventDefault());button.addEventListener('click',()=>chooseSuggestion(title));r.suggestions.append(button)});r.suggestions.hidden=matches.length===0;r.title.setAttribute('aria-expanded',String(matches.length>0))}
  function chooseSuggestion(title){r.title.value=title;hideSuggestions();r.title.focus()}
  function suggestionKeydown(event){const options=[...r.suggestions.querySelectorAll('button')];if(!options.length)return;if(event.key==='Escape'){hideSuggestions();return}if(event.key==='ArrowDown'||event.key==='ArrowUp'){event.preventDefault();suggestionIndex=(suggestionIndex+(event.key==='ArrowDown'?1:-1)+options.length)%options.length;options.forEach((option,i)=>option.classList.toggle('active',i===suggestionIndex));r.title.setAttribute('aria-activedescendant',options[suggestionIndex].id)}else if(event.key==='Enter'&&suggestionIndex>=0){event.preventDefault();chooseSuggestion(options[suggestionIndex].textContent)}}
  function time(min){return `${String(Math.floor(min/60)).padStart(2,'0')}:${String(min%60).padStart(2,'0')}`}
  function weekStart(){const now=new Date(),date=new Date(now.getFullYear(),now.getMonth(),now.getDate());date.setDate(date.getDate()-(date.getDay()+6)%7+weekOffset*7);return date}
  function dateLabel(){const start=weekStart(),end=new Date(start);end.setDate(start.getDate()+6);const fmt=d=>`${d.getMonth()+1}월 ${d.getDate()}일`;return `${start.getFullYear()}년 ${fmt(start)} – ${fmt(end)}`}
  function option(value,label){const el=document.createElement('option');el.value=value;el.textContent=label;return el}
  function populateInputs(){DAYS.forEach((day,i)=>r.day.append(option(i,`${day}요일`)));for(let t=START;t<END;t+=STEP)r.start.append(option(t,time(t)));for(let t=START+STEP;t<=END;t+=STEP)r.end.append(option(t,time(t)));COLORS.forEach(color=>{const b=document.createElement('button');b.type='button';b.style.background=color;b.title=color;b.setAttribute('aria-label',`색상 ${color}`);b.dataset.color=color;b.addEventListener('click',()=>selectColor(color));r.colors.append(b)})}
  function selectColor(color){selectedColor=color;r.colors.querySelectorAll('button').forEach(b=>b.classList.toggle('chosen',b.dataset.color===color))}
  function cell(label,classes=''){const e=document.createElement('div');e.className=`cell ${classes}`;e.textContent=label;return e}
  function render(){
    r.weekLabel.textContent=dateLabel();
    r.schedule.replaceChildren();
    r.schedule.classList.toggle('day',view!=='week');
    r.tabs.querySelectorAll('button').forEach(b=>{const active=b.dataset.view===view;b.classList.toggle('active',active);b.setAttribute('aria-selected',String(active))});
    const shown=view==='week'?[1,2,3,4,5,6,0]:[Number(view)];
    const corner=cell('시간','head-cell corner');corner.style.gridArea='1 / 1';r.schedule.append(corner);
    shown.forEach((day,column)=>{const head=cell(DAYS[day],'head-cell');head.style.gridArea=`1 / ${column+2}`;if(day===new Date().getDay()&&weekOffset===0)head.classList.add('today');r.schedule.append(head)});
    for(let i=0;i<SLOTS;i++){
      const start=START+i*STEP,timeCell=cell(time(start),'time-cell');timeCell.style.gridArea=`${i+2} / 1`;r.schedule.append(timeCell);
      shown.forEach((day,column)=>{const button=document.createElement('button');button.type='button';button.className='cell slot';button.style.gridArea=`${i+2} / ${column+2}`;button.setAttribute('aria-label',`${DAYS[day]}요일 ${time(start)} 일정 추가`);button.addEventListener('click',()=>openEditor(null,day,start));r.schedule.append(button)});
    }
    events.filter(e=>shown.includes(e.day)).forEach(e=>{const button=document.createElement('button');button.type='button';button.className='event';button.style.setProperty('--event-color',e.color||COLORS[1]);button.style.setProperty('--event-row',String(2+(e.start-START)/STEP));button.style.setProperty('--event-span',String((e.end-e.start)/STEP));button.style.setProperty('--event-column',String(2+shown.indexOf(e.day)));const title=document.createElement('strong');title.textContent=e.title;const duration=document.createElement('small');duration.textContent=`${time(e.start)}–${time(e.end)}`;button.append(title,duration);button.title=`${e.title} ${duration.textContent}`;button.addEventListener('click',()=>openEditor(e));r.schedule.append(button)})
  }
  function openEditor(event,day=new Date().getDay(),start=START){editingId=event?.id||null;r.modalTitle.textContent=event?'일정 수정':'일정 추가';r.day.value=String(event?.day??day);r.start.value=String(event?.start??start);r.end.value=String(event?.end??Math.min(start+STEP,END));r.title.value=event?.title||'';hideSuggestions();r.memo.value=event?.memo||'';r.reminder.checked=Boolean(event?.reminder);r.lead.value=String(event?.reminderLead||10);r.leadRow.hidden=!r.reminder.checked;r.del.hidden=!event;r.copy.hidden=!event;r.error.textContent='';selectColor(event?.color||COLORS[1]);r.editor.showModal();r.title.focus()}
  function validate(data){if(!data.title)return '계획을 입력해주세요.';if(data.end<=data.start)return '종료 시간은 시작 시간보다 늦어야 합니다.';if(events.some(e=>e.id!==editingId&&e.day===data.day&&data.start<e.end&&data.end>e.start))return '같은 요일에 겹치는 일정이 있습니다.';return ''}
  function formData(){return {day:Number(r.day.value),start:Number(r.start.value),end:Number(r.end.value),title:r.title.value.trim(),memo:r.memo.value.trim(),reminder:r.reminder.checked,reminderLead:Number(r.lead.value),color:selectedColor}}
  function save(event){event.preventDefault();const data=formData(),error=validate(data);if(error){r.error.textContent=error;return}if(editingId)events=events.map(e=>e.id===editingId?{...e,...data}:e);else events.push({id:globalThis.crypto?.randomUUID?.()||String(Date.now()+Math.random()),...data});recordTitle(data.title);persist();if(data.reminder&&window.Android?.requestAlerts)window.Android.requestAlerts();r.editor.close();render()}
  function showInfo(title,content){$('infoTitle').textContent=title;$('infoContent').replaceChildren(content);r.info.showModal()}
  function widgetInfo(){
    const wrap=document.createElement('div'),picker=document.createElement('div'),note=document.createElement('p'),content=document.createElement('div');
    picker.className='widget-picker';
    const choices=[['today','오늘 일정'],['week','주간 시간표']];
    const renderType=type=>{
      picker.querySelectorAll('button').forEach(button=>button.classList.toggle('active',button.dataset.type===type));
      note.textContent=type==='week'?'월~일 30분 단위 시간표입니다. 위젯 안에서 위아래로 스크롤할 수 있습니다.':'오늘 일정을 시간순으로 나열합니다.';
      const card=document.createElement('div');card.className='widget-card';card.style.setProperty('--widget-alpha',String((100-currentWidgetTransparency())/100));
      const heading=document.createElement('h3');heading.textContent=type==='week'?'주간 시간표':`오늘 일정 · ${DAYS[new Date().getDay()]}요일`;card.append(heading);
      if(type==='today'){
        const todays=events.filter(event=>event.day===new Date().getDay()).sort((a,b)=>a.start-b.start);
        if(!todays.length){const empty=document.createElement('p');empty.textContent='등록된 일정이 없습니다.';card.append(empty)}
        todays.forEach(event=>{const row=document.createElement('div');row.className='widget-item';const dot=document.createElement('span');dot.className='widget-dot';dot.style.background=event.color;const label=document.createElement('span');label.textContent=`${time(event.start)}–${time(event.end)}  ${event.title}`;row.append(dot,label);card.append(row)});
      }else{
        const scroll=document.createElement('div');scroll.className='widget-week-scroll';
        const grid=document.createElement('div');grid.className='widget-week-grid';
        const order=[1,2,3,4,5,6,0];
        ['시간',...order.map(day=>DAYS[day])].forEach(label=>{const head=document.createElement('div');head.className='widget-week-head';head.textContent=label;grid.append(head)});
        for(let minute=START;minute<END;minute+=STEP){
          const clock=document.createElement('div');clock.className='widget-week-time';clock.textContent=time(minute);grid.append(clock);
          order.forEach(day=>{const box=document.createElement('div');box.className='widget-week-cell';const event=events.find(item=>item.day===day&&item.start<=minute&&minute<item.end);if(event){box.style.setProperty('--event-color',event.color||COLORS[1]);box.classList.add('busy');const midpoint=event.start+Math.floor((event.end-event.start)/STEP/2)*STEP;if(minute===midpoint)box.textContent=event.title}grid.append(box)});
        }
        scroll.append(grid);card.append(scroll);
      }
      content.replaceChildren(card);
    };
    choices.forEach(([type,label])=>{const button=document.createElement('button');button.type='button';button.dataset.type=type;button.textContent=label;button.addEventListener('click',()=>renderType(type));picker.append(button)});
    wrap.append(picker,note,content);renderType('week');
    const help=document.createElement('p');help.textContent='안드로이드 홈 화면의 빈 곳을 길게 누른 뒤 위젯 목록에서 ‘오늘 일정’ 또는 ‘주간 시간표’를 선택하세요.';wrap.append(help);
    showInfo('위젯 미리보기',wrap);
  }
  function currentFont(){try{return window.Android?.getFont?.()||localStorage.getItem('timetable.font.v1')||'pretendard'}catch{return 'pretendard'}}
  function preference(key,nativeGetter,fallback){try{return window.Android?.[nativeGetter]?.()??localStorage.getItem(key)??fallback}catch{return fallback}}
  function currentTheme(){return preference('timetable.theme.v1','getTheme','dark')==='light'?'light':'dark'}
  function currentWidgetTransparency(){const value=Number(preference('timetable.widgetTransparency.v1','getWidgetTransparency','0'));return Number.isFinite(value)?Math.max(0,Math.min(100,value)):0}
  function setTheme(theme){const chosen=theme==='light'?'light':'dark';document.documentElement.dataset.theme=chosen;document.querySelector('meta[name="theme-color"]').content=chosen==='light'?'#f6f7fb':'#101114';try{localStorage.setItem('timetable.theme.v1',chosen)}catch{}if(window.Android?.setTheme)window.Android.setTheme(chosen)}
  function setWidgetTransparency(value){const amount=Math.max(0,Math.min(100,Number(value)||0));try{localStorage.setItem('timetable.widgetTransparency.v1',String(amount))}catch{}if(window.Android?.setWidgetTransparency)window.Android.setWidgetTransparency(amount)}
  function settingsInfo(){const wrap=document.createElement('div');wrap.className='settings-content';const p=document.createElement('p');p.textContent='일정은 이 기기에 저장되며 같은 요일에 매주 반복 표시됩니다.';wrap.append(p);
    const themeLabel=document.createElement('label');themeLabel.textContent='화면 모드';const themeSelect=document.createElement('select');[['dark','다크 모드'],['light','라이트 모드']].forEach(([value,name])=>themeSelect.append(option(value,name)));themeSelect.value=currentTheme();themeSelect.addEventListener('change',()=>setTheme(themeSelect.value));themeLabel.append(themeSelect);wrap.append(themeLabel);
    const fontLabel=document.createElement('label');fontLabel.textContent='글꼴';const fontSelect=document.createElement('select');[['pretendard','프리텐다드'],['jua','배민 주아체']].forEach(([value,name])=>fontSelect.append(option(value,name)));fontSelect.value=currentFont();fontSelect.addEventListener('change',()=>setFont(fontSelect.value));fontLabel.append(fontSelect);wrap.append(fontLabel);
    const opacityLabel=document.createElement('label');const opacityText=document.createElement('span');const slider=document.createElement('input');slider.type='range';slider.min='0';slider.max='100';slider.step='5';slider.value=String(currentWidgetTransparency());slider.setAttribute('aria-label','위젯 배경 투명도');opacityLabel.append(opacityText,slider);const updateOpacity=()=>{opacityText.textContent=`위젯 배경 투명도 ${slider.value}%`;slider.style.setProperty('--widget-alpha',String((100-Number(slider.value))/100))};slider.addEventListener('input',updateOpacity);slider.addEventListener('change',()=>setWidgetTransparency(slider.value));updateOpacity();wrap.append(opacityLabel);
    const note=document.createElement('p');note.textContent='투명도는 위젯 배경에만 적용되며 글자와 일정 색상은 유지됩니다.';wrap.append(note);const p2=document.createElement('p');p2.textContent='안드로이드 알림 권한과 정확한 알람 권한이 허용되어야 시작 전 알림을 제시간에 받을 수 있습니다.';wrap.append(p2);showInfo('설정',wrap)}
  function setFont(font){const chosen=font==='jua'?'jua':'pretendard';document.documentElement.dataset.font=chosen;try{localStorage.setItem('timetable.font.v1',chosen)}catch{}if(window.Android?.setFont)window.Android.setFont(chosen)}
  populateInputs();selectColor(selectedColor);setFont(currentFont());setTheme(currentTheme());if(titles.length&&window.Android?.syncTitles)window.Android.syncTitles(JSON.stringify(titles));render();r.form.addEventListener('submit',save);r.title.addEventListener('input',renderSuggestions);r.title.addEventListener('focus',renderSuggestions);r.title.addEventListener('keydown',suggestionKeydown);r.title.addEventListener('blur',()=>setTimeout(hideSuggestions,120));r.reminder.addEventListener('change',()=>{r.leadRow.hidden=!r.reminder.checked;if(r.reminder.checked&&window.Android?.requestAlerts)window.Android.requestAlerts()});$('closeEditor').addEventListener('click',()=>r.editor.close());$('closeInfo').addEventListener('click',()=>r.info.close());r.del.addEventListener('click',()=>{if(!editingId)return;events=events.filter(e=>e.id!==editingId);persist();r.editor.close();render()});r.copy.addEventListener('click',()=>{if(!events.find(e=>e.id===editingId))return;editingId=null;r.modalTitle.textContent='일정 복사';r.del.hidden=true;r.copy.hidden=true;r.error.textContent='복사할 요일과 시간을 선택해주세요.'});r.tabs.addEventListener('click',e=>{const button=e.target.closest('button[data-view]');if(button){view=button.dataset.view;render()}});$('prevWeek').addEventListener('click',()=>{weekOffset--;render()});$('nextWeek').addEventListener('click',()=>{weekOffset++;render()});$('todayButton').addEventListener('click',()=>{weekOffset=0;view=String(new Date().getDay());render()});$('widgetNav').addEventListener('click',widgetInfo);$('settingsNav').addEventListener('click',settingsInfo);$('scheduleNav').addEventListener('click',()=>{r.info.close();view='week';render()});if('serviceWorker'in navigator&&location.protocol!=='file:')navigator.serviceWorker.register('./sw.js').catch(()=>{});
})();
