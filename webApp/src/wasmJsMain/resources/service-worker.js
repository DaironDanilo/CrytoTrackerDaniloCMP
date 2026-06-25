// Cache name is versioned by build content (see precache-manifest.json), not by a
// hand-bumped string, so a new deploy always gets a fresh shell cache and old ones
// are swept on activate.
const SHELL_CACHE_PREFIX = 'app-shell-';
const API_CACHE_NAME = 'api-cache-v1';
const PRECACHE_MANIFEST_URL = 'precache-manifest.json';
const API_HOSTNAME = 'rest.coincap.io';

async function currentShellCacheName() {
    try {
        const response = await fetch(PRECACHE_MANIFEST_URL, { cache: 'no-store' });
        const { revision } = await response.json();
        return SHELL_CACHE_PREFIX + revision;
    } catch (error) {
        return SHELL_CACHE_PREFIX + 'fallback';
    }
}

self.addEventListener('install', (event) => {
    event.waitUntil(
        (async () => {
            let entries = [];
            let cacheName = SHELL_CACHE_PREFIX + 'fallback';
            try {
                const response = await fetch(PRECACHE_MANIFEST_URL, { cache: 'no-store' });
                const manifest = await response.json();
                entries = manifest.entries || [];
                cacheName = SHELL_CACHE_PREFIX + manifest.revision;
            } catch (error) {
                // First install while offline, or manifest missing: nothing to precache yet,
                // the runtime cache-first handler will fill the cache as pages are visited.
                return;
            }

            const cache = await caches.open(cacheName);
            await Promise.all(
                entries.map(async (url) => {
                    try {
                        const response = await fetch(url, { cache: 'no-store' });
                        if (response.ok) {
                            await cache.put(url, response);
                        }
                    } catch (error) {
                        // Skip individual asset failures; don't fail the whole install.
                    }
                }),
            );
        })(),
    );
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        (async () => {
            const activeShellCache = await currentShellCacheName();
            const keys = await caches.keys();
            await Promise.all(
                keys
                    .filter((key) => key !== activeShellCache && key !== API_CACHE_NAME)
                    .map((key) => caches.delete(key)),
            );
            await self.clients.claim();
        })(),
    );
});

async function cacheFirst(request, cacheName) {
    const cached = await caches.match(request);
    if (cached) {
        return cached;
    }
    const response = await fetch(request);
    if (response.ok) {
        const cache = await caches.open(cacheName);
        cache.put(request, response.clone());
    }
    return response;
}

async function networkFirstNavigation(request) {
    try {
        const response = await fetch(request);
        if (response.ok) {
            const cacheName = await currentShellCacheName();
            const cache = await caches.open(cacheName);
            cache.put('index.html', response.clone());
        }
        return response;
    } catch (error) {
        const cached = await caches.match('index.html');
        if (cached) {
            return cached;
        }
        throw error;
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

    if (request.mode === 'navigate') {
        event.respondWith(networkFirstNavigation(request));
        return;
    }

    event.respondWith(
        currentShellCacheName().then((cacheName) => cacheFirst(request, cacheName)),
    );
});
