package kr.jadekim.common.extension

fun Int.dayToHour() = this * 24

fun Int.hourToMinute() = this * 60

fun Int.minuteToSecond() = this * 60

fun Int.secondToMillisecond() = this * 1000

fun Int.toBoolean() = this != 0

fun Long.dayToHour() = this * 24

fun Long.hourToMinute() = this * 60

fun Long.minuteToSecond() = this * 60

fun Long.secondToMillisecond() = this * 1000

fun <T : Comparable<T>> min(a: T, b: T) = if (a < b) a else b

fun <T : Comparable<T>> max(a: T, b: T) = if (a > b) a else b

val Short.bigEndian: ByteArray
    get() {
        val value = toInt()

        return byteArrayOf(
            ((value shr 8) and 0xFF).toByte(),
            (value and 0xFF).toByte()
        )
    }

val Short.littleEndian: ByteArray
    get() {
        val value = toInt()

        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
        )
    }

fun ByteArray.toShortWithinBigEndian(): Short {
    var result = 0

    for (i in indices) {
        result = result or ((this[i].toInt() and 0xFF) shl ((Short.SIZE_BITS - 8) - 8 * i))
    }

    return result.toShort()
}

fun ByteArray.toShortWithinLittleEndian(): Short {
    var result = 0

    for (i in indices) {
        result = result or ((this[i].toInt() and 0xFF) shl 8 * i)
    }

    return result.toShort()
}

val UShort.bigEndian: ByteArray
    get() = toShort().bigEndian

val UShort.littleEndian: ByteArray
    get() = toShort().littleEndian

fun ByteArray.toUShortWithinBigEndian(): UShort {
    return toShortWithinBigEndian().toUShort()
}

fun ByteArray.toUShortWithinLittleEndian(): UShort {
    return toShortWithinLittleEndian().toUShort()
}

val Int.bigEndian: ByteArray
    get() = byteArrayOf(
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        (this and 0xFF).toByte()
    )

val Int.littleEndian: ByteArray
    get() = byteArrayOf(
        (this and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte()
    )

fun ByteArray.toIntWithinBigEndian(): Int {
    var result = 0

    for (i in indices) {
        result = result or ((this[i].toInt() and 0xFF) shl ((Int.SIZE_BITS - 8) - 8 * i))
    }

    return result
}

fun ByteArray.toIntWithinLittleEndian(): Int {
    var result = 0

    for (i in indices) {
        result = result or ((this[i].toInt() and 0xFF) shl 8 * i)
    }

    return result
}

val UInt.bigEndian: ByteArray
    get() = toInt().bigEndian

val UInt.littleEndian: ByteArray
    get() = toInt().littleEndian

fun ByteArray.toUIntWithinBigEndian(): UInt {
    return toIntWithinBigEndian().toUInt()
}

fun ByteArray.toUIntWithinLittleEndian(): UInt {
    return toIntWithinLittleEndian().toUInt()
}

val Long.bigEndian: ByteArray
    get() = byteArrayOf(
        ((this shr 56) and 0xFF).toByte(),
        ((this shr 48) and 0xFF).toByte(),
        ((this shr 40) and 0xFF).toByte(),
        ((this shr 32) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        (this and 0xFF).toByte()
    )

val Long.littleEndian: ByteArray
    get() = byteArrayOf(
        (this and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 32) and 0xFF).toByte(),
        ((this shr 40) and 0xFF).toByte(),
        ((this shr 48) and 0xFF).toByte(),
        ((this shr 56) and 0xFF).toByte()
    )

fun ByteArray.toLongWithinBigEndian(): Long {
    var result = 0L

    for (i in indices) {
        result = result or ((this[i].toInt() and 0xFF) shl ((Long.SIZE_BITS - 8) - 8 * i)).toLong()
    }

    return result
}

fun ByteArray.toLongWithinLittleEndian(): Long {
    var result = 0L

    for (i in indices) {
        result = result or ((this[i].toInt() and 0xFF) shl 8 * i).toLong()
    }

    return result
}

val ULong.bigEndian: ByteArray
    get() = toLong().bigEndian

val ULong.littleEndian: ByteArray
    get() = toLong().littleEndian

fun ByteArray.toULongWithinBigEndian(): ULong {
    return toLongWithinBigEndian().toULong()
}

fun ByteArray.toULongWithinLittleEndian(): ULong {
    return toLongWithinLittleEndian().toULong()
}