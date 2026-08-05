package kim.jade.kotlinx.io

actual fun eprintln(text: String) {
    System.err.println(text)
}

actual fun isTTY(): Boolean {
    return System.console() != null
}