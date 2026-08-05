package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

class Option<T>(val type: BCSType<T>) : BCSType<T?> {

    override fun serialize(
        value: T?,
        writer: BCSWriter
    ): BCSWriter {
        if (value == null) {
            writer.writeByte(0)
        } else {
            writer.writeByte(1)
            type.serialize(value, writer)
        }

        return writer
    }

    override fun deserialize(reader: BCSReader): T? {
        val isNull = reader.readByte() == 0.toByte()

        if (isNull) {
            return null
        }

        return type.deserialize(reader)
    }
}