package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kim.jade.kotlinx.extension.ByteOrder
import kim.jade.kotlinx.extension.toByteArray
import kim.jade.kotlinx.extension.toUInt

object U32 : BCSType<UInt> {

    const val LENGTH: Int = 4

    override fun serialize(
        value: UInt,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray(ByteOrder.LITTLE_ENDIAN))

    override fun deserialize(reader: BCSReader): UInt = reader.readBytes(LENGTH).toUInt(ByteOrder.LITTLE_ENDIAN)
}
