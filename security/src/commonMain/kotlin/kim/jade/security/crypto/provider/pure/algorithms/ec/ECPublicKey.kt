@file:OptIn(CryptographyProviderApi::class)

package kim.jade.security.crypto.provider.pure.algorithms.ec

import dev.whyoleg.cryptography.CryptographyProviderApi
import dev.whyoleg.cryptography.algorithms.EC

open class ECPublicKey(private val bytes: ByteArray) : EC.PublicKey {
    override fun encodeToByteArrayBlocking(format: EC.PublicKey.Format): ByteArray {
        TODO("Not yet implemented")
    }
}