@file:OptIn(CryptographyProviderApi::class, DelicateCryptographyApi::class)

package kim.jade.security.crypto.provider.kotlincrypto

import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.CryptographyProviderApi
import dev.whyoleg.cryptography.CryptographySystem
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.*
import dev.whyoleg.cryptography.materials.Decoder
import dev.whyoleg.cryptography.operations.*
import kim.jade.kotlinx.pool.ObjectPool
import org.kotlincrypto.macs.hmac.Hmac
import org.kotlincrypto.macs.hmac.sha1.HmacSHA1
import org.kotlincrypto.macs.hmac.sha2.HmacSHA224
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import org.kotlincrypto.macs.hmac.sha2.HmacSHA384
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512
import org.kotlincrypto.macs.hmac.sha3.HmacSHA3_224
import org.kotlincrypto.macs.hmac.sha3.HmacSHA3_256
import org.kotlincrypto.macs.hmac.sha3.HmacSHA3_384
import org.kotlincrypto.macs.hmac.sha3.HmacSHA3_512
import kotlin.math.min

internal class KotlinCryptoHmac : HMAC {

    override fun keyDecoder(digest: CryptographyAlgorithmId<Digest>): Decoder<HMAC.Key.Format, HMAC.Key> =
        KotlinCryptoKeyDecoder(digest)

    override fun keyGenerator(digest: CryptographyAlgorithmId<Digest>): KeyGenerator<HMAC.Key> =
        KotlinCryptoKeyGenerator(digest)

    private inner class KotlinCryptoKeyDecoder(private val digest: CryptographyAlgorithmId<Digest>) :
        Decoder<HMAC.Key.Format, HMAC.Key> {
        override fun decodeFromByteArrayBlocking(format: HMAC.Key.Format, bytes: ByteArray): HMAC.Key {
            val rawKey = when (format) {
                HMAC.Key.Format.RAW -> bytes.copyOf()
                HMAC.Key.Format.JWK -> throw IllegalArgumentException("JWK decoding not supported")
            }

            return HmacKey(rawKey, digest)
        }
    }

    private inner class KotlinCryptoKeyGenerator(private val digest: CryptographyAlgorithmId<Digest>) :
        KeyGenerator<HMAC.Key> {
        override fun generateKeyBlocking(): HMAC.Key {
            val key = CryptographySystem.getDefaultRandom().nextBytes(digest.blockSize())

            return HmacKey(key, digest)
        }
    }

    class HmacKey(val key: ByteArray, val digest: CryptographyAlgorithmId<Digest>) : HMAC.Key, SignatureGenerator, SignatureVerifier {

        private val pool = ObjectPool { digest.hmac(key) }

        override fun signatureGenerator(): SignatureGenerator = this

        override fun signatureVerifier(): SignatureVerifier = this

        override fun encodeToByteArrayBlocking(format: HMAC.Key.Format): ByteArray {
            if (format != HMAC.Key.Format.RAW) {
                throw IllegalArgumentException("Only supported RAW encoding")
            }

            return key.copyOf()
        }

        override fun createSignFunction(): SignFunction = HmacFunction()

        override fun createVerifyFunction(): VerifyFunction = HmacFunction()

        inner class HmacFunction : SignFunction, VerifyFunction {

            private var mac: Hmac? = pool.acquire()

            override fun signIntoByteArray(destination: ByteArray, destinationOffset: Int): Int {
                val mac = mac ?: throw IllegalStateException("Already closed")

                return mac.doFinalInto(destination, destinationOffset)
            }

            override fun signToByteArray(): ByteArray {
                val mac = mac ?: throw IllegalStateException("Already closed")

                return mac.doFinal()
            }

            override fun reset() {
                val mac = mac ?: throw IllegalStateException("Already closed")

                mac.reset()
            }

            override fun update(source: ByteArray, startIndex: Int, endIndex: Int) {
                val mac = mac ?: throw IllegalStateException("Already closed")

                mac.update(source, startIndex, endIndex - startIndex)
            }

            override fun close() {
                val mac = mac ?: throw IllegalStateException("Already closed")

                pool.release(mac)
                this.mac = null
            }

            override fun tryVerify(
                signature: ByteArray,
                startIndex: Int,
                endIndex: Int
            ): Boolean {
                val generated = signToByteArray()

                var result = 0

                for (i in 0 until min(generated.size, signature.size)) {
                    result = result or (generated[i].toInt() xor signature[i].toInt())
                }

                return result == 0
            }

            override fun verify(signature: ByteArray, startIndex: Int, endIndex: Int) {
                if (!tryVerify(signature, startIndex, endIndex)) {
                    throw IllegalStateException("Invalid signature")
                }
            }
        }
    }
}

private fun CryptographyAlgorithmId<Digest>.blockSize(): Int = when (this) {
    SHA1 -> 64
    SHA224 -> 64
    SHA256 -> 64
    SHA384 -> 128
    SHA512 -> 128
    SHA3_224 -> 144
    SHA3_256 -> 136
    SHA3_384 -> 104
    SHA3_512 -> 72
    else -> throw IllegalStateException("Unsupported hash algorithm: $this")
} * 8

private fun CryptographyAlgorithmId<Digest>.hmac(key: ByteArray): Hmac = when (this) {
    SHA1 -> HmacSHA1(key)
    SHA224 -> HmacSHA224(key)
    SHA256 -> HmacSHA256(key)
    SHA384 -> HmacSHA384(key)
    SHA512 -> HmacSHA512(key)
    SHA3_224 -> HmacSHA3_224(key)
    SHA3_256 -> HmacSHA3_256(key)
    SHA3_384 -> HmacSHA3_384(key)
    SHA3_512 -> HmacSHA3_512(key)
    else -> throw IllegalStateException("Unsupported hash algorithm: $this")
}