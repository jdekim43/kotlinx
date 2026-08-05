package kim.jade.kotlinx.extension

import kotlin.reflect.KClass

expect val KClass<*>.qualifiedOrSimpleName: String?