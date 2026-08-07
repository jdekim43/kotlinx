@file:OptIn(
    dev.whyoleg.cryptography.CryptographyProviderApi::class,
    dev.whyoleg.cryptography.DelicateCryptographyApi::class,
)

package kim.jade.security.crypto

import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.Digest
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.MD5
import dev.whyoleg.cryptography.algorithms.RIPEMD160
import dev.whyoleg.cryptography.algorithms.SHA1
import dev.whyoleg.cryptography.algorithms.SHA224
import dev.whyoleg.cryptography.algorithms.SHA256
import dev.whyoleg.cryptography.algorithms.SHA384
import dev.whyoleg.cryptography.algorithms.SHA3_224
import dev.whyoleg.cryptography.algorithms.SHA3_256
import dev.whyoleg.cryptography.algorithms.SHA3_384
import dev.whyoleg.cryptography.algorithms.SHA3_512
import dev.whyoleg.cryptography.algorithms.SHA512
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kim.jade.security.crypto.provider.kotlincrypto.KotlinCryptoHmac
import kim.jade.security.crypto.provider.kotlincrypto.KotlinCryptoProvider
import kim.jade.security.crypto.provider.pure.PureKotlinProvider
import kim.jade.security.shouldHaveHex

class KotlinCryptoProviderSpec : DescribeSpec({

    val provider = KotlinCryptoProvider()

    describe("KotlinCryptoProvider") {
        context("when its metadata and algorithm registry are queried") {
            it("exposes its provider name") {
                provider.name shouldBe "KotlinCrypto"
            }

            it("caches supported algorithms and returns null for unsupported algorithms") {
                val first = provider.getOrNull(SHA256)
                val second = provider.getOrNull(SHA256)

                first shouldBeSameInstanceAs second
                provider.getOrNull(AES.GCM).shouldBeNull()
            }
        }

        context("given the published fixed-length digest vectors for \"abc\"") {
            withData(fixedLengthDigestVectors) { vector ->
                it("returns the expected digest and output size") {
                    val digest = provider.getOrNull(vector.algorithm)!!
                        .hasher()
                        .hashBlocking("abc".encodeToByteArray())

                    digest.size shouldBe vector.expectedSize
                    digest.shouldHaveHex(vector.expectedHex)
                }
            }
        }

        context("given parameterized digest identifiers") {
            withData(parameterizedDigestVectors) { vector ->
                it("honors the requested output size") {
                    KotlinCryptoProvider().getOrNull(vector.algorithm)!!
                        .hasher()
                        .hashBlocking("parameterized digest".encodeToByteArray())
                        .size shouldBe vector.expectedSize
                }
            }
        }

        describe("an incremental SHA-256 hash function") {
            context("when a slice is hashed into a destination offset") {
                it("writes only the digest range") {
                    val function = provider.getOrNull(SHA256)!!.hasher().createHashFunction()
                    try {
                        val wrapped = "prefixabcsuffix".encodeToByteArray()
                        function.update(wrapped, startIndex = 6, endIndex = 9)

                        val destination = ByteArray(36) { 0x5a }
                        function.hashIntoByteArray(destination, destinationOffset = 2) shouldBe 32
                        destination.copyOfRange(2, 34).shouldHaveHex(
                            "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD",
                        )
                        destination[0] shouldBe 0x5a.toByte()
                        destination[35] shouldBe 0x5a.toByte()
                    } finally {
                        function.close()
                    }
                }
            }

            context("when it is reset") {
                it("starts hashing from an empty state") {
                    val function = provider.getOrNull(SHA256)!!.hasher().createHashFunction()
                    try {
                        function.update("abc".encodeToByteArray())
                        function.reset()
                        function.update(byteArrayOf())

                        function.hashToByteArray().shouldHaveHex(
                            "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855",
                        )
                    } finally {
                        function.close()
                    }
                }
            }

            context("when it is closed") {
                it("rejects hashing, reset, and update operations") {
                    val function = provider.getOrNull(SHA256)!!.hasher().createHashFunction()
                    function.close()

                    shouldThrow<IllegalStateException> { function.hashToByteArray() }
                    shouldThrow<IllegalStateException> { function.reset() }
                    shouldThrow<IllegalStateException> { function.update(byteArrayOf()) }
                }
            }
        }
    }

    describe("KotlinCryptoHmac") {
        context("when a RAW key is decoded") {
            it("defensively copies the supplied key bytes") {
                val keyBytes = ByteArray(20) { 0x0b }
                val key = KotlinCryptoHmac().keyDecoder(SHA256)
                    .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, keyBytes)
                keyBytes.fill(0)

                key.encodeToByteArrayBlocking(HMAC.Key.Format.RAW)
                    .all { it == 0x0b.toByte() }
                    .shouldBeTrue()
            }

            it("signs and verifies the RFC 4231 vector") {
                val key = KotlinCryptoHmac().keyDecoder(SHA256).decodeFromByteArrayBlocking(
                    HMAC.Key.Format.RAW,
                    ByteArray(20) { 0x0b },
                )
                val message = "Hi There".encodeToByteArray()
                val signature = key.signatureGenerator().generateSignatureBlocking(message)

                signature.shouldHaveHex("B0344C61D8DB38535CA8AFCEAF0BF12B881DC200C9833DA726E9376C2E32CFF7")
                key.signatureVerifier().tryVerifySignatureBlocking(message, signature).shouldBeTrue()

                val changed = signature.copyOf().also { it[0] = (it[0].toInt() xor 1).toByte() }
                key.signatureVerifier().tryVerifySignatureBlocking(message, changed).shouldBeFalse()
            }
        }

        context("when the JWK key format is requested") {
            it("rejects decoding") {
                shouldThrow<IllegalArgumentException> {
                    KotlinCryptoHmac().keyDecoder(SHA256)
                        .decodeFromByteArrayBlocking(HMAC.Key.Format.JWK, byteArrayOf())
                }
            }

            it("rejects encoding") {
                val key = KotlinCryptoHmac().keyDecoder(SHA256).decodeFromByteArrayBlocking(
                    HMAC.Key.Format.RAW,
                    ByteArray(20) { 0x0b },
                )

                shouldThrow<IllegalArgumentException> {
                    key.encodeToByteArrayBlocking(HMAC.Key.Format.JWK)
                }
            }
        }
    }
})

