package kr.jadekim.common.encoder

import kr.jadekim.common.crypto.hash.SHA_256
import kr.jadekim.common.crypto.hash.hash

object Base58WithChecksum : Encoder<ByteArray, String> {

    private const val CHECKSUM_SIZE = 4

    override fun encode(data: ByteArray): String = ByteArray(data.size + CHECKSUM_SIZE).apply {
        data.copyInto(this)
        val checksum = data.hash(SHA_256).hash(SHA_256)
        checksum.copyInto(this, data.size, 0, CHECKSUM_SIZE)

    }.let(Base58::encode)

    override fun decode(data: String): ByteArray {
        val rawBytes = Base58.decode(data)
        if (rawBytes.size < CHECKSUM_SIZE) {
            throw Exception("Too short for checksum: $this l:  ${rawBytes.size}")
        }
        val checksum = rawBytes.copyOfRange(rawBytes.size - CHECKSUM_SIZE, rawBytes.size)

        val payload = rawBytes.copyOfRange(0, rawBytes.size - CHECKSUM_SIZE)

        val hash = payload.hash(SHA_256).hash(SHA_256)
        val computedChecksum = hash.copyOfRange(0, CHECKSUM_SIZE)

        if (checksum.contentEquals(computedChecksum)) {
            return payload
        } else {
            throw IllegalArgumentException("Checksum mismatch: input = ${checksum.encode(Hex)}, expected = ${computedChecksum.encode(Hex)}")
        }
    }
}