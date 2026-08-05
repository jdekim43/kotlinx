package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter

interface BCSType<KotlinType> {

    fun serialize(value: KotlinType, writer: BCSWriter = BCSWriter()): BCSWriter

    fun deserialize(bytes: ByteArray): KotlinType = deserialize(BCSReader(bytes))

    fun deserialize(reader: BCSReader): KotlinType
}