class PureKotlinProviderSpec : DescribeSpec({

    val provider = PureKotlinProvider()

    describe("PureKotlinProvider") {
        context("when its metadata and algorithm registry are queried") {
            it("exposes its provider name") {
                provider.name shouldBe "PureKotlin"
            }

            it("caches RIPEMD-160 and returns null for unsupported algorithms") {
                provider.getOrNull(RIPEMD160) shouldBeSameInstanceAs provider.getOrNull(RIPEMD160)
                provider.getOrNull(SHA256).shouldBeNull()
            }
        }

        context("given the standard RIPEMD-160 vectors") {
            withData(ripemd160Vectors) { vector ->
                it("returns the expected digest") {
                    provider.getOrNull(RIPEMD160)!!
                        .hasher()
                        .hashBlocking(vector.message.encodeToByteArray())
                        .shouldHaveHex(vector.expectedHex)
                }
            }
        }
    }
})

private data class DigestVector(
    val algorithm: CryptographyAlgorithmId<Digest>,
    val expectedSize: Int,
    val expectedHex: String,
)

private data class DigestSizeVector(
    val algorithm: CryptographyAlgorithmId<Digest>,
    val expectedSize: Int,
)

private data class MessageDigestVector(
    val message: String,
    val expectedHex: String,
)

