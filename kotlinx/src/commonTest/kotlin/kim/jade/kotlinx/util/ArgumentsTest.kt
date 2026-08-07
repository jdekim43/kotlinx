package kim.jade.kotlinx.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe

class ArgumentsTest : DescribeSpec({

    describe("parseArguments") {
        context("given command-line arguments") {
            withIts(argumentCases) { case ->
                parseArguments(*case.args.toTypedArray()) shouldBe case.expected
            }
        }
    }
})

private data class ArgumentsCase(
    val args: List<String>,
    val expected: Map<String, List<String>>,
)

private val argumentCases = mapOf(
    "parses long and short options while ignoring positional arguments" to ArgumentsCase(
        args = listOf("--host=example.test", "-p=8080", "input.txt"),
        expected = mapOf(
            "host" to listOf("example.test"),
            "p" to listOf("8080"),
        ),
    ),
    "collects repeated options in encounter order" to ArgumentsCase(
        args = listOf("--tag=first", "--tag=second", "-v", "-v=true"),
        expected = mapOf(
            "tag" to listOf("first", "second"),
            "v" to listOf("", "true"),
        ),
    ),
    "preserves equals signs inside option values" to ArgumentsCase(
        args = listOf("--expression=a=b=c", "--empty="),
        expected = mapOf(
            "expression" to listOf("a=b=c"),
            "empty" to listOf(""),
        ),
    ),
    "returns an empty map for empty input" to ArgumentsCase(
        args = emptyList(),
        expected = emptyMap(),
    ),
)
