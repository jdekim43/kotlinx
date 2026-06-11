package kr.jadekim.common.encoder.rlp

import kr.jadekim.common.extension.utf8
import kotlin.jvm.JvmInline

sealed interface RLPEncodableType {

    @JvmInline
    value class String(val value: ByteArray) : RLPEncodableType {
        constructor(value: kotlin.String) : this(value.utf8())
    }

    @JvmInline
    value class List(val value: kotlin.collections.List<RLPEncodableType>) : RLPEncodableType
}