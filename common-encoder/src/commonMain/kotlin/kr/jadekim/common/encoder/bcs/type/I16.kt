package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kr.jadekim.common.extension.littleEndian
import kr.jadekim.common.extension.toShortWithinLittleEndian

object I16 : BCSType<Short> {

    const val LENGTH: Int = 2

    override fun serialize(
        value: Short,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.littleEndian)

    override fun deserialize(reader: BCSReader): Short = reader.readBytes(LENGTH).toShortWithinLittleEndian()
}
