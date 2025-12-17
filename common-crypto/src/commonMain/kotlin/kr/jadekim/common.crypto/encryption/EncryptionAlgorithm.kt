package kr.jadekim.common.crypto.encryption

interface EncryptionAlgorithm {

    fun encrypt(plaintext: ByteArray, key: ByteArray, initialVector: ByteArray? = null): ByteArray

    fun decrypt(ciphertext: ByteArray, key: ByteArray, initialVector: ByteArray? = null): ByteArray
}

class EncryptionAlgorithmException(cause: Throwable?) : RuntimeException(cause?.message, cause)

internal inline fun EncryptionAlgorithm(
    crossinline encrypt: (ByteArray, ByteArray, ByteArray?) -> ByteArray,
    crossinline decrypt: (ByteArray, ByteArray, ByteArray?) -> ByteArray
) = object : EncryptionAlgorithm {

    override fun encrypt(plaintext: ByteArray, key: ByteArray, initialVector: ByteArray?): ByteArray = try {
        encrypt(plaintext, key, initialVector)
    } catch (e: Exception) {
        throw EncryptionAlgorithmException(e)
    }

    override fun decrypt(ciphertext: ByteArray, key: ByteArray, initialVector: ByteArray?): ByteArray = try {
        decrypt(ciphertext, key, initialVector)
    } catch (e: Exception) {
        throw EncryptionAlgorithmException(e)
    }
}

fun ByteArray.encrypt(algorithm: EncryptionAlgorithm, key: ByteArray, initialVector: ByteArray? = null) =
    algorithm.encrypt(this, key, initialVector)

fun ByteArray.decrypt(algorithm: EncryptionAlgorithm, key: ByteArray, initialVector: ByteArray? = null) =
    algorithm.decrypt(this, key, initialVector)
