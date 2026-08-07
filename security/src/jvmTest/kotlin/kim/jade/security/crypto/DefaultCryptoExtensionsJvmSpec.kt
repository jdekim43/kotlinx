@file:OptIn(dev.whyoleg.cryptography.DelicateCryptographyApi::class)

package kim.jade.security.crypto

import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA256
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kim.jade.encoding.Hex
import kim.jade.security.shouldHaveHex

class DefaultCryptoExtensionsJvmSpec : DescribeSpec({
    describe("default-provider digest extensions") {
        context("given the SHA-256 vector for \"abc\"") {
            val expected = "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD"

            it("returns raw bytes from both receiver overloads") {
                "abc".hash(SHA256).shouldHaveHex(expected)
                "abc".encodeToByteArray().hash(SHA256).shouldHaveHex(expected)
            }

            it("returns encoded values from both receiver overloads") {
                "abc".hash(SHA256, Hex) shouldBe expected
                "abc".encodeToByteArray().hash(SHA256, Hex) shouldBe expected
            }
        }
    }

    describe("default-provider HMAC extensions") {
        val key = ByteArray(20) { 0x0b }
        val message = "Hi There"
        val expected = "B0344C61D8DB38535CA8AFCEAF0BF12B881DC200C9833DA726E9376C2E32CFF7"

        context("given the RFC 4231 HMAC-SHA256 vector") {
            it("generates the expected signature") {
                message.hash(HMAC, SHA256, key).shouldHaveHex(expected)
            }

            it("verifies the signature through both receiver overloads") {
                val signature = message.hash(HMAC, SHA256, key)

                message.verify(HMAC, SHA256, key, signature).shouldBeTrue()
                message.encodeToByteArray().verify(HMAC, SHA256, key, signature).shouldBeTrue()
            }
        }

        context("when the signed inputs are changed") {
            it("rejects an altered signature") {
                val signature = message.hash(HMAC, SHA256, key)
                val changed = signature.copyOf().also {
                    it[it.lastIndex] = (it.last().toInt() xor 1).toByte()
                }

                message.verify(HMAC, SHA256, key, changed).shouldBeFalse()
            }

            it("rejects an altered message") {
                val signature = message.hash(HMAC, SHA256, key)

                "Hi There!".verify(HMAC, SHA256, key, signature).shouldBeFalse()
            }
        }
    }
})
