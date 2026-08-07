package com.carterharrison.ecdsa

import com.carterharrison.ecdsa.curves.Secp256k1
import com.carterharrison.ecdsa.curves.Secp256r1
import com.carterharrison.ecdsa.hash.EcHasher
import com.carterharrison.ecdsa.hash.EcSha256
import com.carterharrison.ecdsa.hash.EcSha512
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kim.jade.security.shouldHaveHex

class PointMathSpec : DescribeSpec({

    val curve = Secp256k1
    val twoG = EcPoint(
        hex("C6047F9441ED7D6D3045406E95C07CD85C778E4B8CEF3CA7ABAC09B95C709EE5"),
        hex("1AE168FEA63DC339A3C58419466CEAEEF7F632653266D0E1236431A950CFE52A"),
        curve,
    )
    val threeG = EcPoint(
        hex("F9308A019258C31049344F85F89D5229B531C845836F99B08601F113BCE036F9"),
        hex("388F7B0F632DE8140FE337E62A37F3566500A99934C2231B6CB9FD7584B8E672"),
        curve,
    )

    describe("PointMath") {
        context("when operating in a prime field") {
            it("performs modular multiplication and division") {
                val prime = 17.toBigInteger()

                PointMath.multiply(8.toBigInteger(), 9.toBigInteger(), prime) shouldBe 4.toBigInteger()
                PointMath.divide(6.toBigInteger(), 3.toBigInteger(), prime) shouldBe 2.toBigInteger()
            }
        }

        context("given known secp256k1 generator multiples") {
            it("computes 2G through doubling, addition, and scalar multiplication") {
                PointMath.double(curve.g) shouldBe twoG
                curve.add(curve.g, curve.g) shouldBe twoG
                curve.g * 2.toBigInteger() shouldBe twoG
            }

            it("computes 3G through addition and scalar multiplication") {
                twoG + curve.g shouldBe threeG
                curve * 3.toBigInteger() shouldBe threeG
            }
        }

        context("given the identity and additive inverse") {
            val identity = curve.identity
            val inverse = EcPoint(curve.g.x, curve.p - curve.g.y, curve)

            it("treats the identity as the neutral element") {
                curve.g + identity shouldBe curve.g
                identity + curve.g shouldBe curve.g
                PointMath.double(identity) shouldBe identity
            }

            it("adds inverse points to the identity") {
                curve.g + inverse shouldBe identity
            }

            withData(
                mapOf(
                    "zero scalar" to ScalarMultipleVector(BigInteger.ZERO, identity),
                    "unit scalar" to ScalarMultipleVector(BigInteger.ONE, curve.g),
                    "curve-order scalar" to ScalarMultipleVector(curve.n, identity),
                ),
            ) { vector ->
                it("returns the expected boundary scalar multiple") {
                    curve.g * vector.scalar shouldBe vector.expected
                }
            }
        }

        context("when the generator tangent is used for a dot operation") {
            it("derives the known doubled point") {
                val tangent = PointMath.tangent(curve.g, curve)

                PointMath.dot(curve.g, curve.g, tangent, curve) shouldBe twoG
            }
        }
    }
})

class EcPointSpec : DescribeSpec({
    describe("EcPoint") {
        context("when coordinates contain a high unsigned bit") {
            it("round trips them through unsigned byte arrays") {
                val original = EcPoint(
                    BigInteger.parseString("80", 16),
                    BigInteger.parseString("FF", 16),
                    Secp256k1,
                )

                val restored = EcPoint.fromByteArray(original.xByteArray, original.yByteArray, Secp256k1)

                restored shouldBe original
                restored.hashCode() shouldBe original.hashCode()
                restored.toString() shouldBe "${original.x}, ${original.y}"
            }
        }

        context("given the standard curve generators") {
            withData(standardCurves) { curve ->
                it("satisfies the curve equation") {
                    val left = (curve.g.y * curve.g.y) % curve.p
                    val right =
                        (curve.g.x * curve.g.x * curve.g.x + curve.a * curve.g.x + curve.b) % curve.p

                    left shouldBe right
                }

                it("represents the identity as (p, 0)") {
                    curve.identity.x shouldBe curve.p
                    curve.identity.y shouldBe BigInteger.ZERO
                }
            }
        }

        context("when compared with a different value type") {
            it("is not equal") {
                Secp256k1.g.equals("not a point").shouldBeFalse()
            }
        }
    }
})

