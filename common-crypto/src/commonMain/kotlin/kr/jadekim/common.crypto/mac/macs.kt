package kr.jadekim.common.crypto.mac

import org.kotlincrypto.macs.hmac.md.HmacMD5
import org.kotlincrypto.macs.hmac.sha1.HmacSHA1
import org.kotlincrypto.macs.hmac.sha2.HmacSHA224
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import org.kotlincrypto.macs.hmac.sha2.HmacSHA384
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512
import org.kotlincrypto.macs.hmac.sha3.*

val HMAC_MD5: MacFunction = MacFunction { data, key ->
    val mac = HmacMD5(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA_1: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA1(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA_224: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA224(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA_256: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA256(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA_384: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA384(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA_512: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA512(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_KECCAK_224: MacFunction = MacFunction { data, key ->
    val mac = HmacKeccak224(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_KECCAK_256: MacFunction = MacFunction { data, key ->
    val mac = HmacKeccak256(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_KECCAK_384: MacFunction = MacFunction { data, key ->
    val mac = HmacKeccak384(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_KECCAK_512: MacFunction = MacFunction { data, key ->
    val mac = HmacKeccak512(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA3_224: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA3_224(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA3_256: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA3_256(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA3_384: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA3_384(key)
    mac.update(data)
    mac.doFinal()
}

val HMAC_SHA3_512: MacFunction = MacFunction { data, key ->
    val mac = HmacSHA3_512(key)
    mac.update(data)
    mac.doFinal()
}
