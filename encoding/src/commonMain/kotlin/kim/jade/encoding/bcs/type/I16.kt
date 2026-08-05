package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kim.jade.kotlinx.extension.ByteOrder
import kim.jade.kotlinx.extension.toByteArray
import kim.jade.kotlinx.extension.toShort

object I16 : BCSType<Short> {

    const val LENGTH: Int = 2

    override fun serialize(
        value: Short,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray(ByteOrder.LITTLE_ENDIAN))

    override fun deserialize(reader: BCSReader): Short = reader.readBytes(LENGTH).toShort(ByteOrder.LITTLE_ENDIAN)
}
