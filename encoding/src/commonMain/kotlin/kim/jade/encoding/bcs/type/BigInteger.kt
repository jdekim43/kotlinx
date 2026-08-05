package kim.jade.encoding.bcs.type

import com.ionspin.kotlin.bignum.integer.Sign
import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import com.ionspin.kotlin.bignum.integer.BigInteger as KBigInteger

class BigInteger(val length: Int = MAX_LENGTH) : BCSType<KBigInteger> {

    companion object {
        const val MAX_LENGTH: Int = 32
    }

    override fun serialize(
        value: KBigInteger,
        writer: BCSWriter
    ): BCSWriter = writer.writeBytes(value.toByteArray().copyOf(length), true)

    override fun deserialize(reader: BCSReader): KBigInteger {
        return KBigInteger.fromByteArray(reader.readBytes(length, true), Sign.POSITIVE)
    }
}