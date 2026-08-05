package kim.jade.kotlinx.thread

actual fun currentThreadName(): String? = Thread.currentThread().name