package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.annotation.InDevelopment
import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

@InDevelopment
@OptIn(ExperimentalWasmJsInterop::class)
class BigInt(val length: Int = MAX_LENGTH) : BCSType<JsBigInt> {

    companion object {
        const val MAX_LENGTH: Int = 32
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    override fun serialize(
        value: JsBigInt,
        writer: BCSWriter
    ): BCSWriter = throw NotImplementedError()

    @OptIn(ExperimentalWasmJsInterop::class)
    override fun deserialize(reader: BCSReader): JsBigInt = throw NotImplementedError()
}