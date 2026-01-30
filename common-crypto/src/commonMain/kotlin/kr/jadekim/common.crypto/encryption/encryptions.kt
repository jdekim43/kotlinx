package kr.jadekim.common.crypto.encryption

import kr.jadekim.common.annotation.Experimental
import kr.jadekim.common.util.SecureRandom

val AES_CBC = AES_CBC_PKCS7

expect val AES_CBC_PKCS7: EncryptionAlgorithm

private const val AES_BLOCK_SIZE = 16
val AES_CBC_PKCS7_RANDOM_IV = EncryptionAlgorithm(
    encrypt = { plaintext, key, iv ->
        val initialVector = iv ?: ByteArray(AES_BLOCK_SIZE).apply(SecureRandom::nextBytes)
        val ciphertext = AES_CBC_PKCS7.encrypt(plaintext, key, initialVector)

        initialVector + ciphertext
    },
    decrypt = { ciphertext, key, iv ->
        val initialVector = iv ?: ciphertext.sliceArray(0 until AES_BLOCK_SIZE)
        val dataBytes = ciphertext.sliceArray(AES_BLOCK_SIZE until ciphertext.size)
        val plaintext = AES_CBC_PKCS7.decrypt(dataBytes, key, initialVector)

        var lastLength = plaintext.size
        for (i in plaintext.size - 1 downTo plaintext.size - AES_BLOCK_SIZE + 1) {
            if (plaintext[i] == 0.toByte()) {
                lastLength--
            } else {
                break
            }
        }

        plaintext.sliceArray(0 until lastLength)
    }
)

@Experimental
expect val RSA_ECB_OAEP_SHA_256: EncryptionAlgorithm