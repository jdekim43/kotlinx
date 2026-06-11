package kr.jadekim.common.crypto.hash

import kr.jadekim.common.encoder.Encoder
import kr.jadekim.common.encoder.encode
import kr.jadekim.common.extension.utf8

interface HashFunction {

    fun hash(data: ByteArray): ByteArray

    fun hash(data: String) = hash(data.utf8())
}

fun <EncodedType> HashFunction.hash(data: ByteArray, encoder: Encoder<ByteArray, EncodedType>) = hash(data).encode(encoder)

fun <EncodedType> HashFunction.hash(data: String, encoder: Encoder<ByteArray, EncodedType>) = hash(data).encode(encoder)

class HashException(cause: Throwable?) : RuntimeException(cause?.message, cause)

inline fun HashFunction(crossinline body: (ByteArray) -> ByteArray) = object : HashFunction {

    override fun hash(data: ByteArray): ByteArray = try {
        body(data)
    } catch (e: Exception) {
        throw HashException(e)
    }
}

fun ByteArray.hash(function: HashFunction) = function.hash(this)

fun String.hash(function: HashFunction) = function.hash(this)

fun <EncodedType> ByteArray.hash(function: HashFunction, encoder: Encoder<ByteArray, EncodedType>) =
    function.hash(this, encoder)

fun <EncodedType> String.hash(function: HashFunction, encoder: Encoder<ByteArray, EncodedType>) =
    function.hash(this, encoder)
