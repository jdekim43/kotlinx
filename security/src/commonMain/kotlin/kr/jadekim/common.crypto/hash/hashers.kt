package kr.jadekim.common.crypto.hash

import kim.jade.security.crypto.provider.pure.algorithms.ripemd160.Ripemd160
import org.kotlincrypto.hash.md.MD5
import org.kotlincrypto.hash.sha1.SHA1
import org.kotlincrypto.hash.sha2.SHA224
import org.kotlincrypto.hash.sha2.SHA256
import org.kotlincrypto.hash.sha2.SHA384
import org.kotlincrypto.hash.sha2.SHA512
import org.kotlincrypto.hash.sha3.*

val MD5: HashFunction = HashFunction {
    val digest = MD5()
    digest.update(it)
    digest.digest()
}

val SHA_1: HashFunction = HashFunction {
    val digest = SHA1()
    digest.update(it)
    digest.digest()
}

val SHA_224: HashFunction = HashFunction {
    val digest = SHA224()
    digest.update(it)
    digest.digest()
}

val SHA_256: HashFunction = HashFunction {
    val digest = SHA256()
    digest.update(it)
    digest.digest()
}

val SHA_384: HashFunction = HashFunction {
    val digest = SHA384()
    digest.update(it)
    digest.digest()
}

val SHA_512: HashFunction = HashFunction {
    val digest = SHA512()
    digest.update(it)
    digest.digest()
}

val KECCAK_224: HashFunction = HashFunction {
    val digest = Keccak224()
    digest.update(it)
    digest.digest()
}

val KECCAK_256: HashFunction = HashFunction {
    val digest = Keccak256()
    digest.update(it)
    digest.digest()
}

val KECCAK_384: HashFunction = HashFunction {
    val digest = Keccak384()
    digest.update(it)
    digest.digest()
}

val KECCAK_512: HashFunction = HashFunction {
    val digest = Keccak512()
    digest.update(it)
    digest.digest()
}

val SHA3_224: HashFunction = HashFunction {
    val digest = SHA3_224()
    digest.update(it)
    digest.digest()
}

val SHA3_256: HashFunction = HashFunction {
    val digest = SHA3_256()
    digest.update(it)
    digest.digest()
}

val SHA3_384: HashFunction = HashFunction {
    val digest = SHA3_384()
    digest.update(it)
    digest.digest()
}

val SHA3_512: HashFunction = HashFunction {
    val digest = SHA3_512()
    digest.update(it)
    digest.digest()
}

val RIPEMD160: HashFunction = HashFunction {
    val digest = Ripemd160()
    digest.update(it, 0, it.size)

    ByteArray(Ripemd160.DIGEST_LENGTH).apply {
        digest.doFinal(this, 0)
    }
}
