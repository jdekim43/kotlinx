package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kotlin.collections.Map as KMap

class Map<K, V>(val keyType: BCSType<K>, val valueType: BCSType<V>) : BCSType<KMap<K, V>> {

    private val vector = Vector(Pair(keyType, valueType))

    override fun serialize(
        value: KMap<K, V>,
        writer: BCSWriter
    ): BCSWriter = vector.serialize(value.map { it.key to it.value }, writer)

    override fun deserialize(reader: BCSReader): KMap<K, V> = vector.deserialize(reader).toMap()
}