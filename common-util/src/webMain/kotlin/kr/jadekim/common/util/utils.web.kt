package kr.jadekim.common.util

import web.crypto.crypto
import kotlin.js.Date

actual fun generateUUID(): String = crypto.randomUUID()

actual fun currentTimeMillis(): Long = Date.now().toLong()
