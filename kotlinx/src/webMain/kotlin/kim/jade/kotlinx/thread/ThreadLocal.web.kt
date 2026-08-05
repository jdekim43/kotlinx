package kim.jade.kotlinx.thread

actual class ThreadLocal<T> {

    private var value: T? = null

    actual fun get(): T? = value

    actual fun set(obj: T?) {
        value = obj
    }

    actual fun remove() {
        value = null
    }
}