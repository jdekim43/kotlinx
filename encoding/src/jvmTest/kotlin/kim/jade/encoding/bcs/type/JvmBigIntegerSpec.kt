package kim.jade.encoding.bcs.type

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kim.jade.encoding.shouldHaveBytes
import java.math.BigInteger

class JvmBigIntegerSpec : DescribeSpec({

    describe("JvmBigInteger") {
        context("given a positive value that fills the configured width") {
            it("serializes in little-endian order and round-trips the value") {
                val type = JvmBigInteger(4)
                val value = BigInteger("16909060")

                val encoded = type.serialize(value).toByteArray()

                encoded.shouldHaveBytes(0x04, 0x03, 0x02, 0x01)
                type.deserialize(encoded) shouldBe value
                type.length shouldBe 4
            }
        }

        context("given zero") {
            it("round-trips at the configured width") {
                val type = JvmBigInteger(8)

                type.deserialize(type.serialize(BigInteger.ZERO).toByteArray()) shouldBe BigInteger.ZERO
            }
        }
    }
})
