package kr.jadekim.common.encoder

object ULEB {

    fun encode(value: UInt): ByteArray {
        val bytes = mutableListOf<Byte>()

        var v = value.toInt()
        while (v > 0x7F) {
            bytes.add((v and 0x7F or 0x80).toByte())
            v = v shr 7
        }
        bytes.add(v.toByte())

        return bytes.toByteArray()
    }

    fun decode(bytes: ByteArray, start: Int = 0): Pair<UInt, Int> {
        var result = 0
        var shift = 0
        var length = 0
        var position = start

        var byte: Int
        do {
            byte = bytes[position++].toInt()
            result = result or ((byte and 0x7F) shl shift)
            shift += 7
            length++
        } while (byte and 0x80 != 0)

        return result.toUInt() to length
    }
}