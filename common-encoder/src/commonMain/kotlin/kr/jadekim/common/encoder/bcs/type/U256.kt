package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter
import kotlin.String as KString

expect object U256 : BCSType<KString> {

    val LENGTH: Int

    override fun serialize(value: KString, writer: BCSWriter): BCSWriter

    override fun deserialize(reader: BCSReader): KString
}