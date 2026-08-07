package kim.jade.encoding

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class Base58CheckSpec : DescribeSpec({
    describe("Base58Check") {
        context("given the canonical Bitcoin vector") {
            val payload = Hex.decode("00010966776006953D5567439E5E39F86A0D273BEE")
            val encoded = "16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM"

            it("encodes the payload") {
                Base58Check.encode(payload) shouldBe encoded
            }

            it("decodes the encoded value") {
                Base58Check.decode(encoded) shouldBe payload
            }
        }

        context("given representative binary payloads") {
            withData(base58CheckRoundTripPayloads) { payload ->
                it("round trips through the Base58WithChecksum alias") {
                    val encoded = Base58WithChecksum.encode(payload)

                    Base58WithChecksum.decode(encoded) shouldBe payload
                }
            }
        }

        context("when decoding invalid data") {
            it("rejects an altered checksum") {
                val encoded = Base58Check.encode("checksum protected".encodeToByteArray())
                val replacement = if (encoded.last() == '1') '2' else '1'

                shouldThrow<IllegalArgumentException> {
                    Base58Check.decode(encoded.dropLast(1) + replacement)
                }.message.orEmpty() shouldContain "Checksum mismatch"
            }

            it("rejects data shorter than the four-byte checksum") {
                shouldThrow<Exception> {
                    Base58Check.decode("1")
                }.message.orEmpty() shouldContain "Too short for checksum"
            }
        }
    }
})

private val base58CheckRoundTripPayloads = mapOf(
    "empty payload" to byteArrayOf(),
    "leading zeroes" to byteArrayOf(0, 0, 1, 2, 3),
    "signed byte values" to byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE),
    "all byte values" to ByteArray(256) { it.toByte() },
)
