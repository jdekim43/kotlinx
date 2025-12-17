package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

object U8 : BCSType<UByte> {

    const val LENGTH: Int = 1

    override fun serialize(
        value: UByte,
        writer: BCSWriter
    ): BCSWriter = writer.writeByte(value.toByte())

    override fun deserialize(reader: BCSReader): UByte {
        return reader.readByte().toUByte()
    }
}