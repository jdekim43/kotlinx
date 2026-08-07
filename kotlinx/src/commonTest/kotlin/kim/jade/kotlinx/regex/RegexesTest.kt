package kim.jade.kotlinx.regex

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe

class RegexesTest : DescribeSpec({

    describe("Regexes.EMAIL") {
        context("given a typical email address") {
            withIts(validEmailAddresses) { value ->
                Regexes.EMAIL.matches(value) shouldBe true
            }
        }

        context("given a malformed email address") {
            withIts(invalidEmailAddresses) { value ->
                Regexes.EMAIL.matches(value) shouldBe false
            }
        }
    }

    describe("Regexes.HTTP_URL") {
        context("given a complete HTTP or HTTPS URL") {
            withIts(validHttpUrls) { value ->
                Regexes.HTTP_URL.matches(value) shouldBe true
            }
        }

        context("given an unsupported or incomplete URL") {
            withIts(invalidHttpUrls) { value ->
                Regexes.HTTP_URL.matches(value) shouldBe false
            }
        }
    }
})

private val validEmailAddresses = mapOf(
    "accepts a basic address" to "user@example.com",
    "accepts dots, tags, and nested domains" to "first.last+tag@sub.example.co.kr",
    "accepts an underscore in the local part" to "a_b@example.io",
)

private val invalidEmailAddresses = mapOf(
    "rejects an address without a domain" to "user",
    "rejects an address without a local part" to "@example.com",
    "rejects a domain without a top-level domain" to "user@example",
    "rejects whitespace in the local part" to "user name@example.com",
    "rejects an unsupported long top-level domain" to "user@example.toolong",
)

private val validHttpUrls = mapOf(
    "accepts an HTTP URL" to "http://example.com",
    "accepts an HTTPS URL with a path, query, and fragment" to "https://example.com/path?q=value#section",
    "accepts localhost with a port and path" to "https://localhost:8080/health",
)

private val invalidHttpUrls = mapOf(
    "rejects FTP" to "ftp://example.com",
    "rejects a URL without a scheme" to "example.com",
    "rejects HTTPS without a host" to "https://",
    "rejects a mailto URL" to "mailto:user@example.com",
)
