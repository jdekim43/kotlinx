package kim.jade.kotlinx.extension

fun Boolean.toBinaryInt(): Int {
    return if (this) 1 else 0
}