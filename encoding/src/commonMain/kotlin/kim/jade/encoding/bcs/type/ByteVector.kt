package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

object ByteVector : BCSType<ByteArray> {

    override fun serialize(
        value: ByteArray,
        writer: BCSWriter
    ): BCSWriter = writer.writeULEB(value.size.toUInt())
        .writeBytes(value)

    override fun deserialize(reader: BCSReader): ByteArray {
        val length = reader.readULEB().toInt()

        return reader.readBytes(length)
    }
}