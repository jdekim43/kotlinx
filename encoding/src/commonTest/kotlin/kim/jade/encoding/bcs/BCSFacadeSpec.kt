package kim.jade.encoding.bcs

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kim.jade.encoding.bcs.type.BCSType
import kim.jade.encoding.bcs.type.Bool
import kim.jade.encoding.bcs.type.ByteVector
import kim.jade.encoding.bcs.type.I128
import kim.jade.encoding.bcs.type.I16
import kim.jade.encoding.bcs.type.I256
import kim.jade.encoding.bcs.type.I32
import kim.jade.encoding.bcs.type.I64
import kim.jade.encoding.bcs.type.I8
import kim.jade.encoding.bcs.type.String
import kim.jade.encoding.bcs.type.U128
import kim.jade.encoding.bcs.type.U16
import kim.jade.encoding.bcs.type.U256
import kim.jade.encoding.bcs.type.U32
import kim.jade.encoding.bcs.type.U64
import kim.jade.encoding.bcs.type.U8
import kim.jade.encoding.bcs.type.ULEB128
import kim.jade.encoding.bytes
import kim.jade.encoding.shouldHaveBytes

class BCSFacadeSpec : DescribeSpec({

    describe("BCS byte-vector encoder") {
        context("when encoding and decoding through the Encoder API") {
            it("round-trips non-empty and empty byte vectors") {
                val value = bytes(0, 1, 0xFE, 0xFF)
                val encoded = BCS.encode(value)

                encoded.shouldHaveBytes(4, 0, 1, 0xFE, 0xFF)
                BCS.decode(encoded).shouldHaveBytes(value)
                BCS.encode(byteArrayOf()).shouldHaveBytes(0)
                BCS.decode(bytes(0)).shouldHaveBytes()
            }
        }
    }

    describe("primitive type factories") {
        context("when requested from the BCS facade") {
            it("returns the corresponding primitive types") {
                BCS.bool() shouldBe Bool
                BCS.byteVector() shouldBe ByteVector
                BCS.string() shouldBe String
                BCS.i8() shouldBe I8
                BCS.i16() shouldBe I16
                BCS.i32() shouldBe I32
                BCS.i64() shouldBe I64
                BCS.i128() shouldBe I128
                BCS.i256() shouldBe I256
                BCS.u8() shouldBe U8
                BCS.u16() shouldBe U16
                BCS.u32() shouldBe U32
                BCS.u64() shouldBe U64
                BCS.u128() shouldBe U128
                BCS.u256() shouldBe U256
                BCS.uleb128() shouldBe ULEB128
                BCS.bytes(7).size shouldBe 7
            }
        }
    }

    describe("composite type factories") {
        context("when configured from the BCS facade") {
            it("preserves every supplied component type and size") {
                val fixedArray = BCS.fixedArray(3, U8)
                fixedArray.size shouldBe 3
                fixedArray.type shouldBe U8

                val vector = BCS.vector(U16)
                vector.type shouldBe U16

                val option = BCS.option(U32)
                option.type shouldBe U32

                val pair = BCS.pair(U8, U16)
                pair.first shouldBe U8
                pair.second shouldBe U16

                val triple = BCS.triple(U8, U16, U32)
                triple.first shouldBe U8
                triple.second shouldBe U16
                triple.third shouldBe U32

                val map = BCS.map(U8, U64)
                map.keyType shouldBe U8
                map.valueType shouldBe U64

                val fields = mapOf("value" to U8.erased())
                BCS.enum(fields).types shouldBe fields
                BCS.struct(fields).types shouldBe fields
            }
        }
    }
})

@Suppress("UNCHECKED_CAST")
private fun <T> BCSType<T>.erased(): BCSType<Any> = this as BCSType<Any>
