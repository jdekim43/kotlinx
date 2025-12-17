package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kotlin.String as KString
import kotlin.collections.Map as KMap

class Struct(val types: KMap<KString, BCSType<Any>>) : BCSType<KMap<KString, Any>> {

    private val sortedTypes = types.entries.sortedWith { a, b -> a.key.compareTo(b.key) }

    override fun serialize(
        value: KMap<KString, Any>,
        writer: BCSWriter
    ): BCSWriter {
        sortedTypes.forEach { (name, type) ->
            type.serialize(value[name]!!, writer)
        }

        return writer
    }

    override fun deserialize(reader: BCSReader): KMap<KString, Any> = sortedTypes.associate { (name, type) ->
        name to type.deserialize(reader)
    }
}