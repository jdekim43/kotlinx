package kim.jade.encoding.bcs.type

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kim.jade.encoding.bytes
import kim.jade.encoding.shouldHaveBytes

class CompositeBCSTypeSpec : DescribeSpec({

    describe("collection types") {
        context("when serializing a Vector") {
            it("uses a ULEB length prefix and round-trips its elements") {
                val type = Vector(U16)
                val value = listOf(1u.toUShort(), 0xABCDu.toUShort())

                type.serialize(value).toByteArray().shouldHaveBytes(2, 1, 0, 0xCD, 0xAB)
                type.deserialize(type.serialize(value).toByteArray()) shouldBe value
                Vector(U8).serialize(emptyList()).toByteArray().shouldHaveBytes(0)
            }
        }

        context("when serializing a FixedArray") {
            it("omits a length prefix and round-trips its elements") {
                val type = FixedArray(3, U8)
                val value = listOf(1u.toUByte(), 2u.toUByte(), 255u.toUByte())

                type.serialize(value).toByteArray().shouldHaveBytes(1, 2, 0xFF)
                type.deserialize(bytes(1, 2, 0xFF)) shouldBe value
            }
        }

        context("when serializing a Map") {
            it("preserves iteration order and round-trips its entries") {
                val type = Map(U8, U16)
                val value = linkedMapOf(
                    2u.toUByte() to 0x1234u.toUShort(),
                    1u.toUByte() to 0xABCDu.toUShort(),
                )

                val encoded = type.serialize(value).toByteArray()

                encoded.shouldHaveBytes(2, 2, 0x34, 0x12, 1, 0xCD, 0xAB)
                type.deserialize(encoded) shouldBe value
            }
        }
    }

    describe("optional and product types") {
        context("when serializing an Option") {
            it("distinguishes absent and present values") {
                val type = Option(U16)

                type.serialize(null).toByteArray().shouldHaveBytes(0)
                type.deserialize(bytes(0)) shouldBe null
                type.serialize(0x1234u.toUShort()).toByteArray().shouldHaveBytes(1, 0x34, 0x12)
                type.deserialize(bytes(1, 0x34, 0x12)) shouldBe 0x1234u.toUShort()
            }
        }

        context("when serializing Pair and Triple values") {
            it("writes and reads fields in declaration order") {
                val pair = Pair(U8, U16)
                val pairValue = 7u.toUByte() to 0x1234u.toUShort()
                pair.serialize(pairValue).toByteArray().shouldHaveBytes(7, 0x34, 0x12)
                pair.deserialize(pair.serialize(pairValue).toByteArray()) shouldBe pairValue

                val triple = Triple(U8, Bool, U16)
                val tripleValue = kotlin.Triple(7u.toUByte(), true, 0x1234u.toUShort())
                triple.serialize(tripleValue).toByteArray().shouldHaveBytes(7, 1, 0x34, 0x12)
                triple.deserialize(triple.serialize(tripleValue).toByteArray()) shouldBe tripleValue
            }
        }

        context("when composites are nested") {
            it("round-trips every level of the value") {
                val type = Vector(Option(Pair(U8, Bool)))
                val value = listOf(
                    7u.toUByte() to true,
                    null,
                    255u.toUByte() to false,
                )

                val encoded = type.serialize(value).toByteArray()

                encoded.shouldHaveBytes(3, 1, 7, 1, 0, 1, 0xFF, 0)
                type.deserialize(encoded) shouldBe value
            }
        }
    }

    describe("named aggregate types") {
        context("when serializing an Enum") {
            it("uses sorted variant names as indexes") {
                val type = Enum(
                    linkedMapOf(
                        "zeta" to U16.erased(),
                        "alpha" to U8.erased(),
                    ),
                )

                type.serialize("alpha" to 7u.toUByte()).toByteArray().shouldHaveBytes(0, 7)
                type.serialize("zeta" to 0x1234u.toUShort()).toByteArray().shouldHaveBytes(1, 0x34, 0x12)
                type.deserialize(bytes(1, 0x34, 0x12)) shouldBe ("zeta" to 0x1234u.toUShort())
            }

            it("rejects an unknown variant name") {
                val type = Enum(mapOf("known" to U8.erased()))

                shouldThrow<IllegalArgumentException> {
                    type.serialize("unknown" to 1u.toUByte())
                }.message shouldBe "Invalid enum key: unknown"
            }
        }

        context("when serializing a Struct") {
            it("writes and reads fields in sorted name order") {
                val type = Struct(
                    linkedMapOf(
                        "zeta" to U16.erased(),
                        "alpha" to U8.erased(),
                    ),
                )
                val value = linkedMapOf<kotlin.String, Any>(
                    "zeta" to 0x1234u.toUShort(),
                    "alpha" to 7u.toUByte(),
                )

                val encoded = type.serialize(value).toByteArray()

                encoded.shouldHaveBytes(7, 0x34, 0x12)
                type.deserialize(encoded) shouldBe mapOf(
                    "alpha" to 7u.toUByte(),
                    "zeta" to 0x1234u.toUShort(),
                )
            }
        }
    }
})

@Suppress("UNCHECKED_CAST")
private fun <T> BCSType<T>.erased(): BCSType<Any> = this as BCSType<Any>
