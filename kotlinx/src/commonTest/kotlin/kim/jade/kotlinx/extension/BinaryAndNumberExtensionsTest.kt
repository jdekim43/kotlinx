package kim.jade.kotlinx.extension

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class BinaryAndNumberExtensionsTest : DescribeSpec({

    describe("ByteArray padding and writes") {
        context("when padding to a larger size") {
            it("fills the requested side without changing the source") {
                val source = byteArrayOf(1, 2)

                source.padStart(5, 9).contentEquals(byteArrayOf(9, 9, 9, 1, 2)) shouldBe true
                source.padEnd(5, 9).contentEquals(byteArrayOf(1, 2, 9, 9, 9)) shouldBe true
                source.padStart(source.size).contentEquals(source) shouldBe true
                source.padEnd(source.size).contentEquals(source) shouldBe true
                source.contentEquals(byteArrayOf(1, 2)) shouldBe true
            }
        }

        context("when writing byte values at offsets") {
            it("stores scalar and array values in the target") {
                val target = ByteArray(6)

                target.write(0, 0x7f.toByte())
                target.write(1, 0x80u.toUByte())
                target.write(2, byteArrayOf(3, 4, 5))

                target.contentEquals(byteArrayOf(0x7f, 0x80.toByte(), 3, 4, 5, 0)) shouldBe true
            }
        }

        context("when writing fixed-width numbers") {
            it("honors the requested byte order") {
                val target = ByteArray(28)

                target.write(0, 0x1234.toShort())
                target.write(2, 0x5678u.toUShort(), littleEndian = true)
                target.write(4, 0x12345678)
                target.write(8, 0x90abcdefu, littleEndian = true)
                target.write(12, 0x0102030405060708L)
                target.write(20, 0x8899aabbccddeeffuL, littleEndian = true)

                target.contentEquals(
                    byteArrayOf(
                        0x12, 0x34,
                        0x78, 0x56,
                        0x12, 0x34, 0x56, 0x78,
                        0xef.toByte(), 0xcd.toByte(), 0xab.toByte(), 0x90.toByte(),
                        0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                        0xff.toByte(), 0xee.toByte(), 0xdd.toByte(), 0xcc.toByte(),
                        0xbb.toByte(), 0xaa.toByte(), 0x99.toByte(), 0x88.toByte(),
                    )
                ) shouldBe true
            }
        }
    }

    describe("binary conversions") {
        context("when converting numeric values to bits") {
            it("orders the bits from most to least significant") {
                0xa5.toByte().toBinary().contentEquals(
                    booleanArrayOf(true, false, true, false, false, true, false, true)
                ) shouldBe true

                5.toBinary().takeLast(8) shouldBe
                    listOf(false, false, false, false, false, true, false, true)
                (-1).toBinary().all { it } shouldBe true
            }
        }

        context("when converting a byte array to bits") {
            it("flattens each byte's representation in order") {
                byteArrayOf(0x80.toByte(), 0x01).toBinary().contentEquals(
                    booleanArrayOf(
                        true, false, false, false, false, false, false, false,
                        false, false, false, false, false, false, false, true,
                    )
                ) shouldBe true
            }
        }

        context("when converting a complete byte of bits") {
            it("produces equivalent numeric and byte-array values") {
                val bits = booleanArrayOf(true, false, true, false, false, true, false, true)

                bits.toInt() shouldBe 0xa5
                bits.toByte() shouldBe 0xa5.toByte()
                bits.toByteArray().contentEquals(byteArrayOf(0xa5.toByte())) shouldBe true
            }
        }

        context("when a bit array does not contain complete bytes") {
            it("rejects conversion to a byte array") {
                shouldThrow<IllegalArgumentException> {
                    booleanArrayOf(true, false).toByteArray()
                }.message shouldBe "Invalid size"
            }
        }
    }

    describe("fixed-width numeric conversions") {
        context("given signed values in either byte order") {
            withData(signedRoundTripCases) { case ->
                it("round-trips the original value") {
                    case.verify()
                }
            }
        }

        context("given unsigned values in either byte order") {
            withData(unsignedRoundTripCases) { case ->
                it("preserves every bit") {
                    case.verify()
                }
            }
        }

        context("given a known byte pattern") {
            withData(serializedNumberCases) { case ->
                it("serializes bytes in the requested order") {
                    case.serialize().contentEquals(case.expected) shouldBe true
                }
            }
        }
    }

    describe("numeric helpers") {
        context("when converting time units") {
            it("multiplies by the size of the next unit") {
                2.dayToHour() shouldBe 48
                2.hourToMinute() shouldBe 120
                2.minuteToSecond() shouldBe 120
                2.secondToMillisecond() shouldBe 2_000

                3L.dayToHour() shouldBe 72L
                3L.hourToMinute() shouldBe 180L
                3L.minuteToSecond() shouldBe 180L
                3L.secondToMillisecond() shouldBe 3_000L
            }
        }

        context("when converting numbers to booleans or comparing values") {
            it("handles zero, signs, ordering, and equal values") {
                0.toBoolean() shouldBe false
                1.toBoolean() shouldBe true
                (-1).toBoolean() shouldBe true

                min("a", "b") shouldBe "a"
                min(5, 5) shouldBe 5
                max("a", "b") shouldBe "b"
                max(5, 5) shouldBe 5
            }
        }

        context("when formatting with a custom alphabet") {
            it("supports signed and unsigned numeric types") {
                val hex = "0123456789ABCDEF".toCharArray()

                255.toString(hex) shouldBe "FF"
                255u.toString(hex) shouldBe "FF"
                65_535L.toString(hex) shouldBe "FFFF"
                65_535uL.toString(hex) shouldBe "FFFF"
                5.toString("ab".toCharArray()) shouldBe "bab"
            }
        }

        context("when the radix exceeds the custom alphabet") {
            it("rejects the conversion") {
                shouldThrow<IllegalArgumentException> {
                    3.toString(charArrayOf('0', '1'), radix = 3)
                }.message shouldBe "Too large radix (support max 2)"
            }
        }
    }
})

