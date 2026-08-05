package kim.jade.kotlinx.extension

import js.buffer.ArrayBuffer
import js.typedarrays.Int8Array
import js.typedarrays.Uint8Array

typealias Buffer = Uint8Array<ArrayBuffer>

fun ByteArray?.toInt8Array(): Int8Array<ArrayBuffer> = unsafeCast<Int8Array<ArrayBuffer>>()

fun ByteArray?.toUint8Array(): Uint8Array<ArrayBuffer> =
    toInt8Array().let { Uint8Array(it.buffer, it.byteOffset, it.length) }

@ExperimentalUnsignedTypes
fun UByteArray?.toInt8Array(): Int8Array<ArrayBuffer> =
    unsafeCast<Uint8Array<ArrayBuffer>>().let { Int8Array(it.buffer, it.byteOffset, it.length) }

@ExperimentalUnsignedTypes
fun UByteArray?.toUint8Array(): Uint8Array<ArrayBuffer> = unsafeCast<Uint8Array<ArrayBuffer>>()
