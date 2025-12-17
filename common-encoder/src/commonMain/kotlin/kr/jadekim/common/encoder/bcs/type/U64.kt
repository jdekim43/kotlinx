package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kr.jadekim.common.extension.littleEndian
import kr.jadekim.common.extension.toULongWithinLittleEndian

object U64 : BCSType<ULong> {

    const val LENGTH: Int = 8

    override fun serialize(
        value: ULong,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.littleEndian)

    override fun deserialize(reader: BCSReader): ULong = reader.readBytes(LENGTH).toULongWithinLittleEndian()
}