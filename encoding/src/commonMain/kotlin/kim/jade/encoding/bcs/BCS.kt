package kim.jade.encoding.bcs

import kim.jade.encoding.Encoder
import kim.jade.encoding.bcs.type.*
import kotlin.String as KString
import kotlin.collections.Map as KMap

object BCS : Encoder<ByteArray, ByteArray> {

    fun bool() = Bool

    fun bytes(size: Int) = Bytes(size)

    fun byteVector() = ByteVector

    fun enum(types: KMap<KString, BCSType<Any>>) = Enum(types)

    fun <T> fixedArray(size: Int, type: BCSType<T>) = FixedArray(size, type)

    fun <K, V> map(keyType: BCSType<K>, valueType: BCSType<V>) = Map(keyType, valueType)

    fun <T> option(type: BCSType<T>) = Option(type)

    fun <A, B> pair(first: BCSType<A>, second: BCSType<B>) = Pair(first, second)

    fun string() = String

    fun struct(types: KMap<KString, BCSType<Any>>) = Struct(types)

    fun <A, B, C> triple(first: BCSType<A>, second: BCSType<B>, third: BCSType<C>) = Triple(first, second, third)

    fun i8() = I8

    fun i16() = I16

    fun i32() = I32

    fun i64() = I64

    fun i128() = I128

    fun i256() = I256

    fun u8() = U8

    fun u16() = U16

    fun u32() = U32

    fun u64() = U64

    fun u128() = U128

    fun u256() = U256

    fun uleb128() = ULEB128

    fun <T> vector(type: BCSType<T>) = Vector(type)

    override fun encode(data: ByteArray): ByteArray = ByteVector.serialize(data).toByteArray()

    override fun decode(data: ByteArray): ByteArray = ByteVector.deserialize(data)
}
