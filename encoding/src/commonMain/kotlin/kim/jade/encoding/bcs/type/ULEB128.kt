package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

object ULEB128 : BCSType<UInt> {

    override fun serialize(
        value: UInt,
        writer: BCSWriter
    ): BCSWriter = writer.writeULEB(value)

    override fun deserialize(reader: BCSReader): UInt = reader.readULEB()
}