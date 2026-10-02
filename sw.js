const CACHE = 'timetable-v6';
const FILES = ['./', './index.html', './styles.css', './app.js', './manifest.webmanifest', './icon.svg', './fonts/Pretendard-Regular.woff2', './fonts/BMJUA.ttf'];
self.addEventListener('install', event => {
  event.waitUntil(caches.open(CACHE).then(cache => cache.addAll(FILES)));
});
self.addEventListener('activate', event => {
  event.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(key => key !== CACHE).map(key => caches.delete(key)))));
});
self.addEventListener('fetch', event => {
  if (event.request.method === 'GET' && new URL(event.request.url).origin === location.origin) {
    event.respondWith(caches.match(event.request).then(cached => cached || fetch(event.request)));
  }
});
