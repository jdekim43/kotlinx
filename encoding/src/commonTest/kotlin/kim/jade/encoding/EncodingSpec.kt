package kim.jade.encoding

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class EncodingSpec : DescribeSpec({

    describe("Encoder") {
        context("when used through the encoding extensions") {
            it("delegates encoding and decoding to the supplied encoder") {
                val encoder = object : Encoder<String, Int> {
                    override fun encode(data: String): Int = data.length

                    override fun decode(data: Int): String = "x".repeat(data)
                }

                "kotest".encode(encoder) shouldBe 6
                3.decode(encoder) shouldBe "xxx"
            }
        }

        context("when a function-backed encoder fails") {
            val encodeCause = IllegalArgumentException("encode failed")
            val decodeCause = IllegalStateException("decode failed")
            val encoder = Encoder<String, String>(
                encode = { throw encodeCause },
                decode = { throw decodeCause },
            )
            val failures = mapOf(
                "encode" to EncoderFailure(encodeCause) { encoder.encode("value") },
                "decode" to EncoderFailure(decodeCause) { encoder.decode("value") },
            )

            withData(failures) { failure ->
                it("wraps the original failure in EncoderException") {
                    val exception = shouldThrow<EncoderException> {
                        failure.operation()
                    }

                    exception.message shouldBe failure.cause.message
                    exception.cause shouldBeSameInstanceAs failure.cause
                }
            }
        }
    }

    describe("Hex") {
        context("when encoding bytes") {
            it("uses uppercase digits for every nibble, including an empty input") {
                Hex.encode(bytes(0x00, 0x0F, 0x10, 0x7F, 0x80, 0xFF)) shouldBe "000F107F80FF"
                Hex.encode(byteArrayOf()) shouldBe ""
            }
        }

        context("when decoding text") {
            it("accepts uppercase, lowercase, and empty input") {
                Hex.decode("00aBcDff").shouldHaveBytes(0x00, 0xAB, 0xCD, 0xFF)
                Hex.decode("").shouldHaveBytes()
            }

            it("rejects a non-hexadecimal digit") {
                shouldThrow<IllegalStateException> {
                    Hex.decode("0G")
                }.message shouldBe "Invalid hex digit 'G'"
            }
        }
    }

    describe("Base64") {
        context("given the RFC 4648 examples") {
            withIts(rfc4648Base64Vectors) { vector ->
                Base64.encode(vector.plain.encodeToByteArray()) shouldBe vector.encoded
                Base64.decode(vector.encoded).decodeToString() shouldBe vector.plain
            }
        }

        context("given every possible byte value") {
            it("round-trips the binary input") {
                val input = ByteArray(256) { it.toByte() }

                Base64.decode(Base64.encode(input)).shouldHaveBytes(input)
            }
        }

        context("when decoding relaxed input") {
            it("ignores whitespace") {
                Base64.decode(" Zm9v\r\nYmFy ").decodeToString() shouldBe "foobar"
            }

            it("accepts omitted padding") {
                Base64.decode("Zg").decodeToString() shouldBe "f"
                Base64.decode("Zm8").decodeToString() shouldBe "fo"
            }
        }
    }

    describe("Base58") {
        context("given known vectors") {
            withIts(base58Vectors) { vector ->
                Base58.encode(vector.plain) shouldBe vector.encoded
                Base58.decode(vector.encoded).shouldHaveBytes(vector.plain)
            }
        }

        context("given arbitrary binary data") {
            it("round-trips without mutating the input") {
                val input = ByteArray(256) { it.toByte() }
                val snapshot = input.copyOf()

                val encoded = Base58.encode(input)

                input.shouldHaveBytes(snapshot)
                Base58.decode(encoded).shouldHaveBytes(input)
            }
        }

        context("given a character outside the Base58 alphabet") {
            withIts(invalidBase58Characters) { invalid ->
                shouldThrow<NumberFormatException> {
                    Base58.decode("12${invalid}3")
                }.message shouldBe "Illegal character $invalid at position 2"
            }
        }
    }

    describe("ULEB") {
        context("given canonical unsigned values") {
            withIts(ulebVectors) { vector ->
                ULEB.encode(vector.value).shouldHaveBytes(vector.encoded)
                ULEB.decode(vector.encoded) shouldBe (vector.value to vector.encoded.size)
            }
        }

        context("when decoding from an offset") {
            it("reports only the bytes consumed by the value") {
                ULEB.decode(bytes(0x55, 0xE5, 0x8E, 0x26, 0x55), start = 1) shouldBe (624_485u to 3)
            }
        }
    }
})

private data class EncoderFailure(
    val cause: Throwable,
    val operation: () -> Unit,
)

private data class Base64Vector(
    val plain: String,
    val encoded: String,
)

private val rfc4648Base64Vectors = mapOf(
    "encodes and decodes empty input" to Base64Vector("", ""),
    "encodes and decodes f" to Base64Vector("f", "Zg=="),
    "encodes and decodes fo" to Base64Vector("fo", "Zm8="),
    "encodes and decodes foo" to Base64Vector("foo", "Zm9v"),
    "encodes and decodes foob" to Base64Vector("foob", "Zm9vYg=="),
    "encodes and decodes fooba" to Base64Vector("fooba", "Zm9vYmE="),
    "encodes and decodes foobar" to Base64Vector("foobar", "Zm9vYmFy"),
)

private data class Base58Vector(
    val plain: ByteArray,
    val encoded: String,
)

private val base58Vectors = mapOf(
    "encodes and decodes empty input" to Base58Vector(byteArrayOf(), ""),
    "encodes and decodes Hello World" to Base58Vector("Hello World".encodeToByteArray(), "JxF12TrwUP45BMd"),
    "preserves leading zero bytes" to Base58Vector(bytes(0, 0, 0, 1), "1112"),
)

private val invalidBase58Characters = mapOf(
    "reports zero and its position" to '0',
    "reports uppercase O and its position" to 'O',
    "reports uppercase I and its position" to 'I',
    "reports lowercase l and its position" to 'l',
    "reports a non-ASCII character and its position" to '가',
)

private data class UlebVector(
    val value: UInt,
    val encoded: ByteArray,
)

private val ulebVectors = mapOf(
    "encodes and decodes zero canonically" to UlebVector(0u, bytes(0x00)),
    "encodes and decodes one canonically" to UlebVector(1u, bytes(0x01)),
    "encodes and decodes the largest one-byte value canonically" to UlebVector(127u, bytes(0x7F)),
    "encodes and decodes the smallest two-byte value canonically" to UlebVector(128u, bytes(0x80, 0x01)),
    "encodes and decodes 255 canonically" to UlebVector(255u, bytes(0xFF, 0x01)),
    "encodes and decodes 16,384 canonically" to UlebVector(16_384u, bytes(0x80, 0x80, 0x01)),
    "encodes and decodes 624,485 canonically" to UlebVector(624_485u, bytes(0xE5, 0x8E, 0x26)),
    "encodes and decodes Int.MAX_VALUE canonically" to
        UlebVector(Int.MAX_VALUE.toUInt(), bytes(0xFF, 0xFF, 0xFF, 0xFF, 0x07)),
)
