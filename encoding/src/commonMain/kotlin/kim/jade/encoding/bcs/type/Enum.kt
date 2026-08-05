package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kotlin.Pair as KPair
import kotlin.String as KString
import kotlin.collections.Map as KMap

class Enum(val types: KMap<KString, BCSType<Any>>) : BCSType<KPair<KString, Any>> {

    private val sortedTypes = types.toList().sortedBy { it.first }

    override fun serialize(
        value: KPair<KString, Any>,
        writer: BCSWriter
    ): BCSWriter {
        for (i in sortedTypes.indices) {
            val (name, type) = sortedTypes[i]

            if (name == value.first) {
                writer.writeULEB(i.toUInt())
                type.serialize(value.second, writer)

                return writer
            }
        }

        throw IllegalArgumentException("Invalid enum key: ${value.first}")
    }

    override fun deserialize(reader: BCSReader): KPair<KString, Any> {
        val index = reader.readULEB().toInt()
        val (name, type) = sortedTypes[index]

        return name to type.deserialize(reader)
    }
}