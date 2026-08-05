package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

class Bytes(val size: Int) : BCSType<ByteArray> {

    override fun serialize(
        value: ByteArray,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value)

    override fun deserialize(reader: BCSReader): ByteArray = reader.readBytes(size)
}