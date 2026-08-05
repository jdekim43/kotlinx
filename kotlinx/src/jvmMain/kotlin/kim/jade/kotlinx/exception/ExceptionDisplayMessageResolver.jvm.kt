package kim.jade.kotlinx.exception

import kim.jade.kotlinx.exception.ResourceExceptionDisplayMessageResolver.Companion.DEFAULT_DIRECTORY_PATH
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Logger

actual fun loadDefaultResolver(): ExceptionDisplayMessageResolver = ResourceExceptionDisplayMessageResolver().apply {
    try {
        load(DEFAULT_DIRECTORY_PATH)
    } catch (e: Exception) {
        Logger.getLogger(ResourceExceptionDisplayMessageResolver::class.java.name)
            .warning("Failed to load default exception display message resolver")
    }
}

open class ResourceExceptionDisplayMessageResolver : StaticExceptionDisplayMessageResolver() {

    companion object {
        const val DEFAULT_DIRECTORY_PATH = "messages.exception"
    }

    fun load(resourcePath: String) {
        val uri = ResourceExceptionDisplayMessageResolver::class.java.classLoader.getResource(resourcePath)?.toURI()

        if (uri != null) {
            load(File(uri))
        }
    }

    fun load(directory: File) {
        directory.listFiles { file -> file.extension == "properties" }
            ?.forEach { file ->
                val locale = try {
                    Locale.from(file.name)
                } catch (e: IllegalArgumentException) {
                    return@forEach
                }
                val messages = file.inputStream().use { Properties().apply { load(InputStreamReader(it)) } }

                load(locale, messages)
            }
    }

    fun load(locale: Locale, file: File) {
        file.inputStream()
            .use { Properties().apply { load(InputStreamReader(it)) } }
            .let { load(locale, it) }
    }

    fun load(locale: Locale, inputStream: InputStream) {
        load(locale, inputStream.use { Properties().apply { load(InputStreamReader(it)) } })
    }

    open fun load(locale: Locale, messages: Properties) {
        setMessages(locale, messages.map { it.key.toString() to it.value.toString() }.toMap())
    }
}
