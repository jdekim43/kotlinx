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

/**
 * Recursive Length Prefix (RLP) decoder.
 *
 * <p>For the specification, refer to p16 of the <a href="http://gavwood.com/paper.pdf">yellow
 * paper</a> and <a href="https://github.com/ethereum/wiki/wiki/RLP">here</a>.
 */
internal object RLPDecoder {

    /**
     * [0x80] If a string is 0-55 bytes long, the RLP encoding consists of a single byte with value
     * 0x80 plus the length of the string followed by the string. The range of the first byte is
     * thus [0x80, 0xb7].
     */
    const val OFFSET_SHORT_STRING: Int = 0x80

    /**
     * [0xb7] If a string is more than 55 bytes long, the RLP encoding consists of a single byte
     * with value 0xb7 plus the length of the length of the string in binary form, followed by the
     * length of the string, followed by the string. For example, a length-1024 string would be
     * encoded as \xb9\x04\x00 followed by the string. The range of the first byte is thus [0xb8,
     * 0xbf].
     */
    const val OFFSET_LONG_STRING: Int = 0xb7

    /**
     * [0xc0] If the total payload of a list (i.e. the combined length of all its items) is 0-55
     * bytes long, the RLP encoding consists of a single byte with value 0xc0 plus the length of the
     * list followed by the concatenation of the RLP encodings of the items. The range of the first
     * byte is thus [0xc0, 0xf7].
     */
    const val OFFSET_SHORT_LIST: Int = 0xc0

    /**
     * [0xf7] If the total payload of a list is more than 55 bytes long, the RLP encoding consists
     * of a single byte with value 0xf7 plus the length of the length of the list in binary form,
     * followed by the length of the list, followed by the concatenation of the RLP encodings of the
     * items. The range of the first byte is thus [0xf8, 0xff].
     */
    const val OFFSET_LONG_LIST: Int = 0xf7

    fun decode(rlpEncoded: ByteArray): RLPEncodableType {
        val list = mutableListOf<RLPEncodableType>()
        traverse(rlpEncoded, 0, rlpEncoded.size, list)
        return RLPEncodableType.List(list)
    }

    private fun traverse(data: ByteArray?, startPos: Int, endPos: Int, rlpList: MutableList<RLPEncodableType>) {
        var startPos = startPos
        try {
            if (data == null || data.isEmpty()) {
                return
            }

            if (endPos < 0 || endPos > data.size) {
                throw RuntimeException("RLP invalid parameters while decoding")
            }

            while (startPos < endPos) {
                val prefix = data[startPos].toInt() and 0xff

                if (prefix < OFFSET_SHORT_STRING) {
                    // 1. the data is a string if the range of the
                    // first byte(i.e. prefix) is [0x00, 0x7f],
                    // and the string is the first byte itself exactly;

                    val rlpData = byteArrayOf(prefix.toByte())
                    rlpList.add(RLPEncodableType.String(rlpData))
                    startPos += 1
                } else if (prefix == OFFSET_SHORT_STRING) {
                    // null

                    rlpList.add(RLPEncodableType.String(ByteArray(0)))
                    startPos += 1
                } else if (prefix > OFFSET_SHORT_STRING && prefix <= OFFSET_LONG_STRING) {
                    // 2. the data is a string if the range of the
                    // first byte is [0x80, 0xb7], and the string
                    // which length is equal to the first byte minus 0x80
                    // follows the first byte;

                    val strLen = (prefix - OFFSET_SHORT_STRING)

                    // Input validation
                    if (strLen > endPos - (startPos + 1)) {
                        throw RuntimeException("RLP length mismatch")
                    }

                    val rlpData = ByteArray(strLen)
                    data.copyInto(rlpData, 0, startPos + 1, startPos + 1 + strLen)

                    rlpList.add(RLPEncodableType.String(rlpData))
                    startPos += 1 + strLen
                } else if (prefix > OFFSET_LONG_STRING && prefix < OFFSET_SHORT_LIST) {
                    // 3. the data is a string if the range of the
                    // first byte is [0xb8, 0xbf], and the length of the
                    // string which length in bytes is equal to the
                    // first byte minus 0xb7 follows the first byte,
                    // and the string follows the length of the string;

                    val lenOfStrLen = (prefix - OFFSET_LONG_STRING).toByte()
                    val strLen = calcLength(lenOfStrLen.toInt(), data, startPos)

                    // Input validation
                    if (strLen > endPos - (startPos + lenOfStrLen + 1)) {
                        throw RuntimeException("RLP length mismatch")
                    }

                    // now we can parse an item for data[1]..data[length]
                    val rlpData = ByteArray(strLen)
                    data.copyInto(rlpData, 0, startPos + lenOfStrLen + 1, startPos + lenOfStrLen + 1 + strLen)

                    rlpList.add(RLPEncodableType.String(rlpData))
                    startPos += lenOfStrLen + strLen + 1
                } else if (prefix in OFFSET_SHORT_LIST..OFFSET_LONG_LIST) {
                    // 4. the data is a list if the range of the
                    // first byte is [0xc0, 0xf7], and the concatenation of
                    // the RLP encodings of all items of the list which the
                    // total payload is equal to the first byte minus 0xc0 follows the first byte;

                    val listLen = (prefix - OFFSET_SHORT_LIST).toByte()

                    val newLevelList = mutableListOf<RLPEncodableType>()
                    traverse(data, startPos + 1, startPos + listLen + 1, newLevelList)
                    rlpList.add(RLPEncodableType.List(newLevelList))

                    startPos += 1 + listLen
                } else if (prefix > OFFSET_LONG_LIST) {
                    // 5. the data is a list if the range of the
                    // first byte is [0xf8, 0xff], and the total payload of the
                    // list which length is equal to the
                    // first byte minus 0xf7 follows the first byte,
                    // and the concatenation of the RLP encodings of all items of
                    // the list follows the total payload of the list;

                    val lenOfListLen = (prefix - OFFSET_LONG_LIST).toByte()
                    val listLen = calcLength(lenOfListLen.toInt(), data, startPos)

                    val newLevelList = mutableListOf<RLPEncodableType>()
                    traverse(
                        data,
                        startPos + lenOfListLen + 1,
                        startPos + lenOfListLen + listLen + 1,
                        newLevelList
                    )
                    rlpList.add(RLPEncodableType.List(newLevelList))

                    startPos += lenOfListLen + listLen + 1
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("RLP wrong encoding", e)
        }
    }

    private fun calcLength(lengthOfLength: Int, data: ByteArray, pos: Int): Int {
        var pow = (lengthOfLength - 1).toByte()
        var length: Long = 0
        for (i in 1..lengthOfLength) {
            length += ((data[pos + i].toInt() and 0xff).toLong()) shl (8 * pow)
            pow--
        }
        if (length < 0 || length > Int.Companion.MAX_VALUE) {
            throw RuntimeException("RLP too many bytes to decode")
        }
        return length.toInt()
    }
}