package kr.jadekim.common.crypto.mac

import kr.jadekim.common.encoder.Encoder
import kr.jadekim.common.encoder.decode
import kr.jadekim.common.encoder.encode
import kr.jadekim.common.extension.min
import kr.jadekim.common.extension.utf8

interface MacFunction {

    fun hash(data: ByteArray, key: ByteArray): ByteArray

    fun hash(data: String, key: ByteArray) = hash(data.utf8(), key)

    fun verify(data: ByteArray, key: ByteArray, hash: ByteArray): Boolean {
        val generated = hash(data, key)

        var result = 0

        for (i in 0 until min(generated.size, hash.size)) {
            result = result or (generated[i].toInt() xor hash[i].toInt())
        }

        return result == 0
    }

    fun verify(data: String, key: ByteArray, hash: ByteArray): Boolean = verify(data.utf8(), key, hash)
}

fun <EncodedType> MacFunction.hash(
    data: ByteArray,
    key: ByteArray,
    encoder: Encoder<ByteArray, EncodedType>,
): EncodedType = hash(data, key).encode(encoder)

fun <EncodedType> MacFunction.hash(
    data: String,
    key: ByteArray,
    encoder: Encoder<ByteArray, EncodedType>,
): EncodedType = hash(data, key).encode(encoder)

fun <EncodedType> MacFunction.verify(
    data: ByteArray,
    key: ByteArray,
    hash: EncodedType,
    encoder: Encoder<ByteArray, EncodedType>,
): Boolean = verify(data, key, hash.decode(encoder))

fun <EncodedType> MacFunction.verify(
    data: String,
    key: ByteArray,
    hash: EncodedType,
    encoder: Encoder<ByteArray, EncodedType>,
): Boolean = verify(data, key, hash.decode(encoder))

class MacException(cause: Throwable?) : RuntimeException(cause?.message, cause)

internal inline fun MacFunction(crossinline body: (ByteArray, ByteArray) -> ByteArray) = object : MacFunction {

    override fun hash(data: ByteArray, key: ByteArray): ByteArray = try {
        body(data, key)
    } catch (e: Exception) {
        throw MacException(e)
    }
}

fun ByteArray.hash(function: MacFunction, key: ByteArray) = function.hash(this, key)

fun String.hash(function: MacFunction, key: ByteArray) = function.hash(this, key)

fun ByteArray.verify(function: MacFunction, key: ByteArray, hash: ByteArray) = function.verify(this, key, hash)

fun String.verify(function: MacFunction, key: ByteArray, hash: ByteArray) = function.verify(this, key, hash)

fun <EncodedType> ByteArray.hash(function: MacFunction, key: ByteArray, encoder: Encoder<ByteArray, EncodedType>) =
    function.hash(this, key, encoder)

fun <EncodedType> String.hash(function: MacFunction, key: ByteArray, encoder: Encoder<ByteArray, EncodedType>) =
    function.hash(this, key, encoder)

fun <EncodedType> ByteArray.verify(
    function: MacFunction,
    key: ByteArray,
    hash: EncodedType,
    encoder: Encoder<ByteArray, EncodedType>,
) = function.verify(this, key, hash, encoder)

fun <EncodedType> String.verify(
    function: MacFunction,
    key: ByteArray,
    hash: EncodedType,
    encoder: Encoder<ByteArray, EncodedType>,
) = function.verify(this, key, hash, encoder)
