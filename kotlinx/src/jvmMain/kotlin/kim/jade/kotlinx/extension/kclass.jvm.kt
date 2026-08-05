package kim.jade.kotlinx.extension

import kotlin.reflect.KClass

actual val KClass<*>.qualifiedOrSimpleName: String?
    get() = qualifiedName ?: java.canonicalName ?: java.name ?: simpleName ?: java.simpleName