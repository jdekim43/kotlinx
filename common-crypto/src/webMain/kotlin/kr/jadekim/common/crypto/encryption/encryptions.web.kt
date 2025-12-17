package kr.jadekim.common.crypto.encryption

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.RSA
import dev.whyoleg.cryptography.algorithms.SHA256
import kr.jadekim.common.annotation.Experimental
import kr.jadekim.common.annotation.InDevelopment

@OptIn(DelicateCryptographyApi::class)
@Experimental
actual val AES_CBC_PKCS7 = EncryptionAlgorithm(
    encrypt = { plaintext, key, initialVector ->
        val aes = CryptographyProvider.Default.get(AES.CBC)
        val cipher = aes.keyDecoder().decodeFromByteArrayBlocking(AES.Key.Format.RAW, key).cipher()
        val initialVector = initialVector ?: if (key.size > 16) key.sliceArray(IntRange(0, 15)) else key

        cipher.encryptWithIvBlocking(initialVector, plaintext)
    },
    decrypt = { ciphertext, key, initialVector ->
        val aes = CryptographyProvider.Default.get(AES.CBC)
        val cipher = aes.keyDecoder().decodeFromByteArrayBlocking(AES.Key.Format.RAW, key).cipher()
        val initialVector = initialVector ?: if (key.size > 16) key.sliceArray(IntRange(0, 15)) else key

        cipher.decryptWithIvBlocking(initialVector, ciphertext)
    },
)

@InDevelopment
actual val RSA_ECB_OAEP_SHA_256 = EncryptionAlgorithm(
    encrypt = { plaintext, key, _ ->
        val rsa = CryptographyProvider.Default.get(RSA.OAEP)
        val encryptor = rsa.publicKeyDecoder(SHA256).decodeFromByteArrayBlocking(RSA.PublicKey.Format.PEM, key).encryptor()

        encryptor.encryptBlocking(plaintext)
    },
    decrypt = { ciphertext, key, _ ->
        val rsa = CryptographyProvider.Default.get(RSA.OAEP)
        val decryptor = rsa.privateKeyDecoder(SHA256).decodeFromByteArrayBlocking(RSA.PrivateKey.Format.PEM, key).decryptor()

        decryptor.decryptBlocking(ciphertext)
    },
)