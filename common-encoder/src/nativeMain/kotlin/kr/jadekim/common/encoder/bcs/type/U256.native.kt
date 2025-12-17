package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.annotation.InDevelopment
import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kotlin.String as KString

@InDevelopment
actual object U256 : BCSType<KString> {

    actual const val LENGTH: Int = 32

    actual override fun serialize(
        value: KString,
        writer: BCSWriter
    ): BCSWriter = throw NotImplementedError()

    actual override fun deserialize(reader: BCSReader): KString = throw NotImplementedError()
}