@file:OptIn(CryptographyProviderApi::class)

package kim.jade.security.crypto.provider.kotlincrypto

import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.CryptographyProviderApi
import dev.whyoleg.cryptography.algorithms.Digest
import dev.whyoleg.cryptography.operations.HashFunction
import dev.whyoleg.cryptography.operations.Hasher
import kim.jade.kotlinx.pool.ObjectPool
import org.kotlincrypto.core.digest.Digest as KotlinCryptoDigest

internal class KotlinCryptoDigest(override val id: CryptographyAlgorithmId<Digest>, instantiate: () -> KotlinCryptoDigest) :
    Digest, Hasher {

    private val pool = ObjectPool<KotlinCryptoDigest>(instantiate)

    override fun hasher(): Hasher = this

    override fun createHashFunction(): HashFunction = KotlinCryptoHashFunction()

    inner class KotlinCryptoHashFunction : HashFunction {

        private var digest: KotlinCryptoDigest? = pool.acquire()

        override fun hashIntoByteArray(destination: ByteArray, destinationOffset: Int): Int {
            val digest = digest ?: throw IllegalStateException("Already closed")

            return digest.digestInto(destination, destinationOffset)
        }

        override fun hashToByteArray(): ByteArray {
            val digest = digest ?: throw IllegalStateException("Already closed")

            return digest.digest()
        }

        override fun reset() {
            val digest = digest ?: throw IllegalStateException("Already closed")

            digest.reset()
        }

        override fun update(source: ByteArray, startIndex: Int, endIndex: Int) {
            val digest = digest ?: throw IllegalStateException("Already closed")

            digest.update(source, startIndex, endIndex - startIndex)
        }

        override fun close() {
            val digest = digest ?: throw IllegalStateException("Already closed")

            pool.release(digest)
            this.digest = null
        }
    }
}
