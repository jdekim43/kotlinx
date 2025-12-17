package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

interface BCSType<KotlinType> {

    fun serialize(value: KotlinType, writer: BCSWriter = BCSWriter()): BCSWriter

    fun deserialize(bytes: ByteArray): KotlinType = deserialize(BCSReader(bytes))

    fun deserialize(reader: BCSReader): KotlinType
}
