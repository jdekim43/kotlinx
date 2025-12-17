package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import java.math.BigInteger

class BigInteger(val length: Int = MAX_LENGTH) : BCSType<BigInteger> {

    companion object {
        const val MAX_LENGTH: Int = 32
    }

    override fun serialize(
        value: BigInteger,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray().copyOf(length), true)

    override fun deserialize(reader: BCSReader): BigInteger {
        return BigInteger(reader.readBytes(length, true))
    }
}