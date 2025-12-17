package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

object I8 : BCSType<Byte> {

    const val LENGTH: Int = 1

    override fun serialize(
        value: Byte,
        writer: BCSWriter
    ): BCSWriter = writer.writeByte(value)

    override fun deserialize(reader: BCSReader): Byte {
        return reader.readByte()
    }
}