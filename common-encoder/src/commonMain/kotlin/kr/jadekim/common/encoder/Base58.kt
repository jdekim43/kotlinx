package kr.jadekim.common.encoder

/**
 * Base58 is a way to encode addresses (or arbitrary data) as alphanumeric strings.
 * Compared to base64, this encoding eliminates ambiguities created by O0Il and potential splits from punctuation
 *
 * The basic idea of the encoding is to treat the data bytes as a large number represented using
 * base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
 * number of leading zeros (which are otherwise lost during the mathematical operations on the
 * numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.
 *
 * This is the Kotlin implementation of base58 - it is based implementation of base58 in java
 * in bitcoinj (https://bitcoinj.github.io) - thanks to  Google Inc. and Andreas Schildbach
 *
 */
object Base58 : Encoder<ByteArray, String> {

    private const val ENCODED_ZERO = '1'

    private const val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private val alphabetIndices by lazy {
        IntArray(128) { alphabet.indexOf(it.toChar()) }
    }

    override fun encode(data: ByteArray): String {
        val input = data.copyOf(data.size) // since we modify it in-place
        if (input.isEmpty()) {
            return ""
        }
        // Count leading zeros.
        var zeros = 0
        while (zeros < input.size && input[zeros].toInt() == 0) {
            ++zeros
        }
        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
        val encoded = CharArray(input.size * 2) // upper bound
        var outputStart = encoded.size
        var inputStart = zeros
        while (inputStart < input.size) {
            encoded[--outputStart] = alphabet[divmod(input, inputStart.toUInt(), 256.toUInt(), 58.toUInt()).toInt()]
            if (input[inputStart].toInt() == 0) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in data.
        while (outputStart < encoded.size && encoded[outputStart] == ENCODED_ZERO) {
            ++outputStart
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = ENCODED_ZERO
        }
        // Return encoded string (including encoded leading zeros).
        return encoded.concatToString(outputStart, encoded.size)
    }

    override fun decode(data: String): ByteArray {
        if (data.isEmpty()) {
            return ByteArray(0)
        }
        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
        val input58 = ByteArray(data.length)
        for (i in 0 until data.length) {
            val c = data[i]
            val digit = if (c.code < 128) alphabetIndices[c.code] else -1
            if (digit < 0) {
                throw NumberFormatException("Illegal character $c at position $i")
            }
            input58[i] = digit.toByte()
        }
        // Count leading zeros.
        var zeros = 0
        while (zeros < input58.size && input58[zeros].toInt() == 0) {
            ++zeros
        }
        // Convert base-58 digits to base-256 digits.
        val decoded = ByteArray(data.length)
        var outputStart = decoded.size
        var inputStart = zeros
        while (inputStart < input58.size) {
            decoded[--outputStart] = divmod(input58, inputStart.toUInt(), 58.toUInt(), 256.toUInt()).toByte()
            if (input58[inputStart].toInt() == 0) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.size && decoded[outputStart].toInt() == 0) {
            ++outputStart
        }
        // Return decoded data (including original number of leading zeros).
        return decoded.copyOfRange(outputStart - zeros, decoded.size)
    }

    private fun divmod(number: ByteArray, firstDigit: UInt, base: UInt, divisor: UInt): UInt {
        // this is just long division which accounts for the base of the input digits
        var remainder = 0.toUInt()
        for (i in firstDigit until number.size.toUInt()) {
            val digit = number[i.toInt()].toUByte()
            val temp = remainder * base + digit
            number[i.toInt()] = (temp / divisor).toByte()
            remainder = temp % divisor
        }
        return remainder
    }
}