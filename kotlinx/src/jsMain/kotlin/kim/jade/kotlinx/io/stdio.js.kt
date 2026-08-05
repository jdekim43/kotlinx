package kim.jade.kotlinx.io

actual fun eprintln(text: String) {
    console.error(text)
}

actual fun isTTY(): Boolean {
    return false
}
