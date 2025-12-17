package kr.jadekim.common.util

import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread

actual fun generateUUID(): String = UUID.randomUUID().toString()

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

fun shutdownHook(block: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(thread(start = false, block = block))
}

fun Properties.load(vararg files: File, includeSystemProperties: Boolean = false) {
    if (includeSystemProperties) {
        loadFromSystem()
    }

    return files
        .filter { it.canRead() }
        .map { it.inputStream() }
        .let { load(it, false) }
}

fun Properties.load(
    inputStreams: Iterable<InputStream>,
    includeSystemProperties: Boolean = false,
) {
    if (includeSystemProperties) {
        loadFromSystem()
    }

    inputStreams.forEach { source ->
        source.use { load(it) }
    }
}

fun Properties.loadFromFileTree(directory: File, prefix: String = "") {
    if (!directory.isDirectory) {
        return
    }

    val files = directory.listFiles() ?: return

    for (file in files) {
        if (file.isDirectory) {
            loadFromFileTree(file, prefix + file.name + '.')
            continue
        }

        put(prefix + file.name, file.readText())
    }
}

private fun Properties.loadFromSystem(environmentVariables: Boolean = true, properties: Boolean = true) {
    if (environmentVariables) {
        putAll(System.getenv())
    }
    if (properties) {
        putAll(System.getProperties())
    }
}