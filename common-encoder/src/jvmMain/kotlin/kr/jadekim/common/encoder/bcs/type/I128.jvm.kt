package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kotlin.String as KString

actual object I128 : BCSType<KString> {

    private val delegator = BigInteger(LENGTH)

    actual const val LENGTH: Int = 16

    actual override fun serialize(
        value: KString,
        writer: BCSWriter
    ): BCSWriter = delegator.serialize(value.toBigInteger(), writer)

    actual override fun deserialize(reader: BCSReader): KString = delegator.deserialize(reader).toString(10)
}