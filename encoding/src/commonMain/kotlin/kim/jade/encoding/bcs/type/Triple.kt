package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kotlin.Triple as KTriple

class Triple<A, B, C>(
    val first: BCSType<A>,
    val second: BCSType<B>,
    val third: BCSType<C>,
) : BCSType<KTriple<A, B, C>> {

    override fun serialize(
        value: KTriple<A, B, C>,
        writer: BCSWriter
    ): BCSWriter {
        first.serialize(value.first, writer)
        second.serialize(value.second, writer)
        third.serialize(value.third, writer)

        return writer
    }

    override fun deserialize(reader: BCSReader): KTriple<A, B, C> =
        KTriple(first.deserialize(reader), second.deserialize(reader), third.deserialize(reader))
}