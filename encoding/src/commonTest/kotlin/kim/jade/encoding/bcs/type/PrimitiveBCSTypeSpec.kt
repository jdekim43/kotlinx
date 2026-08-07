package kim.jade.encoding.bcs.type

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe
import kim.jade.encoding.bytes
import kim.jade.encoding.shouldHaveBytes

class PrimitiveBCSTypeSpec : DescribeSpec({

    describe("boolean and fixed bytes") {
        context("when serializing a Bool") {
            withIts(booleanVectors) { vector ->
                Bool.serialize(vector.value).toByteArray().shouldHaveBytes(vector.encoded)
                Bool.deserialize(bytes(vector.encoded)) shouldBe vector.value
            }
        }

        context("when serializing a fixed-size Bytes value") {
            it("reads and writes exactly the configured number of bytes") {
                val type = Bytes(4)
                val value = bytes(0, 1, 0xFE, 0xFF)

                type.serialize(value).toByteArray().shouldHaveBytes(value)
                type.deserialize(value).shouldHaveBytes(value)
                type.size shouldBe 4
            }
        }
    }

    describe("length-prefixed values") {
        context("when serializing a ByteVector") {
            it("represents an empty value with a zero length") {
                ByteVector.serialize(byteArrayOf()).toByteArray().shouldHaveBytes(0)
                ByteVector.deserialize(bytes(0)).shouldHaveBytes()
            }

            it("uses a multi-byte ULEB prefix when required") {
                val value = ByteArray(130) { (it * 31).toByte() }
                val encoded = ByteVector.serialize(value).toByteArray()

                encoded.copyOfRange(0, 2).shouldHaveBytes(0x82, 0x01)
                encoded.size shouldBe 132
                ByteVector.deserialize(encoded).shouldHaveBytes(value)
            }
        }

        context("when serializing a String") {
            it("writes and reads its UTF-8 byte vector") {
                val value = "hé한👋"
                val utf8 = value.encodeToByteArray()
                val encoded = String.serialize(value).toByteArray()

                encoded.first().toUByte().toUInt() shouldBe utf8.size.toUInt()
                encoded.copyOfRange(1, encoded.size).shouldHaveBytes(utf8)
                String.deserialize(encoded) shouldBe value
            }
        }

        context("when serializing a ULEB128 value") {
            withIts(uleb128Values) { value ->
                ULEB128.deserialize(ULEB128.serialize(value).toByteArray()) shouldBe value
            }
        }
    }

    describe("signed fixed-width integers") {
        context("given boundary values") {
            withIts(signedBoundaryCases) { case ->
                case.verify()
            }
        }

        context("when serializing representative values") {
            withIts(signedEncodingCases) { case ->
                case.verify()
            }
        }
    }

    describe("unsigned fixed-width integers") {
        context("given boundary values") {
            withIts(unsignedBoundaryCases) { case ->
                case.verify()
            }
        }

        context("when serializing representative values") {
            withIts(unsignedEncodingCases) { case ->
                case.verify()
            }
        }
    }

    describe("large fixed-width integers") {
        context("when their sizes are queried") {
            it("publishes the configured byte widths") {
                BigInteger.MAX_LENGTH shouldBe 32
                BigInteger(4).length shouldBe 4
                I128.LENGTH shouldBe 16
                U128.LENGTH shouldBe 16
                I256.LENGTH shouldBe 32
                U256.LENGTH shouldBe 32
            }
        }
    }
})

private data class BooleanVector(
    val value: Boolean,
    val encoded: Int,
)

private val booleanVectors = mapOf(
    "serializes false as zero and round-trips it" to BooleanVector(false, 0),
    "serializes true as one and round-trips it" to BooleanVector(true, 1),
)

private val uleb128Values = mapOf(
    "round-trips zero through the BCS reader and writer" to 0u,
    "round-trips the largest one-byte value through the BCS reader and writer" to 127u,
    "round-trips the smallest two-byte value through the BCS reader and writer" to 128u,
    "round-trips 624,485 through the BCS reader and writer" to 624_485u,
    "round-trips Int.MAX_VALUE through the BCS reader and writer" to Int.MAX_VALUE.toUInt(),
)

private data class IntegerCase(
    val verify: () -> Unit,
)

private val signedBoundaryCases = mapOf(
    "round-trips I8 minimum" to roundTripCase(I8, Byte.MIN_VALUE),
    "round-trips I8 zero" to roundTripCase(I8, 0.toByte()),
    "round-trips I8 maximum" to roundTripCase(I8, Byte.MAX_VALUE),
    "round-trips I16 minimum" to roundTripCase(I16, Short.MIN_VALUE),
    "round-trips I16 zero" to roundTripCase(I16, 0.toShort()),
    "round-trips I16 maximum" to roundTripCase(I16, Short.MAX_VALUE),
    "round-trips I32 minimum" to roundTripCase(I32, Int.MIN_VALUE),
    "round-trips I32 zero" to roundTripCase(I32, 0),
    "round-trips I32 maximum" to roundTripCase(I32, Int.MAX_VALUE),
    "round-trips I64 minimum" to roundTripCase(I64, Long.MIN_VALUE),
    "round-trips I64 zero" to roundTripCase(I64, 0L),
    "round-trips I64 maximum" to roundTripCase(I64, Long.MAX_VALUE),
)

private val unsignedBoundaryCases = mapOf(
    "round-trips U8 zero" to roundTripCase(U8, 0u.toUByte()),
    "round-trips U8 maximum" to roundTripCase(U8, UByte.MAX_VALUE),
    "round-trips U16 zero" to roundTripCase(U16, 0u.toUShort()),
    "round-trips U16 maximum" to roundTripCase(U16, UShort.MAX_VALUE),
    "round-trips U32 zero" to roundTripCase(U32, 0u),
    "round-trips U32 maximum" to roundTripCase(U32, UInt.MAX_VALUE),
    "round-trips U64 zero" to roundTripCase(U64, 0uL),
    "round-trips U64 maximum" to roundTripCase(U64, ULong.MAX_VALUE),
)

private val signedEncodingCases = mapOf(
    "serializes I8 in little-endian order" to encodingCase(I8, (-1).toByte(), bytes(0xFF)),
    "serializes I16 in little-endian order" to encodingCase(I16, 0x1234.toShort(), bytes(0x34, 0x12)),
    "serializes I32 in little-endian order" to encodingCase(I32, 0x12345678, bytes(0x78, 0x56, 0x34, 0x12)),
    "serializes I64 in little-endian order" to
        encodingCase(I64, 0x0102030405060708L, bytes(0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01)),
)

private val unsignedEncodingCases = mapOf(
    "serializes U8 in little-endian order" to encodingCase(U8, 0xFFu.toUByte(), bytes(0xFF)),
    "serializes U16 in little-endian order" to encodingCase(U16, 0xABCDu.toUShort(), bytes(0xCD, 0xAB)),
    "serializes U32 in little-endian order" to encodingCase(U32, 0x89ABCDEFu, bytes(0xEF, 0xCD, 0xAB, 0x89)),
    "serializes U64 in little-endian order" to
        encodingCase(U64, 0x0102030405060708uL, bytes(0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01)),
)

private fun <T> roundTripCase(type: BCSType<T>, value: T) = IntegerCase {
    val encoded = type.serialize(value).toByteArray()

    type.deserialize(encoded) shouldBe value
}

private fun <T> encodingCase(type: BCSType<T>, value: T, expected: ByteArray) = IntegerCase {
    type.serialize(value).toByteArray().shouldHaveBytes(expected)
}
