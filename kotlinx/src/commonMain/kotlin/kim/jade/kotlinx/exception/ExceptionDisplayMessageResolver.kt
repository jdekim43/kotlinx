package kim.jade.kotlinx.exception

expect fun loadDefaultResolver(): ExceptionDisplayMessageResolver

interface ExceptionDisplayMessageResolver {

    companion object : ExceptionDisplayMessageResolver {

        var defaultLocale: Locale = Locale.from("en", "US")

        private var instance: ExceptionDisplayMessageResolver = loadDefaultResolver()

        fun use(resolver: ExceptionDisplayMessageResolver) {
            this.instance = resolver
        }

        override fun getMessage(
            errorCode: ErrorCode,
            locale: Locale
        ): String = instance.getMessage(errorCode, locale)

        override fun getMessage(exception: Throwable, locale: Locale): String = instance.getMessage(exception, locale)
    }

    fun getMessage(errorCode: ErrorCode, locale: Locale = defaultLocale): String

    fun getMessage(exception: Throwable, locale: Locale = defaultLocale): String
}

open class StaticExceptionDisplayMessageResolver : ExceptionDisplayMessageResolver {

    companion object {
        const val DEFAULT_KEY = "default"
    }

    private val defaultMessageMap: Map<Locale, String> = mapOf(
        Locale.from("ko", "KR") to "오류가 발생했습니다.",
        Locale.from("en", "US") to "An error occurred.",
    )

    private var messageMap: Map<ErrorCode, Map<Locale, String>> = mapOf(
        DEFAULT_KEY to defaultMessageMap
    )

    override fun getMessage(errorCode: ErrorCode, locale: Locale): String {
        val localeMap = messageMap[errorCode] ?: defaultMessageMap

        return localeMap[locale]
            ?: localeMap[ExceptionDisplayMessageResolver.defaultLocale]
            ?: localeMap.values.firstOrNull()
            ?: "An error occurred. (code=$errorCode)"
    }

    override fun getMessage(exception: Throwable, locale: Locale): String {
        if (exception is AppException) {
            return getMessage(exception.code, locale)
        }

        return getMessage(DEFAULT_KEY, locale)
    }

    open fun setMessages(messageMap: Map<ErrorCode, Map<Locale, String>>) {
        val result = this.messageMap.toMutableMap()

        for ((code, messages) in messageMap) {
            val codeMessageMap = result[code]?.toMutableMap() ?: mutableMapOf()
            codeMessageMap.putAll(messages)
            result[code] = codeMessageMap
        }

        this.messageMap = result
    }

    open fun setMessages(code: ErrorCode, messages: Map<Locale, String>) {
        val result = this.messageMap.toMutableMap()
        val codeMessageMap = result[code]?.toMutableMap() ?: mutableMapOf()

        codeMessageMap.putAll(messages)
        result[code] = codeMessageMap

        this.messageMap = result
    }

    open fun setMessages(locale: Locale, messages: Map<ErrorCode, String>) {
        val result = messageMap.toMutableMap()

        for ((code, message) in messages) {
            result[code] = result[code]?.toMutableMap()
                ?.apply { put(locale, message) }
                ?: mapOf(locale to message)
        }

        this.messageMap = result
    }
}
