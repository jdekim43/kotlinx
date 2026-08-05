package kim.jade.kotlinx.extension

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

fun Short.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
        val value = toInt()

        byteArrayOf(
            ((value shr 8) and 0xFF).toByte(),
            (value and 0xFF).toByte()
        )
    }

    ByteOrder.LITTLE_ENDIAN -> {
        val value = toInt()

        byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
        )
    }
}

fun ByteArray.toShort(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Short = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
        var result = 0

        for (i in indices) {
            result = result or ((this[i].toInt() and 0xFF) shl ((Short.SIZE_BITS - 8) - 8 * i))
        }

        result.toShort()
    }

    ByteOrder.LITTLE_ENDIAN -> {
        var result = 0

        for (i in indices) {
            result = result or ((this[i].toInt() and 0xFF) shl 8 * i)
        }

        result.toShort()
    }
}

fun UShort.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray = toShort().toByteArray(byteOrder)

fun ByteArray.toUShort(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): UShort = toShort(byteOrder).toUShort()

fun Int.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteArrayOf(
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        (this and 0xFF).toByte()
    )

    ByteOrder.LITTLE_ENDIAN -> byteArrayOf(
        (this and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte()
    )
}

fun ByteArray.toInt(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Int = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
        var result = 0

        for (i in indices) {
            result = result or ((this[i].toInt() and 0xFF) shl ((Int.SIZE_BITS - 8) - 8 * i))
        }

        result
    }

    ByteOrder.LITTLE_ENDIAN -> {
        var result = 0

        for (i in indices) {
            result = result or ((this[i].toInt() and 0xFF) shl 8 * i)
        }

        result
    }
}

fun UInt.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray = toInt().toByteArray(byteOrder)

fun ByteArray.toUInt(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): UInt = toInt(byteOrder).toUInt()

fun Long.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> byteArrayOf(
        ((this shr 56) and 0xFF).toByte(),
        ((this shr 48) and 0xFF).toByte(),
        ((this shr 40) and 0xFF).toByte(),
        ((this shr 32) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        (this and 0xFF).toByte()
    )

    ByteOrder.LITTLE_ENDIAN -> byteArrayOf(
        (this and 0xFF).toByte(),
        ((this shr 8) and 0xFF).toByte(),
        ((this shr 16) and 0xFF).toByte(),
        ((this shr 24) and 0xFF).toByte(),
        ((this shr 32) and 0xFF).toByte(),
        ((this shr 40) and 0xFF).toByte(),
        ((this shr 48) and 0xFF).toByte(),
        ((this shr 56) and 0xFF).toByte()
    )
}

fun ByteArray.toLong(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): Long = when (byteOrder) {
    ByteOrder.BIG_ENDIAN -> {
        var result = 0L

        for (i in indices) {
            val value = this[i].toLong() and 0xFFL
            val shift = (size - 1 - i) * Byte.SIZE_BITS
            result = result or (value shl shift)
        }

        result
    }

    ByteOrder.LITTLE_ENDIAN -> {
        var result = 0L

        for (i in indices) {
            val value = this[i].toLong() and 0xFFL
            val shift = i * Byte.SIZE_BITS
            result = result or (value shl shift)
        }

        result
    }
}

fun ULong.toByteArray(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray = toLong().toByteArray(byteOrder)

fun ByteArray.toULong(byteOrder: ByteOrder = ByteOrder.BIG_ENDIAN): ULong = toLong(byteOrder).toULong()

fun Int.toString(characters: CharArray, radix: Int = characters.size) = buildString {
    if (radix > characters.size) {
        throw IllegalArgumentException("Too large radix (support max ${characters.size})")
    }

    var value = this@toString

    while (value != 0) {
        append(characters[value % radix])
        value /= radix
    }

    reverse()
}

fun UInt.toString(characters: CharArray, radix: Int = characters.size) = buildString {
    if (radix > characters.size) {
        throw IllegalArgumentException("Too large radix (support max ${characters.size})")
    }

    var value = this@toString
    val radix = radix.toUInt()

    while (value != 0u) {
        append(characters[(value % radix).toInt()])
        value /= radix
    }

    reverse()
}

fun Long.toString(characters: CharArray, radix: Int = characters.size) = buildString {
    if (radix > characters.size) {
        throw IllegalArgumentException("Too large radix (support max ${characters.size})")
    }

    var value = this@toString

    while (value != 0L) {
        append(characters[(value % radix).toInt()])
        value /= radix
    }

    reverse()
}

fun ULong.toString(characters: CharArray, radix: Int = characters.size) = buildString {
    if (radix > characters.size) {
        throw IllegalArgumentException("Too large radix (support max ${characters.size})")
    }

    var value = this@toString
    val radix = radix.toULong()

    while (value != 0uL) {
        append(characters[(value % radix).toInt()])
        value /= radix
    }

    reverse()
}