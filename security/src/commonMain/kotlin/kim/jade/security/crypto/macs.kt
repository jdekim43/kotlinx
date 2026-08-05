@file:OptIn(CryptographyProviderApi::class)

package kim.jade.security.crypto

import dev.whyoleg.cryptography.CryptographyProviderApi

//class BLAKE2bMAC(val bitStrength: Int, val personalization: ByteArray? = null) :
//    CryptographyAlgorithmId<HMAC>("BLAKE2b/MAC-$bitStrength-${personalization?.encode(Base64)}") {
//
//    override fun equals(other: Any?): Boolean =
//        other is BLAKE2bMac && other.bitStrength == bitStrength && other.personalization.contentEquals(personalization)
//
//    override fun hashCode(): Int {
//        var result = bitStrength.hashCode()
//        result = 31 * result + personalization.hashCode()
//        return result
//    }
//}
//
//class BLAKE2sMAC(val bitStrength: Int, val personalization: ByteArray? = null) :
//    CryptographyAlgorithmId<HMAC>("BLAKE2s/MAC-$bitStrength-${personalization?.encode(Base64)}") {
//
//    override fun equals(other: Any?): Boolean =
//        other is BLAKE2sMac && other.bitStrength == bitStrength && other.personalization.contentEquals(personalization)
//
//    override fun hashCode(): Int {
//        var result = bitStrength.hashCode()
//        result = 31 * result + personalization.hashCode()
//        return result
//    }
//}

//class KMAC