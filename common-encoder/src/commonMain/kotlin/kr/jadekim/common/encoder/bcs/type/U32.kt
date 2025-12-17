package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kr.jadekim.common.extension.littleEndian
import kr.jadekim.common.extension.toUIntWithinLittleEndian

object U32 : BCSType<UInt> {

    const val LENGTH: Int = 4

    override fun serialize(
        value: UInt,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.littleEndian)

    override fun deserialize(reader: BCSReader): UInt = reader.readBytes(LENGTH).toUIntWithinLittleEndian()
}
