package kim.jade.kotlinx.io

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class JvmIoTest : DescribeSpec({
    describe("environmentVariable") {
        context("given a name that is not present") {
            it("returns null") {
                val missingName = "KIM_JADE_KOTLINX_TEST_VARIABLE_THAT_MUST_NOT_EXIST_7F3B1A"

                environmentVariable(missingName) shouldBe null
            }
        }
    }

    describe("eprintln") {
        context("when text is written to standard error") {
            it("appends the platform line separator") {
                val previous = System.err
                val output = ByteArrayOutputStream()
                try {
                    System.setErr(PrintStream(output, true, Charsets.UTF_8.name()))

                    eprintln("diagnostic")

                    output.toString(Charsets.UTF_8.name()) shouldBe "diagnostic${System.lineSeparator()}"
                } finally {
                    System.setErr(previous)
                }
            }
        }
    }

    describe("isTTY") {
        context("when queried on the JVM") {
            it("reflects whether the process exposes a console") {
                isTTY() shouldBe (System.console() != null)
            }
        }
    }
})
