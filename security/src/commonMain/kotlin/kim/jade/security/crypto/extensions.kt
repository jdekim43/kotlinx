package kim.jade.security.crypto

import dev.whyoleg.cryptography.CryptographyAlgorithmId
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.Digest
import dev.whyoleg.cryptography.algorithms.HMAC
import kim.jade.encoding.Encoder
import kim.jade.encoding.encode
import kim.jade.kotlinx.extension.utf8

fun ByteArray.hash(algorithmId: CryptographyAlgorithmId<Digest>): ByteArray = CryptographyProvider.Default
    .get(algorithmId)
    .hasher()
    .hashBlocking(this)

fun String.hash(algorithmId: CryptographyAlgorithmId<Digest>): ByteArray = utf8().hash(algorithmId)

fun <EncodedType> ByteArray.hash(
    algorithmId: CryptographyAlgorithmId<Digest>,
    encoder: Encoder<ByteArray, EncodedType>,
): EncodedType = hash(algorithmId).encode(encoder)

fun <EncodedType> String.hash(
    algorithmId: CryptographyAlgorithmId<Digest>,
    encoder: Encoder<ByteArray, EncodedType>,
): EncodedType = hash(algorithmId).encode(encoder)

fun ByteArray.hash(
    algorithmId: CryptographyAlgorithmId<HMAC>,
    digest: CryptographyAlgorithmId<Digest>,
    key: ByteArray,
): ByteArray = CryptographyProvider.Default
    .get(algorithmId)
    .keyDecoder(digest)
    .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, key)
    .signatureGenerator()
    .generateSignatureBlocking(this)

fun String.hash(
    algorithmId: CryptographyAlgorithmId<HMAC>,
    digest: CryptographyAlgorithmId<Digest>,
    key: ByteArray,
): ByteArray = utf8().hash(algorithmId, digest, key)

fun ByteArray.verify(
    algorithmId: CryptographyAlgorithmId<HMAC>,
    digest: CryptographyAlgorithmId<Digest>,
    key: ByteArray,
    hash: ByteArray,
): Boolean = CryptographyProvider.Default
    .get(algorithmId)
    .keyDecoder(digest)
    .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, key)
    .signatureVerifier()
    .tryVerifySignatureBlocking(this, hash)

fun String.verify(
    algorithmId: CryptographyAlgorithmId<HMAC>,
    digest: CryptographyAlgorithmId<Digest>,
    key: ByteArray,
    hash: ByteArray,
): Boolean = utf8().verify(algorithmId, digest, key, hash)
