package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kr.jadekim.common.extension.littleEndian
import kr.jadekim.common.extension.toIntWithinLittleEndian

object I32 : BCSType<Int> {

    const val LENGTH: Int = 4

    override fun serialize(
        value: Int,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.littleEndian)

    override fun deserialize(reader: BCSReader): Int = reader.readBytes(LENGTH).toIntWithinLittleEndian()
}
