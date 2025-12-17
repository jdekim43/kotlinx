package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kr.jadekim.common.extension.littleEndian
import kr.jadekim.common.extension.toUShortWithinLittleEndian

object U16 : BCSType<UShort> {

    const val LENGTH: Int = 2

    override fun serialize(
        value: UShort,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.littleEndian)

    override fun deserialize(reader: BCSReader): UShort = reader.readBytes(LENGTH).toUShortWithinLittleEndian()
}
