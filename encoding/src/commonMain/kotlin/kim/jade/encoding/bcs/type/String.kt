package kim.jade.encoding.bcs.type

import kim.jade.encoding.bcs.BCSReader
import kim.jade.encoding.bcs.BCSWriter
import kotlin.String as KString

object String : BCSType<KString> {

    override fun serialize(
        value: KString,
        writer: BCSWriter
    ): BCSWriter = ByteVector.serialize(value.encodeToByteArray())

    override fun deserialize(reader: BCSReader): KString = ByteVector.deserialize(reader).decodeToString()
}