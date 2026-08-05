package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kim.jade.kotlinx.extension.ByteOrder
import kim.jade.kotlinx.extension.toByteArray
import kim.jade.kotlinx.extension.toInt

object I32 : BCSType<Int> {

    const val LENGTH: Int = 4

    override fun serialize(
        value: Int,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray(ByteOrder.LITTLE_ENDIAN))

    override fun deserialize(reader: BCSReader): Int = reader.readBytes(LENGTH).toInt(ByteOrder.LITTLE_ENDIAN)
}
