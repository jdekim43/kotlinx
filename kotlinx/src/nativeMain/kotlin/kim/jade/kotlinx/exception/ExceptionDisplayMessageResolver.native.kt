package kim.jade.kotlinx.exception

actual fun loadDefaultResolver(): ExceptionDisplayMessageResolver = StaticExceptionDisplayMessageResolver()