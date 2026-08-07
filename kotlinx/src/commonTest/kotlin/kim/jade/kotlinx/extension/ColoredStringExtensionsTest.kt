package kim.jade.kotlinx.extension

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ColoredStringExtensionsTest : DescribeSpec({

    describe("colored") {
        context("when coloring is enabled") {
            it("wraps colors and text styles with ANSI codes and a reset") {
                colored { "danger".red } shouldBe "${ANSI_ESCAPE}[31mdanger${ANSI_ESCAPE}[0m"
                colored { "important".bold } shouldBe "${ANSI_ESCAPE}[1mimportant${ANSI_ESCAPE}[0m"
                colored { "note".underline } shouldBe "${ANSI_ESCAPE}[4mnote${ANSI_ESCAPE}[0m"
            }
        }

        context("when coloring is disabled") {
            it("returns plain text through property, function, and style APIs") {
                colored(enabled = false) { "danger".red } shouldBe "danger"
                colored(enabled = false) { red("danger") } shouldBe "danger"
                colored(enabled = false) { "danger"(red) } shouldBe "danger"
            }
        }

        context("when a style has a predicate") {
            it("applies formatting only when the predicate passes") {
                colored { 5.red { it > 0 } } shouldBe "${ANSI_ESCAPE}[31m5${ANSI_ESCAPE}[0m"
                colored { (-1).red { it > 0 } } shouldBe "-1"
                colored { 5.style(red) { it == 5 } } shouldBe "${ANSI_ESCAPE}[31m5${ANSI_ESCAPE}[0m"
                colored { 5.style(red) { it != 5 } } shouldBe "5"
            }
        }
    }

    describe("composite styles") {
        context("when styles are nested") {
            it("preserves nesting order and propagates NotApplied") {
                val redAndBold = style { red.bold }

                redAndBold.wrap("alert") shouldBe "${ANSI_ESCAPE}[1m${ANSI_ESCAPE}[31malert${ANSI_ESCAPE}[0m"
                (ColoredConsole.Style.NotApplied + redAndBold).wrap("alert") shouldBe "alert"
            }
        }
    }

    describe("background and bright modifiers") {
        context("when applied to Style values") {
            it("transforms color styles only") {
                style { red.bg }.wrap("x") shouldBe "${ANSI_ESCAPE}[41mx${ANSI_ESCAPE}[0m"
                style { red.bright }.wrap("x") shouldBe "${ANSI_ESCAPE}[91mx${ANSI_ESCAPE}[0m"
                style { bold.bg }.wrap("x") shouldBe "${ANSI_ESCAPE}[1mx${ANSI_ESCAPE}[0m"
                style { bold.bright }.wrap("x") shouldBe "${ANSI_ESCAPE}[1mx${ANSI_ESCAPE}[0m"
            }
        }

        context("when applied to strings") {
            it("changes only a leading color escape") {
                colored {
                    "${ANSI_ESCAPE}[31mred${ANSI_ESCAPE}[0m".bright shouldBe
                        "${ANSI_ESCAPE}[91mred${ANSI_ESCAPE}[0m"
                    "${ANSI_ESCAPE}[31mred${ANSI_ESCAPE}[0m".bg shouldBe
                        "${ANSI_ESCAPE}[41mred${ANSI_ESCAPE}[0m"
                    "plain".bright shouldBe "plain"
                    "${ANSI_ESCAPE}[1mbold${ANSI_ESCAPE}[0m".bg shouldBe
                        "${ANSI_ESCAPE}[1mbold${ANSI_ESCAPE}[0m"
                }
            }
        }
    }

    describe("wrapping an already styled string") {
        context("when an intermediate reset is present") {
            it("reapplies the outer style after the reset") {
                colored {
                    "left${ANSI_ESCAPE}[0mright".bold shouldBe
                        "${ANSI_ESCAPE}[1mleft${ANSI_ESCAPE}[0m${ANSI_ESCAPE}[1mright${ANSI_ESCAPE}[0m"
                }
            }
        }
    }
})

private const val ANSI_ESCAPE = "\u001B"
