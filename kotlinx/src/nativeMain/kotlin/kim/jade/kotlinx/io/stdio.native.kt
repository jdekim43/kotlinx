package kim.jade.kotlinx.io

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.STDOUT_FILENO
import platform.posix.fprintf
import platform.posix.isatty
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
actual fun eprintln(text: String) {
    fprintf(stderr, "%s\n", text)
}

actual fun isTTY(): Boolean {
    return isatty(STDOUT_FILENO) != 0
}