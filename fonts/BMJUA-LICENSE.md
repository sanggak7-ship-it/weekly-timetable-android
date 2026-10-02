# 배민 주아체

[배포처 바로가기](http://font.woowahan.com/jua/)

&nbsp;

## 웹 폰트

사용하는 `font-family`의 이름은 `BM JUA`입니다.

### HTML

```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/BMJUA.css" type="text/css"/>
```

### CSS `@Import`

```css
@import url('https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/BMJUA.css');
```

### CSS `@font-face`

```css
@font-face {
    font-family: 'BM JUA';
    font-weight: normal;
    font-style: normal;
    font-display: swap;
    src: url('https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/BMJUA.woff2') format('woff2'),
         url('https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/BMJUA.woff') format('woff'),
         url('https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/BMJUA.otf') format('opentype'),
         url('https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/BMJUA.ttf') format('truetype');
}
```

&nbsp;

## 다이나믹 서브셋

웹폰트의 최적화를 위해 모던 브라우저에서는 글리프를 여러개로 나누어 필요한 부분만 동적으로 파싱하는 다이나믹 서브셋을 제공합니다. 폰트의 용량이 부담된다면 아래 코드를 사용하는 걸 추천합니다.

### HTML

```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/subsets/BMJUA-dynamic-subset.css" type="text/css"/>
```

### CSS

```css
@import url('https://cdn.jsdelivr.net/gh/fonts-archive/BMJUA/subsets/BMJUA-dynamic-subset.css');
```

&nbsp;

## font-family

어느 브라우저나 시스템 환경에서도 동일한 폰트가 적용되어야 한다면 아래와 같이 구성하는 걸 추천합니다. `-apple-system`과 `BlinkMacSystemFont`는 맥, `Segoe UI`는 윈도우, `Roboto`는 안드로이드의 기본 폰트입니다.



```css
font-family: "BM JUA", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
```

&nbsp;

## 라이선스

라이선스는 언제든지 변경될 수 있습니다. 변경사항을 확인하려면 배포처를 방문해 주세요.

```
배달의민족에서 무료로 배포하는 글꼴은(한나체·주아체·도현체·연성체·기랑해랑체·한나체 Air·한나체 Pro·을지로체·배민 을지로10년후체, 을지로 오래오래체, 글림체) 자유롭게 수정·변경하여 영리적·비영리적 목적으로 개인 및 기업 사용자가 모두 사용하실 수 있습니다.

다만, 글꼴 폰트 파일(otf/ttf)자체를 유상으로 판매하는 것은 금지하고 있으니 유의 부탁드립니다.

- 사용 가능 범위 : 간판, 현수막, 브로슈어, 포스터, 책, 잡지, 명함, 스티커, 패키지, 제품, 웹페이지, 광고 배너, E브로슈어, 영상 자막, 영화, TV광고, 웹툰, 게임 UI, 앱 UI, 뉴스레터, 웹진, 프리젠테이션, E-book, 메신저 스티커, 로고, 마크, 신문 광고, 잡지 광고, 지면 광고, 컵홀더, 부채, 쇼핑백, 컵홀더, 포장지, 핸드폰 케이스, 노트, 가방, 신발, 옷 등 상업적·비상업적 목적의 제작물 등
- 사용 불가능 범위 : 폰트파일(otf/ttf)의 유료 판매
배민에서 무료로 배포하는 글꼴(한나체·주아체·도현체·연성체·기랑해랑체·한나체 Air·한나체 Pro· 을지로체·배민 을지로10년후체·을지로 오래오래체·글림체)를 사용한 인쇄물, 광고물(온라인 포함)등의 이미지는 당사의 자료 수집 및 연구 목적으로 활용될 수 있습니다. 이를 원치 않는 사용자는 언제든지 당사 고객센터(1600-0987 / CS@woowahan.com)로 요청 부탁드립니다.

아래 “지식재산권 안내 및 라이선스” 전문을 표시하는 경우 다른 소프트웨어와 번들, 재배포 또는 판매하실 수 있습니다.
```
