package kr.jadekim.common.crypto.encryption

import kr.jadekim.common.annotation.Experimental
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec

actual val AES_CBC_PKCS7 = EncryptionAlgorithm(
    encrypt = { plaintext, key, initialVector ->
        val initialVector = initialVector ?: if (key.size > 16) key.sliceArray(IntRange(0, 15)) else key
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(initialVector))

        cipher.doFinal(plaintext)
    },
    decrypt = { ciphertext, key, initialVector ->
        val initialVector = initialVector ?: if (key.size > 16) key.sliceArray(IntRange(0, 15)) else key
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(initialVector))

        cipher.doFinal(ciphertext)
    },
)

val RSA_ECB_PKCS1 = EncryptionAlgorithm(
    encrypt = { plaintext, key, _ ->
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

        val keyFactory = KeyFactory.getInstance("RSA");

        val publicKeySpec = X509EncodedKeySpec(key);
        val publicKey = keyFactory.generatePublic(publicKeySpec);

        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val input = plaintext.inputStream()
        val output = ByteArrayOutputStream()

        val inputBuffer = ByteArray(245)

        var len: Int = input.read(inputBuffer)
        while (len != -1) {
            output.write(cipher.doFinal(inputBuffer, 0, len))

            len = input.read(inputBuffer)
        }

        output.toByteArray()
    },
    decrypt = { ciphertext, key, _ ->
        val cipher = Cipher.getInstance("RSA")

        val keyFactory = KeyFactory.getInstance("RSA");

        val privateKeySpec = PKCS8EncodedKeySpec(key)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val input = ciphertext.inputStream()
        val output = ByteArrayOutputStream()

        val inputBuffer = ByteArray(256)

        var len: Int = input.read(inputBuffer)
        while (len != -1) {
            output.write(cipher.doFinal(inputBuffer, 0, len))

            len = input.read(inputBuffer)
        }

        output.toByteArray()
    },
)

@Experimental
actual val RSA_ECB_OAEP_SHA_256 = EncryptionAlgorithm(
    encrypt = { plaintext, key, _ ->
        val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")

        val keyFactory = KeyFactory.getInstance("RSA")

        val publicKeySpec = X509EncodedKeySpec(key)
        val publicKey = keyFactory.generatePublic(publicKeySpec)

        val oaepParams = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT)

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams)
        cipher.doFinal(plaintext)
    },
    decrypt = { ciphertext, key, _ ->
        val cipher = Cipher.getInstance("RSA")

        val keyFactory = KeyFactory.getInstance("RSA")

        val privateKeySpec = PKCS8EncodedKeySpec(key)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        val oaepParams = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT)

        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams)
        cipher.doFinal(ciphertext)
    },
)
