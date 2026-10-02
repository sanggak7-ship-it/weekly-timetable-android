// Preview-only sample data. The Android app does not load this file.
const sampleEvents = [
  [0, 480, 540, '아침 운동', '#68b5b9'],
  [0, 600, 690, '독서', '#b8a5da'],
  [0, 900, 990, '수학 공부', '#d9bb70'],
  [1, 540, 600, '주간 계획', '#91b7e5'],
  [1, 660, 750, '프로젝트', '#93c98a'],
  [1, 990, 1050, '요가', '#df91ae'],
  [2, 480, 570, '영어 회화', '#8b9bf0'],
  [2, 780, 870, '디자인 작업', '#edaa87'],
  [2, 1080, 1140, '저녁 산책', '#68b5b9'],
  [3, 540, 630, '집중 업무', '#91b7e5'],
  [3, 870, 960, '자료 정리', '#d9bb70'],
  [4, 480, 540, '스트레칭', '#93c98a'],
  [4, 660, 750, '팀 미팅', '#b8a5da'],
  [4, 1020, 1110, '운동', '#df91ae'],
  [5, 600, 690, '아이디어 정리', '#edaa87'],
  [5, 840, 930, '카페에서 작업', '#91b7e5'],
  [6, 570, 660, '장보기', '#d9bb70'],
  [6, 900, 1020, '자유 시간', '#93c98a']
].map(([day,start,end,title,color],index) => ({
  id: `sample-${index}`, day, start, end, title, color,
  memo: '', reminder: false, reminderLead: 10
}));

window.Android = {
  getEvents: () => JSON.stringify(sampleEvents),
  getTitles: () => JSON.stringify(sampleEvents.map(event => event.title)),
  getFont: () => 'pretendard',
  getTheme: () => 'dark',
  getWidgetTransparency: () => 15,
  syncEvents: () => {},
  syncTitles: () => {},
  setFont: () => {},
  setTheme: () => {},
  setWidgetTransparency: () => {},
  requestAlerts: () => {}
};

const previewStyle = document.createElement('style');
previewStyle.textContent = `
  body { max-width: 430px; margin: 0 auto; box-shadow: 0 0 50px #0008; }
  .app { max-width: 430px; }
  .bottom-nav { max-width: 430px; }
  .grid-scroll { max-height: calc(100dvh - 260px); }
  .schedule { grid-template-columns: 42px repeat(var(--cols), minmax(0,1fr)); min-width: 0; }
  .schedule.day { grid-template-columns: 42px 1fr; }
  .event { padding: 2px 1px; }
  .event strong { font-size: 9px; }
  .event small { display: none; }
  .time-cell { font-size: 10px; }
`;
document.head.append(previewStyle);
