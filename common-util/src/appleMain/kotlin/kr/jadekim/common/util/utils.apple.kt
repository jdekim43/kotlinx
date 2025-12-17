package kr.jadekim.common.util

import platform.Foundation.NSUUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

actual fun generateUUID(): String = NSUUID().UUIDString()

@OptIn(ExperimentalTime::class)
actual fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
