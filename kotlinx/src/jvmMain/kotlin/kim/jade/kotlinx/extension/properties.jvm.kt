package kim.jade.kotlinx.extension

import java.io.File
import java.io.InputStream
import java.util.*

fun Properties.getBoolean(key: String): Boolean? = getProperty(key)?.toBoolean()

fun Properties.getBoolean(key: String, defaultValue: Boolean): Boolean = getBoolean(key) ?: defaultValue

fun Properties.getShort(key: String): Short? = getProperty(key)?.toShort()

fun Properties.getShort(key: String, defaultValue: Short): Short = getShort(key) ?: defaultValue

fun Properties.getInt(key: String): Int? = getProperty(key)?.toInt()

fun Properties.getInt(key: String, defaultValue: Int): Int = getInt(key) ?: defaultValue

fun Properties.getLong(key: String): Long? = getProperty(key)?.toLong()

fun Properties.getLong(key: String, defaultValue: Long): Long = getLong(key) ?: defaultValue

fun Properties.getFloat(key: String): Float? = getProperty(key)?.toFloat()

fun Properties.getFloat(key: String, defaultValue: Float): Float = getFloat(key) ?: defaultValue

fun Properties.getDouble(key: String): Double? = getProperty(key)?.toDouble()

fun Properties.getDouble(key: String, defaultValue: Double): Double = getDouble(key) ?: defaultValue

fun Properties.getString(key: String): String? = getProperty(key)

fun Properties.getString(key: String, defaultValue: String): String = getString(key) ?: defaultValue

fun Properties.load(vararg files: File, includeSystemProperties: Boolean = false) {
    if (includeSystemProperties) {
        loadFromSystem()
    }

    return files
        .filter { it.canRead() }
        .map { it.inputStream() }
        .let { load(it) }
}

fun Properties.load(inputStreams: Iterable<InputStream>) {
    inputStreams.forEach { source ->
        source.use { load(it) }
    }
}

/**
 * e.g.loadFileBasedProperties(File("/config"))
 * /config/key1/key2 => key1.key2=[key2 file contents]
 * /config/key1/key3 => key1.key3=[key3 file contents]
 */
fun Properties.loadFileBasedProperties(directory: File, prefix: String = "") {
    if (!directory.isDirectory) {
        return
    }

    val files = directory.listFiles() ?: return

    for (file in files) {
        if (file.isDirectory) {
            loadFileBasedProperties(file, prefix + file.name + '.')
            continue
        }

        put(prefix + file.name, file.readText())
    }
}

fun Properties.loadFromSystem(environmentVariables: Boolean = true, properties: Boolean = true) {
    if (environmentVariables) {
        putAll(System.getenv())
    }
    if (properties) {
        putAll(System.getProperties())
    }
}