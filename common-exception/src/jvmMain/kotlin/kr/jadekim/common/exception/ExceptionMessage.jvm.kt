package kr.jadekim.common.exception

import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Logger

var EXCEPTION_MESSAGE_RESOURCE_PATH = "messages.exception"

actual fun loadPlatformMessages() {
    try {
        val uri = ExceptionMessage::class.java.classLoader.getResource(EXCEPTION_MESSAGE_RESOURCE_PATH)?.toURI()

        if (uri != null) {
            ExceptionMessage.loadErrorMessage(File(uri))
        }
    } catch (e: Exception) {
        Logger.getLogger("kr.jadekim.common.exception.ExceptionMessage").warning("Failed to loadPlatformMessages")
    }
}

fun ExceptionMessage.loadErrorMessage(directory: File) {
    directory.readMessageResources().forEach {
        it.loadMessagesTo(messageMap)
    }
}

fun ExceptionMessage.loadErrorMessage(language: Language, file: File) {
    file.inputStream()
        .use { Properties().apply { load(InputStreamReader(it)) } }
        .let { loadErrorMessage(language, it) }
}

fun ExceptionMessage.loadErrorMessage(language: Language, inputStream: InputStream) {
    loadErrorMessage(language, inputStream.use { Properties().apply { load(InputStreamReader(it)) } })
}

fun ExceptionMessage.loadErrorMessage(language: Language, properties: Properties) {
    Pair(language, properties).loadMessagesTo(messageMap)
}

private fun File.readMessageResources() = listFiles { file -> file.extension == "properties" }
    ?.map {
        it.name.toLanguage() to it.inputStream().use {
            Properties().apply { load(InputStreamReader(it)) }
        }
    }
    ?: emptyList()

private fun String.toLanguage(): Language = substringBefore(".", "")

private fun Pair<Language, Properties>.loadMessagesTo(messageMap: MessageMap): MessageMap {
    second.forEach { messageName, message ->
        val languageMap = messageMap.getOrPut(messageName.toString()) { mutableMapOf() }
        languageMap[first] = message.toString()
    }

    return messageMap
}