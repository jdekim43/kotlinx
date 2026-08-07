package kim.jade.kotlinx.extension

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay

class CoroutineExtensionsTest : DescribeSpec({
    describe("parallel fixed-arity overloads") {
        context("when blocks complete out of order") {
            it("preserves declaration order") {
                parallel(
                    block1 = { delay(20); "first" },
                    block2 = { "second" },
                ) shouldBe ("first" to "second")
            }
        }

        context("given three, four, or five heterogeneous blocks") {
            it("retains all three results") {
                parallel({ 1 }, { "two" }, { 3.0 }) shouldBe Triple(1, "two", 3.0)
            }

            it("retains all four results") {
                val result = parallel({ 1 }, { "two" }, { 3.0 }, { true })

                result.first shouldBe 1
                result.second shouldBe "two"
                result.third shouldBe 3.0
                result.fourth shouldBe true
            }

            it("retains all five results") {
                val result = parallel({ 1 }, { 2 }, { 3 }, { 4 }, { 5 })

                result.first shouldBe 1
                result.second shouldBe 2
                result.third shouldBe 3
                result.fourth shouldBe 4
                result.fifth shouldBe 5
            }
        }

        context("when a coroutine context is supplied") {
            it("makes the context visible to every block") {
                parallel(
                    block1 = { currentCoroutineContext()[CoroutineName]?.name },
                    block2 = { currentCoroutineContext()[CoroutineName]?.name },
                    context = CoroutineName("parallel-test"),
                ) shouldBe ("parallel-test" to "parallel-test")
            }
        }

        context("when one block fails") {
            it("propagates the original exception and cancels sibling work") {
                val siblingStarted = CompletableDeferred<Unit>()
                val siblingCancelled = CompletableDeferred<Unit>()
                val failure = IllegalStateException("parallel failed")

                shouldThrow<IllegalStateException> {
                    parallel(
                        block1 = {
                            siblingStarted.await()
                            throw failure
                        },
                        block2 = {
                            siblingStarted.complete(Unit)
                            try {
                                awaitCancellation()
                            } finally {
                                siblingCancelled.complete(Unit)
                            }
                        },
                    )
                } shouldBe failure

                siblingCancelled.await()
            }
        }
    }

    describe("parallel vararg overload") {
        context("given different numbers of blocks") {
            withData(varargParallelVectors) { vector ->
                it("returns every result in declaration order") {
                    parallel(*vector.blocks.toTypedArray()) shouldBe vector.expected
                }
            }
        }
    }
})

private data class VarargParallelVector(
    val blocks: List<suspend () -> Int>,
    val expected: List<Int>,
)

private val varargParallelVectors = mapOf(
    "zero blocks" to VarargParallelVector(emptyList(), emptyList()),
    "one block" to VarargParallelVector(listOf({ 1 }), listOf(1)),
    "three blocks" to VarargParallelVector(listOf({ 1 }, { 2 }, { 3 }), listOf(1, 2, 3)),
)