private val fixedLengthDigestVectors = mapOf(
    "MD5" to DigestVector(MD5, 16, "900150983CD24FB0D6963F7D28E17F72"),
    "SHA-1" to DigestVector(SHA1, 20, "A9993E364706816ABA3E25717850C26C9CD0D89D"),
    "SHA-224" to DigestVector(SHA224, 28, "23097D223405D8228642A477BDA255B32AADBCE4BDA0B3F7E36C9DA7"),
    "SHA-256" to DigestVector(SHA256, 32, "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD"),
    "SHA-384" to DigestVector(SHA384, 48, "CB00753F45A35E8BB5A03D699AC65007272C32AB0EDED1631A8B605A43FF5BED8086072BA1E7CC2358BAECA134C825A7"),
    "SHA-512" to DigestVector(SHA512, 64, "DDAF35A193617ABACC417349AE20413112E6FA4E89A97EA20A9EEEE64B55D39A2192992A274FC1A836BA3C23A3FEEBBD454D4423643CE80E2A9AC94FA54CA49F"),
    "SHA3-224" to DigestVector(SHA3_224, 28, "E642824C3F8CF24AD09234EE7D3C766FC9A3A5168D0C94AD73B46FDF"),
    "SHA3-256" to DigestVector(SHA3_256, 32, "3A985DA74FE225B2045C172D6BD390BD855F086E3E9D525B46BFE24511431532"),
    "SHA3-384" to DigestVector(SHA3_384, 48, "EC01498288516FC926459F58E2C6AD8DF9B473CB0FC08C2596DA7CF0E49BE4B298D88CEA927AC7F539F1EDF228376D25"),
    "SHA3-512" to DigestVector(SHA3_512, 64, "B751850B1A57168A5693CD924B6B096E08F621827444F70D884F5D0240D2712E10E116E9192AF3C91A7EC57647E3934057340B4CF408D5A56592F8274EEC53F0"),
    "KECCAK-224" to DigestVector(KECCAK224, 28, "C30411768506EBE1C2871B1EE2E87D38DF342317300A9B97A95EC6A8"),
    "KECCAK-256" to DigestVector(KECCAK256, 32, "4E03657AEA45A94FC7D47BA826C8D667C0D1E6E33A64A036EC44F58FA12D6C45"),
    "KECCAK-384" to DigestVector(KECCAK384, 48, "F7DF1165F033337BE098E7D288AD6A2F74409D7A60B49C36642218DE161B1F99F8C681E4AFAF31A34DB29FB763E3C28E"),
    "KECCAK-512" to DigestVector(KECCAK512, 64, "18587DC2EA106B9A1563E32B3312421CA164C7F1F07BC922A9C83D77CEA3A1E5D0C69910739025372DC14AC9642629379540C17E2A65B19D77AA511A9D00BB96"),
)

private val parameterizedDigestVectors = mapOf(
    "SHA-512/256" to DigestSizeVector(SHA512t(256), 32),
    "SHAKE128 with default output" to DigestSizeVector(SHAKE128(), 32),
    "SHAKE128 with 17-byte output" to DigestSizeVector(SHAKE128(17), 17),
    "SHAKE256 with default output" to DigestSizeVector(SHAKE256(), 64),
    "SHAKE256 with 19-byte output" to DigestSizeVector(SHAKE256(19), 19),
    "cSHAKE128 with 18-byte output" to DigestSizeVector(
        CSHAKE128("N".encodeToByteArray(), "S".encodeToByteArray(), 18),
        18,
    ),
    "cSHAKE256 with 20-byte output" to DigestSizeVector(
        CSHAKE256("N".encodeToByteArray(), "S".encodeToByteArray(), 20),
        20,
    ),
    "ParallelHash128 with 21-byte output" to DigestSizeVector(
        ParallelHash128("custom".encodeToByteArray(), 8, 21),
        21,
    ),
    "ParallelHash256 with 22-byte output" to DigestSizeVector(
        ParallelHash256("custom".encodeToByteArray(), 8, 22),
        22,
    ),
    "TupleHash128 with 23-byte output" to DigestSizeVector(
        TupleHash128("custom".encodeToByteArray(), 23),
        23,
    ),
    "TupleHash256 with 24-byte output" to DigestSizeVector(
        TupleHash256("custom".encodeToByteArray(), 24),
        24,
    ),
    "BLAKE2b-256" to DigestSizeVector(BLAKE2b(256), 32),
    "BLAKE2s-128" to DigestSizeVector(BLAKE2s(128), 16),
)

private val ripemd160Vectors = mapOf(
    "empty message" to MessageDigestVector("", "9C1185A5C5E9FC54612808977EE8F548B2258D31"),
    "one-character message" to MessageDigestVector("a", "0BDC9D2D256B3EE9DAAE347BE6F4DC835A467FFE"),
    "three-character message" to MessageDigestVector("abc", "8EB208F7E05D987A9B044A8E98C6B087F15A0BFC"),
    "message digest" to MessageDigestVector("message digest", "5D0689EF49D2FAE572B881B123A85FFA21595F36"),
    "alphabet" to MessageDigestVector(
        "abcdefghijklmnopqrstuvwxyz",
        "F71C27109C692C1B56BBDCEB5B9D2865B3708DBC",
    ),
    "multi-block message" to MessageDigestVector(
        "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
        "12A053384A9C0C88E405A06C27DCF49ADA62EB2B",
    ),
)
