package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kim.jade.kotlinx.extension.ByteOrder
import kim.jade.kotlinx.extension.toByteArray
import kim.jade.kotlinx.extension.toULong

object U64 : BCSType<ULong> {

    const val LENGTH: Int = 8

    override fun serialize(
        value: ULong,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray(ByteOrder.LITTLE_ENDIAN))

    override fun deserialize(reader: BCSReader): ULong = reader.readBytes(LENGTH).toULong(ByteOrder.LITTLE_ENDIAN)
}