package kim.jade.encoding.bcs.type

import com.ionspin.kotlin.bignum.integer.toBigInteger
import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kotlin.String as KString

object I256 : BCSType<KString> {

    const val LENGTH: Int = 32

    private val delegator = BigInteger(LENGTH)

    override fun serialize(value: KString, writer: BCSWriter): BCSWriter =
        delegator.serialize(value.toBigInteger(), writer)

    override fun deserialize(reader: BCSReader): KString = delegator.deserialize(reader).toString(10)
}