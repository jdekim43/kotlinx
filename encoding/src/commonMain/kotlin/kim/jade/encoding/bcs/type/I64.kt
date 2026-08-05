package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kim.jade.kotlinx.extension.ByteOrder
import kim.jade.kotlinx.extension.toByteArray
import kim.jade.kotlinx.extension.toLong

object I64 : BCSType<Long> {

    const val LENGTH: Int = 8

    override fun serialize(
        value: Long,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray(ByteOrder.LITTLE_ENDIAN))

    override fun deserialize(reader: BCSReader): Long = reader.readBytes(LENGTH).toLong(ByteOrder.LITTLE_ENDIAN)
}