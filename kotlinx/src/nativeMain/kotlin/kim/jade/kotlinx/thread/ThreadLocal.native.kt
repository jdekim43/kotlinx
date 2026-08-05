package kim.jade.kotlinx.thread

import kotlin.concurrent.AtomicInt
import kotlin.native.concurrent.ThreadLocal

actual class ThreadLocal<T> {

    private val threadLocalId = ThreadLocalIdCounter.nextThreadLocalId()

    @Suppress("UNCHECKED_CAST")
    actual fun get(): T? = if (ThreadLocalState.threadLocalMap.containsKey(threadLocalId)) {
        ThreadLocalState.threadLocalMap[threadLocalId] as T
    } else {
        null
    }

    actual fun set(obj: T?) {
        if (obj == null) {
            remove()
        } else {
            ThreadLocalState.threadLocalMap[threadLocalId] = obj
        }
    }

    actual fun remove() {
        ThreadLocalState.threadLocalMap.remove(threadLocalId)
    }
}

@ThreadLocal
private object ThreadLocalState {
    val threadLocalMap = HashMap<Int, Any>()
}

private object ThreadLocalIdCounter {
    val threadLocalId = AtomicInt(0)
    fun nextThreadLocalId(): Int = threadLocalId.addAndGet(1)
}