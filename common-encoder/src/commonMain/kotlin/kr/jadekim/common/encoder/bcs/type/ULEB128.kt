package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

object ULEB128 : BCSType<UInt> {

    override fun serialize(
        value: UInt,
        writer: BCSWriter
    ): BCSWriter = writer.writeULEB(value)

    override fun deserialize(reader: BCSReader): UInt = reader.readULEB()
}