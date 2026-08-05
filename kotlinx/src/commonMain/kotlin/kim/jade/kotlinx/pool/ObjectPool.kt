package kim.jade.kotlinx.pool

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndUpdate
import kotlin.concurrent.atomics.updateAndFetch

@OptIn(ExperimentalAtomicApi::class)
class ObjectPool<T>(
    val newInstance: () -> T,
) {

    private class Ref<T>(val item: T) {
        var next: Ref<T>? = null
    }

    private val head = AtomicReference<Ref<T>?>(null)

    fun acquire(): T = head.fetchAndUpdate { current -> current?.next }?.item ?: newInstance()

    inline fun <R> use(crossinline body: (T) -> R): R {
        val obj = acquire()

        return try {
            body(obj)
        } finally {
            release(obj)
        }
    }

    fun release(obj: T) {
        val new = Ref(obj)

        head.updateAndFetch { current ->
            new.next = current
            new
        }
    }

    fun close() {
        head.store(null)
    }
}