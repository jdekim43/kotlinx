package kim.jade.kotlinx.dsl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kim.jade.kotlinx.annotation.Experimental

@OptIn(Experimental::class)
class DslTest : DescribeSpec({

    describe("Boolean.then") {
        context("when the receiver is true") {
            it("executes the block and returns its value") {
                var invocations = 0

                true.then {
                    invocations += 1
                    "result"
                } shouldBe "result"

                invocations shouldBe 1
            }
        }

        context("when the receiver is false") {
            it("skips the block and returns null") {
                var invocations = 0

                false.then {
                    invocations += 1
                    "ignored"
                } shouldBe null

                invocations shouldBe 0
            }
        }
    }

    describe("matchChanges") {
        context("when several transitions are declared") {
            it("executes only matching transitions") {
                val matches = mutableListOf<String>()

                matchChanges(before = "draft", after = "published") {
                    "draft".to("published") { matches += "member extension" }
                    case("draft" to "published") { matches += "case" }
                    "published".to("draft") { matches += "wrong direction" }
                    case("draft" to "archived") { matches += "wrong destination" }
                }

                matches shouldBe listOf("member extension", "case")
            }
        }

        context("when a transition contains null") {
            it("matches null values") {
                var matched = false

                matchChanges<String?>(before = null, after = "ready") {
                    null.to("ready") { matched = true }
                }

                matched shouldBe true
            }
        }
    }

    describe("transactional") {
        val key = "dsl-test-manager"
        val manager = RecordingTransactionManager()
        TransactionManager.register(key, manager)

        beforeTest {
            manager.lastOptions = null
            manager.invocations = 0
        }

        context("when options are configured") {
            it("delegates the options and returns the block result") {
                val result = transactional(key, options = {
                    isolation = 7
                    readOnly = true
                }) {
                    "committed"
                }

                result shouldBe "committed"
                manager.invocations shouldBe 1
                manager.lastOptions?.isolation shouldBe 7
                manager.lastOptions?.readOnly shouldBe true
                manager.lastOptions?.label shouldBe "default"
            }
        }

        context("when no option builder is supplied") {
            it("passes null options to the manager") {
                transactional(key) { 42 } shouldBe 42

                manager.lastOptions shouldBe null
                manager.invocations shouldBe 1
            }
        }

        context("when the transaction block fails") {
            it("propagates the original exception") {
                val failure = IllegalStateException("rollback")

                shouldThrow<IllegalStateException> {
                    transactional(key) { throw failure }
                } shouldBeSameInstanceAs failure

                manager.invocations shouldBe 1
            }
        }

        context("when the manager name is not registered") {
            it("rejects the transaction") {
                shouldThrow<IllegalArgumentException> {
                    transactional("missing-dsl-test-manager") { Unit }
                }.message shouldContain "No transaction manager found"
            }
        }
    }
})

@OptIn(Experimental::class)
private class RecordingTransactionManager : TransactionManager<RecordingTransactionOptions> {
    var lastOptions: RecordingTransactionOptions? = null
    var invocations: Int = 0

    override fun createDefaultOptions() = RecordingTransactionOptions(label = "default")

    override suspend fun <T> inTransaction(
        options: RecordingTransactionOptions?,
        block: suspend () -> T,
    ): T {
        invocations += 1
        lastOptions = options
        return block()
    }
}

@OptIn(Experimental::class)
private class RecordingTransactionOptions(
    val label: String,
) : TransactionManager.TransactionOptions()
