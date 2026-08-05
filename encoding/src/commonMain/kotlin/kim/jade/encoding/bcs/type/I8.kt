package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

object I8 : BCSType<Byte> {

    const val LENGTH: Int = 1

    override fun serialize(
        value: Byte,
        writer: BCSWriter
    ): BCSWriter = writer.writeByte(value)

    override fun deserialize(reader: BCSReader): Byte {
        return reader.readByte()
    }
}