package kim.jade.kotlinx.io

actual fun environmentVariable(name: String): String? {
    return System.getenv(name)
}