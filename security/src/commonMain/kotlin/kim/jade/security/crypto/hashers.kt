@file:OptIn(CryptographyProviderApi::class)

package kim.jade.security.crypto

import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.CryptographyProviderApi
import dev.whyoleg.cryptography.algorithms.Digest
import kim.jade.encoding.Base64
import kim.jade.encoding.encode

class SHA512t(val t: Int) : CryptographyAlgorithmId<Digest>("SHA512/$t") {

    override fun equals(other: Any?): Boolean = this === other || other is SHA512t && other.t == t

    override fun hashCode(): Int = t.hashCode()
}

object KECCAK224 : CryptographyAlgorithmId<Digest>("KECCAK-224")
object KECCAK256 : CryptographyAlgorithmId<Digest>("KECCAK-256")
object KECCAK384 : CryptographyAlgorithmId<Digest>("KECCAK-384")
object KECCAK512 : CryptographyAlgorithmId<Digest>("KECCAK-512")

class SHAKE128(val outputLength: Int? = null) : CryptographyAlgorithmId<Digest>("SHAKE128-$outputLength") {

    override fun equals(other: Any?): Boolean = this === other || other is SHAKE128 && other.outputLength == outputLength

    override fun hashCode(): Int = outputLength.hashCode()
}

class SHAKE256(val outputLength: Int? = null) : CryptographyAlgorithmId<Digest>("SHAKE256-$outputLength") {

    override fun equals(other: Any?): Boolean = this === other || other is SHAKE128 && other.outputLength == outputLength

    override fun hashCode(): Int = outputLength.hashCode()
}

class CSHAKE128(val N: ByteArray?, val S: ByteArray?, val outputLength: Int? = null) :
    CryptographyAlgorithmId<Digest>("CSHAKE128/${N?.encode(Base64)}/${S?.encode(Base64)}-$outputLength") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSHAKE128) return false
        if (!N.contentEquals(other.N)) return false
        if (!S.contentEquals(other.S)) return false
        if (outputLength != other.outputLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = outputLength.hashCode()
        result = 31 * result + N.hashCode()
        result = 31 * result + S.hashCode()
        return result
    }
}

class CSHAKE256(val N: ByteArray?, val S: ByteArray?, val outputLength: Int? = null) :
    CryptographyAlgorithmId<Digest>("CSHAKE256/${N?.encode(Base64)}/${S?.encode(Base64)}-$outputLength") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CSHAKE256) return false
        if (!N.contentEquals(other.N)) return false
        if (!S.contentEquals(other.S)) return false
        if (outputLength != other.outputLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = outputLength.hashCode()
        result = 31 * result + N.hashCode()
        result = 31 * result + S.hashCode()
        return result
    }
}

class ParallelHash128(val S: ByteArray?, val B: Int, val outputLength: Int? = null) :
    CryptographyAlgorithmId<Digest>("ParallelHash128/${S?.encode(Base64)}/$B-$outputLength") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParallelHash128) return false
        if (!S.contentEquals(other.S)) return false
        if (B != other.B) return false
        if (outputLength != other.outputLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = outputLength.hashCode()
        result = 31 * result + S.hashCode()
        result = 31 * result + B.hashCode()
        return result
    }
}

class ParallelHash256(val S: ByteArray?, val B: Int, val outputLength: Int? = null) :
    CryptographyAlgorithmId<Digest>("ParallelHash256/${S?.encode(Base64)}/$B-$outputLength") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParallelHash256) return false
        if (!S.contentEquals(other.S)) return false
        if (B != other.B) return false
        if (outputLength != other.outputLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = outputLength.hashCode()
        result = 31 * result + S.hashCode()
        result = 31 * result + B.hashCode()
        return result
    }
}

class TupleHash128(val S: ByteArray?, val outputLength: Int? = null) :
    CryptographyAlgorithmId<Digest>("TupleHash128/${S?.encode(Base64)}-$outputLength") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TupleHash128) return false
        if (!S.contentEquals(other.S)) return false
        if (outputLength != other.outputLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = outputLength.hashCode()
        result = 31 * result + S.hashCode()
        return result
    }
}

class TupleHash256(val S: ByteArray?, val outputLength: Int? = null) :
    CryptographyAlgorithmId<Digest>("TupleHash256/${S?.encode(Base64)}-$outputLength") {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TupleHash256) return false
        if (!S.contentEquals(other.S)) return false
        if (outputLength != other.outputLength) return false
        return true
    }

    override fun hashCode(): Int {
        var result = outputLength.hashCode()
        result = 31 * result + S.hashCode()
        return result
    }
}

class BLAKE2b(val bitStrength: Int) : CryptographyAlgorithmId<Digest>("BLAKE2b-$bitStrength") {

    override fun equals(other: Any?): Boolean = other is BLAKE2b && other.bitStrength == bitStrength

    override fun hashCode(): Int = bitStrength.hashCode()
}

class BLAKE2s(val bitStrength: Int) : CryptographyAlgorithmId<Digest>("BLAKE2s-$bitStrength") {

    override fun equals(other: Any?): Boolean = other is BLAKE2s && other.bitStrength == bitStrength

    override fun hashCode(): Int = bitStrength.hashCode()
}