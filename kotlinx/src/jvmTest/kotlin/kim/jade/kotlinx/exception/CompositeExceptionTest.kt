package kim.jade.kotlinx.exception

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.PrintWriter

class CompositeExceptionTest : DescribeSpec({
    describe("CompositeException construction") {
        context("given no failures") {
            it("exposes an empty composite without a cause") {
                val composite = CompositeException(emptyList())

                composite.size shouldBe 0
                composite.message shouldBe "0 exceptions occurred."
                composite.cause shouldBe null
            }
        }

        context("given one failure") {
            it("uses that exact failure as its cause") {
                val failure = IllegalStateException("one")
                val composite = CompositeException(failure)

                composite.size shouldBe 1
                composite.message shouldBe "1 exceptions occurred."
                composite.cause shouldBeSameInstanceAs failure
            }
        }

        context("given nested composites and duplicate instances") {
            it("flattens nested values and retains unique instances in encounter order") {
                val first = IllegalArgumentException("first")
                val second = IllegalStateException("second")
                val nested = CompositeException(first, second, first)

                val result = CompositeException(nested, second, RuntimeException("third"))

                result.size shouldBe 3
                result.exceptions.take(2).shouldContainExactly(first, second)
                result.exceptions[2].message shouldBe "third"
            }
        }
    }

    describe("the cause overview") {
        context("given multiple failures with a nested cause") {
            it("summarizes every failure and omits its own stack frames") {
                val root = IllegalArgumentException("root cause")
                val first = IllegalStateException("first failure", root)
                val second = UnsupportedOperationException("second failure")

                val overview = CompositeException(first, second).cause

                overview.shouldBeInstanceOf<CompositeException.ExceptionOverview>()
                overview.message shouldContain "Multiple exceptions (2)"
                overview.message shouldContain "java.lang.IllegalStateException: first failure"
                overview.message shouldContain "java.lang.IllegalArgumentException: root cause"
                overview.message shouldContain "java.lang.UnsupportedOperationException: second failure"
                overview.stackTrace.size shouldBe 0
            }
        }

        context("given failures that share the same cause instance") {
            it("marks the repeated cause instead of expanding it again") {
                val shared = IllegalArgumentException("shared", RuntimeException("shared root"))
                val first = IllegalStateException("first", shared)
                val second = UnsupportedOperationException("second", shared)

                CompositeException(first, second).cause?.message shouldContain "cause not expanded again"
            }
        }
    }

    describe("stack-trace output") {
        context("given a composite with two failures") {
            withData(stackTraceSinks) { render ->
                it("contains every composed exception and nested cause") {
                    val composite = CompositeException(
                        IllegalArgumentException("bad argument"),
                        IllegalStateException("bad state", RuntimeException("root")),
                    )
                    val trace = render(composite)

                    trace shouldContain "2 exceptions occurred."
                    trace shouldContain "ComposedException 1"
                    trace shouldContain "java.lang.IllegalArgumentException: bad argument"
                    trace shouldContain "ComposedException 2"
                    trace shouldContain "java.lang.IllegalStateException: bad state"
                    trace shouldContain "Caused by: java.lang.RuntimeException: root"
                }
            }
        }
    }
})

private val stackTraceSinks = mapOf<String, (CompositeException) -> String>(
    "PrintStream" to { composite ->
        val bytes = ByteArrayOutputStream()
        composite.printStackTrace(PrintStream(bytes))
        bytes.toString(Charsets.UTF_8.name())
    },
    "PrintWriter" to { composite ->
        val bytes = ByteArrayOutputStream()
        PrintWriter(bytes.writer()).use { writer -> composite.printStackTrace(writer) }
        bytes.toString(Charsets.UTF_8.name())
    },
)
