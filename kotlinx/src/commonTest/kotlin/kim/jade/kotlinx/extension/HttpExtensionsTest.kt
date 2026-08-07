package kim.jade.kotlinx.extension

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe

class HttpExtensionsTest : DescribeSpec({

    describe("addQueryParameter") {
        context("given a URL without a query") {
            it("starts a query with the new parameter") {
                "https://example.test/path".addQueryParameter("page", "2") shouldBe
                    "https://example.test/path?page=2"
            }
        }

        context("given a URL with an existing query suffix") {
            withIts(addQueryParameterCases) { case ->
                case.input.addQueryParameter("page", "2") shouldBe case.expected
            }
        }
    }

    describe("putQueryParameter") {
        context("when the key does not exist") {
            withIts(missingQueryParameterCases) { case ->
                case.input.putQueryParameter("page", "2") shouldBe case.expected
            }
        }

        context("when the key already has a value") {
            withIts(existingQueryParameterCases) { case ->
                case.input.putQueryParameter("page", "2") shouldBe case.expected
            }
        }

        context("when the key has no value") {
            withIts(emptyQueryParameterCases) { case ->
                case.input.putQueryParameter("page", "2") shouldBe case.expected
            }
        }
    }

    describe("escapeJsNull") {
        context("given a JavaScript null literal") {
            withIts(jsNullCases) { value ->
                value.escapeJsNull() shouldBe null
            }
        }

        context("given null, blank, or ordinary text") {
            withIts(preservedStringCases) { case ->
                case.input.escapeJsNull() shouldBe case.expected
            }
        }
    }
})

private data class QueryParameterCase(
    val input: String,
    val expected: String,
)

private val addQueryParameterCases = mapOf(
    "appends with an ampersand after an existing parameter" to QueryParameterCase(
        "https://example.test/path?sort=name",
        "https://example.test/path?sort=name&page=2",
    ),
    "appends directly after a trailing question mark" to QueryParameterCase(
        "https://example.test/path?",
        "https://example.test/path?page=2",
    ),
    "appends directly after a trailing ampersand" to QueryParameterCase(
        "https://example.test/path?sort=name&",
        "https://example.test/path?sort=name&page=2",
    ),
)

private val missingQueryParameterCases = mapOf(
    "appends the missing key to an existing query" to QueryParameterCase(
        "/items?sort=name",
        "/items?sort=name&page=2",
    ),
    "starts a query for the missing key" to QueryParameterCase(
        "/items",
        "/items?page=2",
    ),
)

private val existingQueryParameterCases = mapOf(
    "replaces a value at the beginning of a query" to QueryParameterCase(
        "/items?page=1&sort=name",
        "/items?page=2&sort=name",
    ),
    "replaces a value in the middle of a query" to QueryParameterCase(
        "/items?sort=name&page=1&size=20",
        "/items?sort=name&page=2&size=20",
    ),
    "replaces a value at the end of a query" to QueryParameterCase(
        "/items?sort=name&page=1",
        "/items?sort=name&page=2",
    ),
)

private val emptyQueryParameterCases = mapOf(
    "fills a parameter without an equals sign" to QueryParameterCase(
        "/items?page",
        "/items?page=2",
    ),
    "fills a parameter with an empty value" to QueryParameterCase(
        "/items?page=",
        "/items?page=2",
    ),
    "fills a valueless parameter before another parameter" to QueryParameterCase(
        "/items?page&sort=name",
        "/items?page=2&sort=name",
    ),
)

private val jsNullCases = mapOf(
    "converts lowercase null" to "null",
    "converts uppercase null" to "NULL",
    "converts lowercase undefined" to "undefined",
    "converts mixed-case undefined" to "UnDeFiNeD",
)

private data class PreservedStringCase(
    val input: String?,
    val expected: String?,
)

private val preservedStringCases = mapOf(
    "preserves null" to PreservedStringCase(null, null),
    "preserves an empty string" to PreservedStringCase("", ""),
    "preserves whitespace" to PreservedStringCase("  ", "  "),
    "preserves ordinary text" to PreservedStringCase("nullable", "nullable"),
    "preserves a padded null literal" to PreservedStringCase(" null ", " null "),
)
