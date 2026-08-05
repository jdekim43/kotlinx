package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kim.jade.kotlinx.extension.ByteOrder
import kim.jade.kotlinx.extension.toByteArray
import kim.jade.kotlinx.extension.toUShort

object U16 : BCSType<UShort> {

    const val LENGTH: Int = 2

    override fun serialize(
        value: UShort,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray(ByteOrder.LITTLE_ENDIAN))

    override fun deserialize(reader: BCSReader): UShort = reader.readBytes(LENGTH).toUShort(ByteOrder.LITTLE_ENDIAN)
}
