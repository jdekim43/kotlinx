@file:OptIn(CryptographyProviderApi::class, DelicateCryptographyApi::class)

package kim.jade.security.crypto.provider.kotlincrypto

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.*
import kim.jade.security.crypto.*

class KotlinCryptoProvider : CryptographyProvider() {

    override val name: String = "KotlinCrypto"

    private val cache = mutableMapOf<CryptographyAlgorithmId<*>, CryptographyAlgorithm?>()

    @Suppress("UNCHECKED_CAST")
    override fun <A : CryptographyAlgorithm> getOrNull(identifier: CryptographyAlgorithmId<A>): A? =
        cache.getOrPut(identifier) {
            when (identifier) {
                is MD5 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.md.MD5() })
                is SHA1 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha1.SHA1() })
                is SHA224 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha2.SHA224() })
                is SHA256 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha2.SHA256() })
                is SHA384 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha2.SHA384() })
                is SHA512 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha2.SHA512() })
                is SHA512t -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha2.SHA512t(identifier.t) })
                is SHA3_224 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.SHA3_224() })
                is SHA3_256 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.SHA3_256() })
                is SHA3_384 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.SHA3_384() })
                is SHA3_512 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.SHA3_512() })
                is KECCAK224 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.Keccak224() })
                is KECCAK256 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.Keccak256() })
                is KECCAK384 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.Keccak384() })
                is KECCAK512 -> KotlinCryptoDigest(identifier, { org.kotlincrypto.hash.sha3.Keccak512() })
                is SHAKE128 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.SHAKE128(identifier.outputLength)
                        else -> org.kotlincrypto.hash.sha3.SHAKE128()
                    }
                })

                is SHAKE256 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.SHAKE256(identifier.outputLength)
                        else -> org.kotlincrypto.hash.sha3.SHAKE256()
                    }
                })

                is CSHAKE128 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.CSHAKE128(
                            identifier.N,
                            identifier.S,
                            identifier.outputLength
                        )

                        else -> org.kotlincrypto.hash.sha3.CSHAKE128(identifier.N, identifier.S)
                    }
                })

                is CSHAKE256 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.CSHAKE256(
                            identifier.N,
                            identifier.S,
                            identifier.outputLength
                        )

                        else -> org.kotlincrypto.hash.sha3.CSHAKE256(identifier.N, identifier.S)
                    }
                })

                is ParallelHash128 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.ParallelHash128(
                            identifier.S,
                            identifier.B,
                            identifier.outputLength
                        )

                        else -> org.kotlincrypto.hash.sha3.ParallelHash128(identifier.S, identifier.B)
                    }
                })

                is ParallelHash256 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.ParallelHash256(
                            identifier.S,
                            identifier.B,
                            identifier.outputLength
                        )

                        else -> org.kotlincrypto.hash.sha3.ParallelHash256(identifier.S, identifier.B)
                    }
                })

                is TupleHash128 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.TupleHash128(
                            identifier.S,
                            identifier.outputLength
                        )

                        else -> org.kotlincrypto.hash.sha3.TupleHash128(identifier.S)
                    }
                })

                is TupleHash256 -> KotlinCryptoDigest(identifier, {
                    when {
                        identifier.outputLength != null -> org.kotlincrypto.hash.sha3.TupleHash256(
                            identifier.S,
                            identifier.outputLength
                        )

                        else -> org.kotlincrypto.hash.sha3.TupleHash256(identifier.S)
                    }
                })

                is BLAKE2b -> KotlinCryptoDigest(
                    identifier,
                    { org.kotlincrypto.hash.blake2.BLAKE2b(identifier.bitStrength) })

                is BLAKE2s -> KotlinCryptoDigest(
                    identifier,
                    { org.kotlincrypto.hash.blake2.BLAKE2s(identifier.bitStrength) })

                is HMAC -> KotlinCryptoHmac()
                else -> null
            }
        } as? A
}
