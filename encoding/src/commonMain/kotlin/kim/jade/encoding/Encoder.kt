package kim.jade.encoding

interface Encoder<OriginalType, EncodedType> {

    fun encode(data: OriginalType): EncodedType

    fun decode(data: EncodedType): OriginalType
}

fun <OriginalType, EncodedType> OriginalType.encode(encoder: Encoder<OriginalType, EncodedType>) = encoder.encode(this)

fun <OriginalType, EncodedType> EncodedType.decode(encoder: Encoder<OriginalType, EncodedType>) = encoder.decode(this)

class EncoderException(cause: Throwable?) : RuntimeException(cause?.message, cause)

internal fun <OriginalType, EncodedType> Encoder(
    encode: (OriginalType) -> EncodedType,
    decode: (EncodedType) -> OriginalType
) = object : Encoder<OriginalType, EncodedType> {

    override fun encode(data: OriginalType): EncodedType = try {
        encode(data)
    } catch (e: Exception) {
        throw EncoderException(e)
    }

    override fun decode(data: EncodedType): OriginalType = try {
        decode(data)
    } catch (e: Exception) {
        throw EncoderException(e)
    }
}
