package com.cryptodanilo.project.core.database

import org.w3c.dom.Worker

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => new Worker(new URL('sqlite-wasm-worker/worker.js', import.meta.url), { type: 'module' })")
private external fun createSQLiteWorkerJs(): Worker

internal fun createSQLiteWorker(): Worker = createSQLiteWorkerJs()
