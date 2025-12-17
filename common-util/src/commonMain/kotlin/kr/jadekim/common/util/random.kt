package kr.jadekim.common.util

import kr.jadekim.common.extension.toIntWithinBigEndian
import org.kotlincrypto.random.CryptoRand
import kotlin.math.log
import kotlin.math.log10
import kotlin.math.pow
import kotlin.random.Random

//todo: remove external library
//using js native crypto library
object SecureRandom : Random() {

    override fun nextBits(bitCount: Int): Int {
        val randomInt = ByteArray(Int.SIZE_BYTES)
            .apply(CryptoRand::nextBytes)
            .toIntWithinBigEndian()

        return randomInt.ushr(32 - bitCount) and (-bitCount).shr(31)
    }
}

private val DEFAULT_RANDOM: Random = SecureRandom

private const val RANDOM_STRING_START_ASCII = 97
private const val RANDOM_STRING_END_ASCII = 122
private const val RANDOM_STRING_LENGTH = 8

fun randomString(length: Int = RANDOM_STRING_LENGTH, random: Random = DEFAULT_RANDOM): String {
    val builder = StringBuilder(length)

    for (i in 0 until length) {
        val randomLimitedInt = random.nextInt(RANDOM_STRING_START_ASCII, RANDOM_STRING_END_ASCII)
        builder.append(randomLimitedInt.toChar())
    }

    return builder.toString()
}

private val UNIQUE_STRING_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()
private val UNIQUE_STRING_CHARACTERS_SIZE = UNIQUE_STRING_CHARACTERS.size

fun uniqueString(length: Int = 8, random: Random = DEFAULT_RANDOM): String {
    val now = currentTimeMillis()
    val nowDouble = now.toDouble()

    val nowEncodeLength = log(nowDouble, UNIQUE_STRING_CHARACTERS_SIZE.toDouble()).toInt() + 1
    if (nowEncodeLength > length) {
        throw IllegalArgumentException("Must greater than $nowEncodeLength")
    }

    val nowLength = log10(nowDouble).toInt() + 1

    val randomStart = UNIQUE_STRING_CHARACTERS_SIZE.toDouble().pow(length - 1).toLong()
    val randomEnd = UNIQUE_STRING_CHARACTERS_SIZE.toDouble().pow(length).toLong()
    var randomValue = random.nextLong(randomStart, randomEnd)
    randomValue -= (randomValue % (10.0.pow(nowLength))).toLong()

    return (randomValue + now).radix(UNIQUE_STRING_CHARACTERS.size, UNIQUE_STRING_CHARACTERS)
}

fun strictUniqueString(randomSize: Int = 2, random: Random = DEFAULT_RANDOM): String {
    val now = currentTimeMillis()
    val offset = 10.0.pow(randomSize).toLong()
    val randomValue = random.nextLong(offset / 10, offset - 1)

    return ((now * offset) + randomValue).radix(UNIQUE_STRING_CHARACTERS.size, UNIQUE_STRING_CHARACTERS)
}
