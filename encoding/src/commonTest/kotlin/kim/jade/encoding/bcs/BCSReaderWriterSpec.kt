package kim.jade.encoding.bcs

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kim.jade.encoding.bytes
import kim.jade.encoding.shouldHaveBytes

class BCSReaderWriterSpec : DescribeSpec({

    describe("BCSReader") {
        context("when initialized from a byte array") {
            it("copies its input and tracks byte reads") {
                val source = bytes(1, 2)
                val reader = BCSReader(source)
                source[0] = 99

                reader.length shouldBe 2
                reader.position shouldBe 0
                reader.isAtEnd() shouldBe false
                reader.readByte() shouldBe 1.toByte()
                reader.position shouldBe 1
                reader.readByte() shouldBe 2.toByte()
                reader.isAtEnd() shouldBe true
            }
        }

        context("when reading encoded values") {
            it("reads byte ranges in either byte order") {
                val reader = BCSReader(bytes(1, 2, 3, 4))

                reader.readBytes(2).shouldHaveBytes(1, 2)
                reader.readBytes(2, littleEndian = true).shouldHaveBytes(4, 3)
                reader.position shouldBe 4
            }

            it("reads ULEB values and advances by their encoded length") {
                val reader = BCSReader(bytes(0xE5, 0x8E, 0x26, 0x7F))

                reader.readULEB() shouldBe 624_485u
                reader.position shouldBe 3
                reader.readULEB() shouldBe 127u
                reader.isAtEnd() shouldBe true
            }

            it("passes the index and vector length to an element reader") {
                val reader = BCSReader(bytes(3, 10, 20, 30))

                val values = reader.readVector { index, length ->
                    Triple(readByte(), index, length)
                }

                values shouldBe listOf(
                    Triple(10.toByte(), 0, 3),
                    Triple(20.toByte(), 1, 3),
                    Triple(30.toByte(), 2, 3),
                )
                reader.isAtEnd() shouldBe true
            }
        }

        context("when a read extends beyond the available bytes") {
            it("fails instead of returning a partial value") {
                shouldThrowAny {
                    BCSReader(bytes(1)).readBytes(2)
                }
            }
        }
    }

    describe("BCSWriter") {
        context("when writing raw bytes") {
            it("chains writes, tracks length, and supports either byte order") {
                val writer = BCSWriter()
                    .writeByte(0x01.toByte())
                    .writeBytes(bytes(2, 3))
                    .writeBytes(listOf(4, 5).map(Int::toByte), littleEndian = true)
                    .writeBytes(bytes(6, 7), littleEndian = true)

                writer.length shouldBe 7
                writer.toByteArray().shouldHaveBytes(1, 2, 3, 5, 4, 7, 6)
            }

            it("returns a new byte array snapshot") {
                val writer = BCSWriter().writeBytes(bytes(1, 2, 3))
                val first = writer.toByteArray()
                first[0] = 99

                writer.toByteArray().shouldHaveBytes(1, 2, 3)
            }
        }

        context("when writing encoded values") {
            it("writes ULEB values") {
                BCSWriter()
                    .writeULEB(624_485u)
                    .toByteArray()
                    .shouldHaveBytes(0xE5, 0x8E, 0x26)
            }

            it("prefixes vectors and supplies callback metadata") {
                val writer = BCSWriter().writeVector(listOf(10, 20)) { value, index, length ->
                    writeByte(value.toByte())
                    writeByte(index.toByte())
                    writeByte(length.toByte())
                }

                writer.toByteArray().shouldHaveBytes(2, 10, 0, 2, 20, 1, 2)
            }
        }
    }
})
