package kim.jade.encoding

import dev.whyoleg.cryptography.algorithms.SHA256
import kim.jade.security.crypto.hash

typealias Base58WithChecksum = Base58Check

object Base58Check : Encoder<ByteArray, String> {

    private const val CHECKSUM_SIZE = 4

    override fun encode(data: ByteArray): String = ByteArray(data.size + CHECKSUM_SIZE).apply {
        data.copyInto(this)
        val checksum = data.hash(SHA256).hash(SHA256)
        checksum.copyInto(this, data.size, 0, CHECKSUM_SIZE)

    }.let(Base58::encode)

    override fun decode(data: String): ByteArray {
        val rawBytes = Base58.decode(data)
        if (rawBytes.size < CHECKSUM_SIZE) {
            throw Exception("Too short for checksum: $this l:  ${rawBytes.size}")
        }
        val checksum = rawBytes.copyOfRange(rawBytes.size - CHECKSUM_SIZE, rawBytes.size)

        val payload = rawBytes.copyOfRange(0, rawBytes.size - CHECKSUM_SIZE)

        val hash = payload.hash(SHA256).hash(SHA256)
        val computedChecksum = hash.copyOfRange(0, CHECKSUM_SIZE)

        if (checksum.contentEquals(computedChecksum)) {
            return payload
        } else {
            throw IllegalArgumentException(
                "Checksum mismatch: input = ${checksum.encode(Hex)}, expected = ${
                    computedChecksum.encode(
                        Hex
                    )
                }"
            )
        }
    }
}