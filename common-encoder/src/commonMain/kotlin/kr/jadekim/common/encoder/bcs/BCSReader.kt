package kr.jadekim.common.encoder.bcs

import kr.jadekim.common.encoder.ULEB

class BCSReader(bytes: ByteArray) {
    val bytes = bytes.copyOf()
    val length: Int = bytes.size

    var position: Int = 0
        private set

    fun readByte(): Byte = bytes[position].also { shift(1) }

    fun readBytes(size: Int, littleEndian: Boolean = false): ByteArray {
        var result = bytes.copyOfRange(position, position + size)

        if (littleEndian) {
            result = result.reversedArray()
        }

        shift(size)

        return result
    }

    fun readULEB(): UInt {
        val (value, length) = ULEB.decode(bytes, position)
        shift(length)

        return value
    }

    fun <T> readVector(read: BCSReader.(i: Int, length: Int) -> T): List<T> {
        val length = readULEB().toInt()

        return MutableList(length) { i ->
            read(i, length)
        }
    }

    fun isAtEnd(): Boolean = position >= length

    private fun shift(size: Int) {
        position += size
    }
}