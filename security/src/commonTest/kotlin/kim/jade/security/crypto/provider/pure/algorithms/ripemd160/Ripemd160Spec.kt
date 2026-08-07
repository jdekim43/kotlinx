package kim.jade.security.crypto.provider.pure.algorithms.ripemd160

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kim.jade.security.shouldHaveHex

class Ripemd160Spec : DescribeSpec({
    describe("Ripemd160") {
        context("when input arrives one byte at a time") {
            it("produces the expected digest") {
                val digest = Ripemd160()
                "abc".encodeToByteArray().forEach(digest::update)

                val output = ByteArray(Ripemd160.DIGEST_LENGTH)
                digest.doFinal(output, 0) shouldBe Ripemd160.DIGEST_LENGTH
                output.shouldHaveHex("8EB208F7E05D987A9B044A8E98C6B087F15A0BFC")
            }
        }

        context("when a slice crossing block boundaries is finalized at a non-zero offset") {
            it("writes only the digest range") {
                val message = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
                val wrapped = "ignored:$message:ignored".encodeToByteArray()
                val digest = Ripemd160()
                digest.update(wrapped, inputOffset = 8, len = message.length)

                val output = ByteArray(Ripemd160.DIGEST_LENGTH + 4) { 0x5a }
                digest.doFinal(output, 2) shouldBe Ripemd160.DIGEST_LENGTH

                output.copyOfRange(2, 22).shouldHaveHex("12A053384A9C0C88E405A06C27DCF49ADA62EB2B")
                output[0] shouldBe 0x5a.toByte()
                output[1] shouldBe 0x5a.toByte()
                output[22] shouldBe 0x5a.toByte()
                output[23] shouldBe 0x5a.toByte()
            }
        }

        context("after finalization") {
            it("resets itself to the empty state") {
                val digest = Ripemd160()
                digest.update("abc".encodeToByteArray(), 0, 3)
                digest.doFinal(ByteArray(Ripemd160.DIGEST_LENGTH), 0)

                val emptyHash = ByteArray(Ripemd160.DIGEST_LENGTH)
                digest.doFinal(emptyHash, 0)

                emptyHash.shouldHaveHex("9C1185A5C5E9FC54612808977EE8F548B2258D31")
            }
        }
    }
})