private data class NumericRoundTripCase(
    val verify: () -> Unit,
)

private data class SerializedNumberCase(
    val serialize: () -> ByteArray,
    val expected: ByteArray,
)

private val signedRoundTripCases = ByteOrder.entries.flatMap { order ->
    listOf<Short>(Short.MIN_VALUE, -1, 0, 0x1234, Short.MAX_VALUE).map { value ->
        "Short $value / $order" to NumericRoundTripCase {
            value.toByteArray(order).toShort(order) shouldBe value
        }
    } + listOf(Int.MIN_VALUE, -1, 0, 0x12345678, Int.MAX_VALUE).map { value ->
        "Int $value / $order" to NumericRoundTripCase {
            value.toByteArray(order).toInt(order) shouldBe value
        }
    } + listOf(Long.MIN_VALUE, -1L, 0L, 0x0102030405060708L, Long.MAX_VALUE).map { value ->
        "Long $value / $order" to NumericRoundTripCase {
            value.toByteArray(order).toLong(order) shouldBe value
        }
    }
}.toMap()

private val unsignedRoundTripCases = ByteOrder.entries.flatMap { order ->
    listOf(UShort.MIN_VALUE, 1u.toUShort(), UShort.MAX_VALUE).map { value ->
        "UShort $value / $order" to NumericRoundTripCase {
            value.toByteArray(order).toUShort(order) shouldBe value
        }
    } + listOf(UInt.MIN_VALUE, 1u, 0x89abcdefu, UInt.MAX_VALUE).map { value ->
        "UInt $value / $order" to NumericRoundTripCase {
            value.toByteArray(order).toUInt(order) shouldBe value
        }
    } + listOf(ULong.MIN_VALUE, 1uL, 0x8899aabbccddeeffuL, ULong.MAX_VALUE).map { value ->
        "ULong $value / $order" to NumericRoundTripCase {
            value.toByteArray(order).toULong(order) shouldBe value
        }
    }
}.toMap()

private val serializedNumberCases = mapOf(
    "Short / big endian" to SerializedNumberCase(
        { 0x1234.toShort().toByteArray(ByteOrder.BIG_ENDIAN) },
        byteArrayOf(0x12, 0x34),
    ),
    "Short / little endian" to SerializedNumberCase(
        { 0x1234.toShort().toByteArray(ByteOrder.LITTLE_ENDIAN) },
        byteArrayOf(0x34, 0x12),
    ),
    "Int / big endian" to SerializedNumberCase(
        { 0x01020304.toByteArray(ByteOrder.BIG_ENDIAN) },
        byteArrayOf(1, 2, 3, 4),
    ),
    "Int / little endian" to SerializedNumberCase(
        { 0x01020304.toByteArray(ByteOrder.LITTLE_ENDIAN) },
        byteArrayOf(4, 3, 2, 1),
    ),
)
