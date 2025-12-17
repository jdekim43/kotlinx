package kr.jadekim.common.encoder.bcs.type

import kr.jadekim.common.encoder.bcs.BCSReader
import kr.jadekim.common.encoder.bcs.BCSWriter

class FixedArray<T>(val size: Int, val type: BCSType<T>) : BCSType<List<T>> {

    override fun serialize(
        value: List<T>,
        writer: BCSWriter
    ): BCSWriter {
        value.forEach {
            type.serialize(it, writer)
        }

        return writer
    }

    override fun deserialize(reader: BCSReader): List<T> = List(size) {
        type.deserialize(reader)
    }
}