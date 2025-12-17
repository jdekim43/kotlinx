package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kr.jadekim.common.extension.littleEndian
import kr.jadekim.common.extension.toLongWithinLittleEndian

object I64 : BCSType<Long> {

    const val LENGTH: Int = 8

    override fun serialize(
        value: Long,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.littleEndian)

    override fun deserialize(reader: BCSReader): Long = reader.readBytes(LENGTH).toLongWithinLittleEndian()
}