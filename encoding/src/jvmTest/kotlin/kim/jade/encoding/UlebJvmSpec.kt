package kim.jade.encoding

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec

class UlebJvmSpec : DescribeSpec({

    describe("ULEB decoding on the JVM") {
        context("given a truncated continuation sequence") {
            it("fails instead of returning an incomplete value") {
                shouldThrowAny {
                    ULEB.decode(byteArrayOf(0x80.toByte()))
                }
            }
        }
    }
})
