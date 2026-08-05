@file:OptIn(CryptographyProviderApi::class, DelicateCryptographyApi::class)

package kim.jade.security.crypto.provider.pure

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.RIPEMD160
import kim.jade.security.crypto.provider.pure.algorithms.ripemd160.Ripemd160

@Suppress("UNCHECKED_CAST")
class PureKotlinProvider : CryptographyProvider() {

    override val name: String = "PureKotlin"

    private val cache = mutableMapOf<CryptographyAlgorithmId<*>, CryptographyAlgorithm?>()

    override fun <A : CryptographyAlgorithm> getOrNull(identifier: CryptographyAlgorithmId<A>): A? = cache.getOrPut(identifier) {
        when (identifier) {
            is RIPEMD160 -> Ripemd160.CryptographyAdapter
            else -> null
        }
    } as? A
}