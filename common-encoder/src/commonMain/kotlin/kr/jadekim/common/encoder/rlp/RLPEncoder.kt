/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package kr.jadekim.common.encoder.rlp

import kr.jadekim.common.encoder.rlp.RLPDecoder.OFFSET_SHORT_LIST
import kr.jadekim.common.encoder.rlp.RLPDecoder.OFFSET_SHORT_STRING

/**
 * Recursive Length Prefix (RLP) encoder.
 *
 * <p>For the specification, refer to p16 of the <a href="http://gavwood.com/paper.pdf">yellow
 * paper</a> and <a href="https://github.com/ethereum/wiki/wiki/RLP">here</a>.
 */
internal object RLPEncoder {

    fun encode(value: RLPEncodableType): ByteArray = when (value) {
        is RLPEncodableType.List -> encode(value)
        is RLPEncodableType.String -> encode(value)
    }

    private fun encode(bytesValue: ByteArray, offset: Int): ByteArray {
        if (bytesValue.size == 1 && offset == OFFSET_SHORT_STRING && bytesValue[0] >= 0x00.toByte() && bytesValue[0] <= 0x7f.toByte()) {
            return bytesValue
        } else if (bytesValue.size <= 55) {
            val result = ByteArray(bytesValue.size + 1)
            result[0] = (offset + bytesValue.size).toByte()
            bytesValue.copyInto(result, 1)
            return result
        } else {
            val encodedStringLength = toMinimalByteArray(bytesValue.size)
            val result = ByteArray(bytesValue.size + encodedStringLength.size + 1)

            result[0] = ((offset + 0x37) + encodedStringLength.size).toByte()
            encodedStringLength.copyInto(result, 1)
            bytesValue.copyInto(result, 1 + encodedStringLength.size)
            return result
        }
    }

    fun encode(value: RLPEncodableType.String): ByteArray {
        return encode(value.value, OFFSET_SHORT_STRING)
    }

    private fun toMinimalByteArray(value: Int): ByteArray {
        val encoded = toByteArray(value)

        for (i in encoded.indices) {
            if (encoded[i].toInt() != 0) {
                return encoded.copyOfRange(i, encoded.size)
            }
        }

        return byteArrayOf()
    }

    private fun toByteArray(value: Int): ByteArray {
        return byteArrayOf(
            ((value shr 24) and 0xff).toByte(),
            ((value shr 16) and 0xff).toByte(),
            ((value shr 8) and 0xff).toByte(),
            (value and 0xff).toByte()
        )
    }

    fun encode(value: RLPEncodableType.List): ByteArray {
        val values: List<RLPEncodableType> = value.value
        if (values.isEmpty()) {
            return encode(byteArrayOf(), OFFSET_SHORT_LIST)
        } else {
            var result = ByteArray(0)
            for (entry in values) {
                result += encode(entry)
            }
            return encode(result, OFFSET_SHORT_LIST)
        }
    }
}