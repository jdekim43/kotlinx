package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

object Bool : BCSType<Boolean> {

    const val LENGTH: Int = 1

    override fun serialize(
        value: Boolean,
        writer: BCSWriter
    ): BCSWriter = writer.writeByte(if (value) 1 else 0)

    override fun deserialize(reader: BCSReader): Boolean = reader.readByte().toInt() == 1
}