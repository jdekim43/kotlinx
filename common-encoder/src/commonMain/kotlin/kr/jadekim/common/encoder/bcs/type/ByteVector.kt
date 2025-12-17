package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

object ByteVector : BCSType<ByteArray> {

    override fun serialize(
        value: ByteArray,
        writer: BCSWriter
    ): BCSWriter = writer.writeULEB(value.size.toUInt())
        .writeBytes(value)

    override fun deserialize(reader: BCSReader): ByteArray {
        val length = reader.readULEB().toInt()

        return reader.readBytes(length)
    }
}