package kim.jade.kotlinx.util

import kotlin.concurrent.thread

fun addShutdownHook(body: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(thread(start = false, block = body))
}