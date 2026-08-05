package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

object U8 : BCSType<UByte> {

    const val LENGTH: Int = 1

    override fun serialize(
        value: UByte,
        writer: BCSWriter
    ): BCSWriter = writer.writeByte(value.toByte())

    override fun deserialize(reader: BCSReader): UByte {
        return reader.readByte().toUByte()
    }
}