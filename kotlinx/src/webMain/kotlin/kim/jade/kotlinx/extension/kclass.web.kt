package kim.jade.kotlinx.extension

import kotlin.reflect.KClass

actual val KClass<*>.qualifiedOrSimpleName: String?
    get() = simpleName