class EcKeyAndSignatureSpec : DescribeSpec({
    describe("EcKeyGenerator") {
        context("given known private scalars") {
            withData(knownPrivateScalars) { scalar ->
                it("preserves the private scalar and derives its public multiple") {
                    val keyPair = EcKeyGenerator.newInstance(scalar, Secp256k1)

                    keyPair.privateKey shouldBe scalar
                    keyPair.publicKey shouldBe Secp256k1.g * scalar
                }
            }
        }

        context("when a random key pair is requested") {
            it("generates a private scalar and matching public key on the requested curve") {
                val keyPair = EcKeyGenerator.newInstance(Secp256k1)

                (keyPair.privateKey >= BigInteger.ZERO).shouldBeTrue()
                (keyPair.privateKey < Secp256k1.p).shouldBeTrue()
                keyPair.publicKey shouldBe Secp256k1.g * keyPair.privateKey
            }
        }
    }

    describe("EcSign") {
        context("given a valid key pair and message") {
            it("creates in-range components and verifies the signature") {
                val keyPair = EcKeyGenerator.newInstance(7.toBigInteger(), Secp256k1)
                val message = "signed message".encodeToByteArray()
                val signature = EcSign.signData(keyPair, message, EcSha256)

                (signature.r >= BigInteger.ONE && signature.r < Secp256k1.n).shouldBeTrue()
                (signature.s >= BigInteger.ONE && signature.s < Secp256k1.n).shouldBeTrue()
                EcSign.verifySignature(keyPair.publicKey, message, EcSha256, signature).shouldBeTrue()
            }

            it("rejects an altered message and a different public key") {
                val keyPair = EcKeyGenerator.newInstance(7.toBigInteger(), Secp256k1)
                val otherKey = EcKeyGenerator.newInstance(11.toBigInteger(), Secp256k1)
                val message = "signed message".encodeToByteArray()
                val signature = EcSign.signData(keyPair, message, EcSha256)

                EcSign.verifySignature(
                    keyPair.publicKey,
                    "altered message".encodeToByteArray(),
                    EcSha256,
                    signature,
                ).shouldBeFalse()
                EcSign.verifySignature(otherKey.publicKey, message, EcSha256, signature).shouldBeFalse()
            }
        }

        context("given signature components outside the curve order") {
            withData(invalidSignatures) { signature ->
                it("rejects the signature") {
                    val publicKey = EcKeyGenerator.newInstance(BigInteger.ONE, Secp256k1).publicKey
                    val message = "range checks".encodeToByteArray()

                    EcSign.verifySignature(publicKey, message, EcSha256, signature).shouldBeFalse()
                }
            }
        }
    }

    describe("ECDSA hash adapters") {
        context("given the SHA-2 vectors for \"abc\"") {
            withData(ecdsaHashVectors) { vector ->
                it("returns the expected digest") {
                    vector.hasher.hash("abc".encodeToByteArray()).shouldHaveHex(vector.expectedHex)
                }
            }
        }
    }
})

private data class ScalarMultipleVector(
    val scalar: BigInteger,
    val expected: EcPoint,
)

private data class EcdsaHashVector(
    val hasher: EcHasher,
    val expectedHex: String,
)

private val standardCurves: Map<String, EcCurve> = mapOf(
    "secp256k1" to Secp256k1,
    "secp256r1" to Secp256r1,
)

private val knownPrivateScalars = mapOf(
    "private scalar 1" to BigInteger.ONE,
    "private scalar 2" to 2.toBigInteger(),
)

private val invalidSignatures = mapOf(
    "r is zero" to EcSignature(BigInteger.ZERO, BigInteger.ONE),
    "r equals the curve order" to EcSignature(Secp256k1.n, BigInteger.ONE),
    "s is zero" to EcSignature(BigInteger.ONE, BigInteger.ZERO),
    "s equals the curve order" to EcSignature(BigInteger.ONE, Secp256k1.n),
)

private val ecdsaHashVectors = mapOf(
    "SHA-256" to EcdsaHashVector(
        EcSha256,
        "BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD",
    ),
    "SHA-512" to EcdsaHashVector(
        EcSha512(),
        "DDAF35A193617ABACC417349AE20413112E6FA4E89A97EA20A9EEEE64B55D39A" +
            "2192992A274FC1A836BA3C23A3FEEBBD454D4423643CE80E2A9AC94FA54CA49F",
    ),
)

private fun hex(value: String): BigInteger = BigInteger.parseString(value, 16)
