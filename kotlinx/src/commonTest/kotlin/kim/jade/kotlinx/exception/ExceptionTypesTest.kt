package kim.jade.kotlinx.exception

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ExceptionTypesTest : DescribeSpec({

    describe("Locale.from") {
        context("given valid locale components") {
            withData(localeNormalizationCases) { case ->
                it("normalizes language, script, and region casing") {
                    case.create().value shouldBe case.expected
                }
            }
        }

        context("given malformed locale components") {
            withData(invalidLocaleCases) { case ->
                it("rejects the locale with a descriptive message") {
                    shouldThrow<IllegalArgumentException> { case.create() }
                        .message shouldContain case.expectedMessage
                }
            }
        }
    }

    describe("StaticExceptionDisplayMessageResolver") {
        val ko = Locale.from("ko", "KR")
        val en = Locale.from("en", "US")
        val fr = Locale.from("fr", "FR")

        context("when no message is registered") {
            it("returns built-in defaults for codes and ordinary throwables") {
                val resolver = StaticExceptionDisplayMessageResolver()

                resolver.getMessage("unknown", ko) shouldBe "오류가 발생했습니다."
                resolver.getMessage("unknown", en) shouldBe "An error occurred."
                resolver.getMessage(IllegalStateException("internal"), en) shouldBe "An error occurred."
            }
        }

        context("when messages exist in different locales") {
            it("uses the requested locale, default locale, then first available message") {
                val previousDefault = ExceptionDisplayMessageResolver.defaultLocale
                try {
                    val resolver = StaticExceptionDisplayMessageResolver()
                    resolver.setMessages("custom", mapOf(ko to "사용자 메시지", en to "User message"))

                    resolver.getMessage("custom", ko) shouldBe "사용자 메시지"
                    resolver.getMessage("custom", fr) shouldBe "User message"

                    ExceptionDisplayMessageResolver.defaultLocale = fr
                    val koreanOnly = StaticExceptionDisplayMessageResolver().apply {
                        setMessages("korean-only", mapOf(ko to "한국어만"))
                    }
                    koreanOnly.getMessage("korean-only", en) shouldBe "한국어만"
                } finally {
                    ExceptionDisplayMessageResolver.defaultLocale = previousDefault
                }
            }
        }

        context("when messages are registered through different overloads") {
            it("merges locales and codes rather than replacing them") {
                val resolver = StaticExceptionDisplayMessageResolver()
                resolver.setMessages(mapOf("first" to mapOf(en to "First")))
                resolver.setMessages("first", mapOf(ko to "첫 번째"))
                resolver.setMessages(fr, mapOf("first" to "Première", "second" to "Deuxième"))

                resolver.getMessage("first", en) shouldBe "First"
                resolver.getMessage("first", ko) shouldBe "첫 번째"
                resolver.getMessage("first", fr) shouldBe "Première"
                resolver.getMessage("second", fr) shouldBe "Deuxième"
            }
        }

        context("when resolving an AppException") {
            it("looks up the message by error code") {
                val resolver = StaticExceptionDisplayMessageResolver().apply {
                    setMessages("APP-1", mapOf(en to "Readable application error"))
                }

                resolver.getMessage(AppException("APP-1"), en) shouldBe "Readable application error"
            }
        }
    }

    describe("ExceptionDisplayMessageResolver") {
        context("when a global resolver is installed") {
            it("delegates code, throwable, and AppException display lookups") {
                val previousDefault = ExceptionDisplayMessageResolver.defaultLocale
                val en = Locale.from("en", "US")
                val ko = Locale.from("ko", "KR")
                val resolver = object : ExceptionDisplayMessageResolver {
                    override fun getMessage(errorCode: ErrorCode, locale: Locale) =
                        "$errorCode@${locale.value}"

                    override fun getMessage(exception: Throwable, locale: Locale) =
                        "${exception::class.simpleName}@${locale.value}"
                }

                try {
                    ExceptionDisplayMessageResolver.defaultLocale = ko
                    ExceptionDisplayMessageResolver.use(resolver)

                    ExceptionDisplayMessageResolver.getMessage("GLOBAL") shouldBe "GLOBAL@ko-KR"
                    ExceptionDisplayMessageResolver.getMessage(IllegalStateException(), en) shouldBe
                        "IllegalStateException@en-US"
                    AppException("APP-DISPLAY").getDisplayMessage(en) shouldBe "APP-DISPLAY@en-US"
                } finally {
                    ExceptionDisplayMessageResolver.defaultLocale = previousDefault
                    ExceptionDisplayMessageResolver.use(loadDefaultResolver())
                }
            }
        }
    }

    describe("AppException") {
        context("when every optional attribute is supplied") {
            it("exposes its code, formatted message, cause, level, and data") {
                val cause = IllegalStateException("root")
                val exception = AppException(
                    code = "APP-42",
                    message = "failed",
                    cause = cause,
                    level = ExceptionLevel.WARNING,
                    data = mapOf("requestId" to "r-1", "optional" to null),
                )

                exception.code shouldBe "APP-42"
                exception.message shouldBe "[APP-42] failed"
                exception.cause shouldBe cause
                exception.level shouldBe ExceptionLevel.WARNING
                exception.data shouldBe mapOf("requestId" to "r-1", "optional" to null)
            }
        }

        context("when no message is supplied") {
            it("uses the concrete exception class name") {
                class SpecificException : AppException("SPECIFIC")

                SpecificException().message shouldBe "[SPECIFIC] SpecificException"
            }
        }
    }
})

private data class LocaleNormalizationCase(
    val create: () -> Locale,
    val expected: String,
)

private data class InvalidLocaleCase(
    val create: () -> Locale,
    val expectedMessage: String,
)

private val localeNormalizationCases = mapOf(
    "language and region components" to LocaleNormalizationCase({ Locale.from("EN", "us") }, "en-US"),
    "language, script, and region components" to LocaleNormalizationCase(
        { Locale.from("zh", "tw", "hant") },
        "zh-Hant-TW",
    ),
    "two-token locale string" to LocaleNormalizationCase({ Locale.from("KO-kr") }, "ko-KR"),
    "three-token locale string" to LocaleNormalizationCase({ Locale.from("zh-hant-tw") }, "zh-Hant-TW"),
)

private val invalidLocaleCases = mapOf(
    "one-character language" to InvalidLocaleCase({ Locale.from("e", "US") }, "Invalid language code"),
    "three-character region" to InvalidLocaleCase({ Locale.from("en", "USA") }, "Invalid region code"),
    "five-character script" to InvalidLocaleCase(
        { Locale.from("en", "US", "Latin") },
        "Invalid script code",
    ),
    "one-token locale string" to InvalidLocaleCase({ Locale.from("en") }, "Invalid locale format"),
    "four-token locale string" to InvalidLocaleCase(
        { Locale.from("en-Latn-US-extra") },
        "Invalid locale format",
    ),
)
