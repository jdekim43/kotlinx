package kim.jade.kotlinx.exception

open class AppException(
    val code: String,
    message: String? = null,
    override val cause: Throwable? = null,
    val level: ExceptionLevel = ExceptionLevel.ERROR,
    val data: Map<String, String?> = emptyMap(),
) : Exception() {

    override val message: String? = "[$code] ${message ?: this::class.simpleName}"

    open fun getDisplayMessage(locale: Locale): String = ExceptionDisplayMessageResolver.getMessage(code, locale)
}