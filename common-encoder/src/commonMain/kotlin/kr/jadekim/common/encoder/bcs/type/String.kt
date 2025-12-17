package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kotlin.String as KString

object String : BCSType<KString> {

    override fun serialize(
        value: KString,
        writer: BCSWriter
    ): BCSWriter = ByteVector.serialize(value.encodeToByteArray())

    override fun deserialize(reader: BCSReader): KString = ByteVector.deserialize(reader).decodeToString()
}