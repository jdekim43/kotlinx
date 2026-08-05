package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

class Vector<T>(val type: BCSType<T>) : BCSType<List<T>> {

    override fun serialize(
        value: List<T>,
        writer: BCSWriter
    ): BCSWriter {
        writer.writeULEB(value.size.toUInt())

        value.forEach {
            type.serialize(it, writer)
        }

        return writer
    }

    override fun deserialize(reader: BCSReader): List<T> = List(reader.readULEB().toInt()) {
        type.deserialize(reader)
    }
}