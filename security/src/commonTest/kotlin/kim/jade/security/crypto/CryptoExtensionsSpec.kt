package kim.jade.security.crypto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kim.jade.encoding.Hex
import kim.jade.security.shouldHaveHex
import kr.jadekim.common.crypto.hash.HashException
import kr.jadekim.common.crypto.hash.HashFunction
import kr.jadekim.common.crypto.hash.KECCAK_224
import kr.jadekim.common.crypto.hash.KECCAK_256
import kr.jadekim.common.crypto.hash.KECCAK_384
import kr.jadekim.common.crypto.hash.KECCAK_512
import kr.jadekim.common.crypto.hash.MD5
import kr.jadekim.common.crypto.hash.RIPEMD160
import kr.jadekim.common.crypto.hash.SHA3_224
import kr.jadekim.common.crypto.hash.SHA3_256
import kr.jadekim.common.crypto.hash.SHA3_384
import kr.jadekim.common.crypto.hash.SHA3_512
import kr.jadekim.common.crypto.hash.SHA_1
import kr.jadekim.common.crypto.hash.SHA_224
import kr.jadekim.common.crypto.hash.SHA_256
import kr.jadekim.common.crypto.hash.SHA_384
import kr.jadekim.common.crypto.hash.SHA_512
import kr.jadekim.common.crypto.hash.hash as legacyHash

class CryptoExtensionsSpec : DescribeSpec({
    describe("legacy hash extensions") {
        context("given the published digest vectors for \"abc\"") {
            withData(legacyHashVectors) { vector ->
                val input = "abc"
                val inputBytes = input.encodeToByteArray()

                it("returns raw bytes from every receiver overload") {
                    vector.function.hash(input).shouldHaveHex(vector.expected)
                    vector.function.hash(inputBytes).shouldHaveHex(vector.expected)
                    input.legacyHash(vector.function).shouldHaveHex(vector.expected)
                    inputBytes.legacyHash(vector.function).shouldHaveHex(vector.expected)
                }

                it("returns encoded values from every encoder overload") {
                    vector.function.legacyHash(input, Hex) shouldBe vector.expected
                    vector.function.legacyHash(inputBytes, Hex) shouldBe vector.expected
                    input.legacyHash(vector.function, Hex) shouldBe vector.expected
                    inputBytes.legacyHash(vector.function, Hex) shouldBe vector.expected
                }
            }
        }
    }

    describe("function-backed HashFunction") {
        context("when the implementation throws") {
            it("wraps the original failure in HashException") {
                val cause = IllegalArgumentException("broken digest")
                val function = HashFunction { throw cause }

                val failure = shouldThrow<HashException> {
                    function.hash(byteArrayOf(1, 2, 3))
                }

                failure.message shouldBe cause.message
                failure.cause shouldBeSameInstanceAs cause
            }
        }
    }
})

private data class LegacyHashVector(
    val function: HashFunction,
    val expected: String,
)

private val legacyHashVectors = mapOf(
    "MD5" to LegacyHashVector(MD5, "900150983CD24FB0D6963F7D28E17F72"),
    "SHA-1" to LegacyHashVector(SHA_1, "A9993E364706816ABA3E25717850C26C9CD0D89D"),
    "SHA-224" to LegacyHashVector(SHA_224, "23097D223405D8228642A477BDA255B32AADBCE4BDA0B3F7E36C9DA7"),
    "SHA-256" to LegacyHashVector(SHA_256, "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD"),
    "SHA-384" to LegacyHashVector(SHA_384, "CB00753F45A35E8BB5A03D699AC65007272C32AB0EDED1631A8B605A43FF5BED8086072BA1E7CC2358BAECA134C825A7"),
    "SHA-512" to LegacyHashVector(SHA_512, "DDAF35A193617ABACC417349AE20413112E6FA4E89A97EA20A9EEEE64B55D39A2192992A274FC1A836BA3C23A3FEEBBD454D4423643CE80E2A9AC94FA54CA49F"),
    "KECCAK-224" to LegacyHashVector(KECCAK_224, "C30411768506EBE1C2871B1EE2E87D38DF342317300A9B97A95EC6A8"),
    "KECCAK-256" to LegacyHashVector(KECCAK_256, "4E03657AEA45A94FC7D47BA826C8D667C0D1E6E33A64A036EC44F58FA12D6C45"),
    "KECCAK-384" to LegacyHashVector(KECCAK_384, "F7DF1165F033337BE098E7D288AD6A2F74409D7A60B49C36642218DE161B1F99F8C681E4AFAF31A34DB29FB763E3C28E"),
    "KECCAK-512" to LegacyHashVector(KECCAK_512, "18587DC2EA106B9A1563E32B3312421CA164C7F1F07BC922A9C83D77CEA3A1E5D0C69910739025372DC14AC9642629379540C17E2A65B19D77AA511A9D00BB96"),
    "SHA3-224" to LegacyHashVector(SHA3_224, "E642824C3F8CF24AD09234EE7D3C766FC9A3A5168D0C94AD73B46FDF"),
    "SHA3-256" to LegacyHashVector(SHA3_256, "3A985DA74FE225B2045C172D6BD390BD855F086E3E9D525B46BFE24511431532"),
    "SHA3-384" to LegacyHashVector(SHA3_384, "EC01498288516FC926459F58E2C6AD8DF9B473CB0FC08C2596DA7CF0E49BE4B298D88CEA927AC7F539F1EDF228376D25"),
    "SHA3-512" to LegacyHashVector(SHA3_512, "B751850B1A57168A5693CD924B6B096E08F621827444F70D884F5D0240D2712E10E116E9192AF3C91A7EC57647E3934057340B4CF408D5A56592F8274EEC53F0"),
    "RIPEMD-160" to LegacyHashVector(RIPEMD160, "8EB208F7E05D987A9B044A8E98C6B087F15A0BFC"),
)
