package kim.jade.kotlinx.pool

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class ObjectPoolTest : DescribeSpec({

    describe("acquire") {
        context("when the pool is empty") {
            it("creates a new object for each acquisition") {
                var creations = 0
                val pool = ObjectPool { Any().also { creations += 1 } }

                val first = pool.acquire()
                val second = pool.acquire()

                creations shouldBe 2
                first shouldNotBeSameInstanceAs second
            }
        }

        context("when released objects are available") {
            it("returns them in last-in-first-out order") {
                val pool = ObjectPool { Any() }
                val first = Any()
                val second = Any()

                pool.release(first)
                pool.release(second)

                pool.acquire() shouldBeSameInstanceAs second
                pool.acquire() shouldBeSameInstanceAs first
            }
        }
    }

    describe("use") {
        context("when the body succeeds") {
            it("returns the body result and releases the object") {
                val pooled = StringBuilder()
                val pool = ObjectPool { pooled }

                pool.use { value ->
                    value.append("used")
                    value.length
                } shouldBe 4

                pool.acquire() shouldBeSameInstanceAs pooled
            }
        }

        context("when the body fails") {
            it("rethrows the same failure and still releases the object") {
                val pooled = Any()
                val pool = ObjectPool { pooled }
                val failure = IllegalStateException("body failed")

                val thrown = shouldThrow<IllegalStateException> {
                    pool.use { throw failure }
                }

                thrown shouldBeSameInstanceAs failure
                pool.acquire() shouldBeSameInstanceAs pooled
            }
        }
    }

    describe("close") {
        context("when objects are retained") {
            it("discards every retained object") {
                var creations = 0
                val pool = ObjectPool { Any().also { creations += 1 } }
                val retained = pool.acquire()
                pool.release(retained)

                pool.close()
                val afterClose = pool.acquire()

                creations shouldBe 2
                afterClose shouldNotBeSameInstanceAs retained
            }
        }
    }
})
