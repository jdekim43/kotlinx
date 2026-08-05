package kim.jade.encoding.bcs

import kim.jade.encoding.ULEB

class BCSWriter {
    private var bytes = mutableListOf<Byte>()

    val length: Int
        get() = bytes.size

    fun writeByte(byte: Byte): BCSWriter {
        bytes.add(byte)

        return this
    }

    fun writeBytes(bytes: ByteArray, littleEndian: Boolean = false): BCSWriter {
        this.bytes.addAll(if (littleEndian) bytes.reversed() else bytes.toList())

        return this
    }

    fun writeBytes(bytes: Collection<Byte>, littleEndian: Boolean = false): BCSWriter {
        this.bytes.addAll(if (littleEndian) bytes.reversed() else bytes)

        return this
    }

    fun writeULEB(value: UInt): BCSWriter {
        writeBytes(ULEB.encode(value))

        return this
    }

    fun <T> writeVector(vector: List<T>, write: BCSWriter.(value: T, i: Int, length: Int) -> Unit): BCSWriter {
        val vectorSize = vector.size
        writeULEB(vectorSize.toUInt())
        vector.forEachIndexed { i, value ->
            write(value, i, vectorSize)
        }

        return this
    }

    fun toByteArray(): ByteArray = bytes.toByteArray()
}