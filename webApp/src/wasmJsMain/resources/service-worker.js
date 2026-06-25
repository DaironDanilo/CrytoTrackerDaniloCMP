// Substituted by the generatePwaPrecacheManifest Gradle task (see webApp/build.gradle.kts)
// with a hash of the build output, so this file's own bytes - and therefore the cache name -
// change on every deploy. That's what makes the browser notice a new worker and update it.
const BUILD_REVISION = '__BUILD_REVISION__';
const SHELL_CACHE_NAME = `app-shell-${BUILD_REVISION}`;
const API_CACHE_NAME = 'api-cache-v1';
const PRECACHE_MANIFEST_URL = 'precache-manifest.json';
const API_HOSTNAME = 'rest.coincap.io';

// Zero external dependencies (no images, no linked CSS) so it always renders even if this
// is the very first offline visit, before the shell itself has finished caching.
const OFFLINE_FALLBACK_HTML = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>CryptoTracker - Offline</title>
<style>
  html, body { height: 100%; margin: 0; }
  body {
    display: flex; align-items: center; justify-content: center; text-align: center;
    background: #101410; color: #e6f1e6; font-family: system-ui, sans-serif; padding: 24px;
  }
  div { max-width: 320px; }
  h1 { font-size: 1.25rem; margin-bottom: 8px; }
  p { opacity: 0.8; line-height: 1.4; }
</style>
</head>
<body>
  <div>
    <h1>You're offline</h1>
    <p>CryptoTracker needs one successful connection to finish setting up offline access. Reconnect and reopen the app once, then it will keep working offline.</p>
  </div>
</body>
</html>`;

async function cacheAll(cache, entries) {
    await Promise.all(
        entries.map(async ({ url, immutable }) => {
            try {
                // Content-hashed (.wasm) assets are already warm in the browser's HTTP
                // cache from the page's own boot - reuse that instead of paying for an
                // extra multi-megabyte download. Everything else has a fixed name that
                // can go stale, so it must bypass the cache.
                const response = await fetch(url, immutable ? {} : { cache: 'no-store' });
                if (response.ok) {
                    await cache.put(url, response);
                }
            } catch (error) {
                // Skip individual asset failures; don't fail the whole install.
            }
        }),
    );
}

self.addEventListener('install', (event) => {
    event.waitUntil(
        (async () => {
            const cache = await caches.open(SHELL_CACHE_NAME);
            await cache.put('offline.html', new Response(OFFLINE_FALLBACK_HTML, { headers: { 'Content-Type': 'text/html' } }));

            let manifest;
            try {
                const response = await fetch(PRECACHE_MANIFEST_URL, { cache: 'no-store' });
                manifest = await response.json();
            } catch (error) {
                // First install while offline: nothing more we can precache yet.
                return;
            }

            // Critical app-shell files first and awaited on their own, so the app is
            // guaranteed bootable offline even if the worker gets killed before the much
            // larger (but non-essential) bulk asset set below finishes caching.
            await cacheAll(cache, manifest.critical || []);
            await cacheAll(cache, manifest.bulk || []);
        })(),
    );
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        (async () => {
            const keys = await caches.keys();
            await Promise.all(
                keys
                    .filter((key) => key !== SHELL_CACHE_NAME && key !== API_CACHE_NAME)
                    .map((key) => caches.delete(key)),
            );
            await self.clients.claim();
        })(),
    );
});

async function cacheFirst(request) {
    const cached = await caches.match(request);
    if (cached) {
        return cached;
    }
    const response = await fetch(request);
    if (response.ok) {
        const cache = await caches.open(SHELL_CACHE_NAME);
        cache.put(request, response.clone());
    }
    return response;
}

async function navigationHandler(request) {
    try {
        const response = await fetch(request);
        if (response.ok) {
            const cache = await caches.open(SHELL_CACHE_NAME);
            cache.put('index.html', response.clone());
        }
        return response;
    } catch (error) {
        const cached = await caches.match('index.html');
        if (cached) {
            return cached;
        }
        const fallback = await caches.match('offline.html');
        return fallback || new Response(OFFLINE_FALLBACK_HTML, { status: 503, headers: { 'Content-Type': 'text/html' } });
    }
}

async function staleWhileRevalidate(request) {
    const cache = await caches.open(API_CACHE_NAME);
    const cached = await cache.match(request);

    const networkUpdate = fetch(request)
        .then((response) => {
            if (response.ok) {
                cache.put(request, response.clone());
            }
            return response;
        })
        .catch(() => null);

    if (cached) {
        // Refresh the cache in the background; the caller gets the last-known data now.
        networkUpdate.catch(() => {});
        return cached;
    }

    const networkResponse = await networkUpdate;
    if (networkResponse) {
        return networkResponse;
    }

    return new Response(
        JSON.stringify({ error: 'offline', message: 'No cached data available.' }),
        { status: 503, headers: { 'Content-Type': 'application/json' } },
    );
}

self.addEventListener('fetch', (event) => {
    const { request } = event;
    if (request.method !== 'GET') {
        return;
    }

    const url = new URL(request.url);

    if (url.hostname === API_HOSTNAME) {
        event.respondWith(staleWhileRevalidate(request));
        return;
    }

    if (url.origin !== self.location.origin) {
        return;
    }

    // respondWith() must never receive a rejected promise - that surfaces to the browser
    // as a hard navigation failure (net::ERR_FAILED) instead of falling back gracefully.
    if (request.mode === 'navigate') {
        event.respondWith(navigationHandler(request).catch(() => new Response(OFFLINE_FALLBACK_HTML, { status: 503, headers: { 'Content-Type': 'text/html' } })));
        return;
    }

    event.respondWith(cacheFirst(request).catch(() => new Response('', { status: 504 })));
});
