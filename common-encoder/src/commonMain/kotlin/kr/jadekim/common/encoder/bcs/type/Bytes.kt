package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

class Bytes(val size: Int) : BCSType<ByteArray> {

    override fun serialize(
        value: ByteArray,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value)

    override fun deserialize(reader: BCSReader): ByteArray = reader.readBytes(size)
}