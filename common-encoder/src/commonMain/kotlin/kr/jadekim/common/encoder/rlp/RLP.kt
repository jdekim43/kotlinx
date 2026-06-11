package kr.jadekim.common.encoder.rlp

object RLP : kr.jadekim.common.encoder.Encoder<RLPEncodableType, ByteArray> {

    override fun encode(data: RLPEncodableType): ByteArray = RLPEncoder.encode(data)

    override fun decode(data: ByteArray): RLPEncodableType = RLPDecoder.decode(data)
}
