package kim.jade.kotlinx.thread

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class JvmThreadTest : DescribeSpec({
    describe("ThreadLocal") {
        context("when accessed through methods and the value property") {
            it("uses the same per-thread storage and supports removal") {
                val local = ThreadLocal<String>()

                local.get() shouldBe null
                local.set("first")
                local.value shouldBe "first"
                local.value = "second"
                local.get() shouldBe "second"
                local.remove()
                local.value shouldBe null
            }
        }

        context("when parent and child JVM threads use the same instance") {
            it("isolates their values") {
                val local = ThreadLocal<String>()
                local.value = "parent"
                var childBefore: String? = "unset"
                var childAfter: String? = null

                val child = kotlin.concurrent.thread(name = "kotlinx-test-child") {
                    childBefore = local.value
                    local.value = "child"
                    childAfter = local.value
                }
                child.join()

                childBefore shouldBe null
                childAfter shouldBe "child"
                local.value shouldBe "parent"
            }
        }
    }

    describe("currentThreadName") {
        context("on the active test thread") {
            it("returns the JVM thread name") {
                currentThreadName() shouldBe Thread.currentThread().name
            }
        }

        context("on a named child thread") {
            it("returns the child's name") {
                var reportedName: String? = null
                val child = kotlin.concurrent.thread(name = "kotlinx-named-thread") {
                    reportedName = currentThreadName()
                }
                child.join()

                reportedName shouldBe "kotlinx-named-thread"
            }
        }
    }
})
