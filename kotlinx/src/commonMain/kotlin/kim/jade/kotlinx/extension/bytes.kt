package kim.jade.kotlinx.extension

enum class ByteOrder {
    BIG_ENDIAN,
    LITTLE_ENDIAN,
}

fun ByteArray.padStart(size: Int, byte: Byte = 0): ByteArray {
    val result = ByteArray(size)

    for (i in 0 until (size - this.size)) {
        result[i] = byte
    }

    copyInto(result, size - this.size)

    return result
}

fun ByteArray.padEnd(size: Int, byte: Byte = 0): ByteArray {
    val result = ByteArray(size)

    for (i in this.size until size) {
        result[i] = byte
    }

    copyInto(result, 0)

    return result
}

fun ByteArray.write(offset: Int, value: Byte) {
    set(offset, value)
}

fun ByteArray.write(offset: Int, value: UByte) {
    set(offset, value.toByte())
}

fun ByteArray.write(offset: Int, value: ByteArray) {
    value.copyInto(this, offset)
}

fun ByteArray.write(offset: Int, value: Short, littleEndian: Boolean = false) {
    write(offset, value.toByteArray(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN))
}

fun ByteArray.write(offset: Int, value: UShort, littleEndian: Boolean = false) {
    write(offset, value.toByteArray(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN))
}

fun ByteArray.write(offset: Int, value: Int, littleEndian: Boolean = false) {
    write(offset, value.toByteArray(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN))
}

fun ByteArray.write(offset: Int, value: UInt, littleEndian: Boolean = false) {
    write(offset, value.toByteArray(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN))
}

fun ByteArray.write(offset: Int, value: Long, littleEndian: Boolean = false) {
    write(offset, value.toByteArray(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN))
}

fun ByteArray.write(offset: Int, value: ULong, littleEndian: Boolean = false) {
    write(offset, value.toByteArray(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN))
}

fun Byte.toBinary(): BooleanArray = toInt().let { byte ->
    BooleanArray(Byte.SIZE_BITS) { i ->
        val isOne = ((byte shr (Byte.SIZE_BITS - 1 - i)) and 1) == 1

        return@BooleanArray isOne
    }
}

fun Int.toBinary(): BooleanArray = BooleanArray(Int.SIZE_BITS) { i ->
    val isOne = ((this@toBinary shr (Int.SIZE_BITS - 1 - i)) and 1) == 1

    return@BooleanArray isOne
}

fun ByteArray.toBinary(): BooleanArray = foldIndexed(BooleanArray(size * Byte.SIZE_BITS)) { index, acc, byte ->
    byte.toBinary()
        .copyInto(acc, index * Byte.SIZE_BITS, 0, Byte.SIZE_BITS)
}

fun BooleanArray.toByte(): Byte = toInt().toByte()

fun BooleanArray.toInt(): Int = fold(0) { acc, isOne -> (acc shl 1) or (if (isOne) 1 else 0) }

fun BooleanArray.toByteArray(): ByteArray {
    require(size % Byte.SIZE_BITS == 0) { "Invalid size" }

    val result = ByteArray(size / Byte.SIZE_BITS)

    for (i in indices step Byte.SIZE_BITS) {
        result[i] = sliceArray(i until i + Byte.SIZE_BITS).toByte()
    }

    return result
}
