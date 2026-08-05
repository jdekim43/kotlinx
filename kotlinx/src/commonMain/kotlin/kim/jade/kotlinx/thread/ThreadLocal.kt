package kim.jade.kotlinx.thread

expect class ThreadLocal<T>() {

    fun get(): T?

    fun set(obj: T?)

    fun remove()
}

var <T> ThreadLocal<T>.value: T?
    get() = get()
    set(value) = set(value)
