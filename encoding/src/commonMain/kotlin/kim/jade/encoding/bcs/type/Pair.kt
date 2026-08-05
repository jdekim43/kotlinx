package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kotlin.Pair as KPair

class Pair<A, B>(val first: BCSType<A>, val second: BCSType<B>) : BCSType<KPair<A, B>> {

    override fun serialize(
        value: KPair<A, B>,
        writer: BCSWriter
    ): BCSWriter {
        first.serialize(value.first, writer)
        second.serialize(value.second, writer)

        return writer
    }

    override fun deserialize(reader: BCSReader): KPair<A, B> =
        first.deserialize(reader) to second.deserialize(reader)
}