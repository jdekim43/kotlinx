package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import java.math.BigInteger

class JvmBigInteger(val length: Int = MAX_LENGTH) : BCSType<BigInteger> {

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