package kim.jade.security

import io.kotest.matchers.shouldBe
import kim.jade.encoding.Hex

internal fun ByteArray.shouldHaveHex(expected: String) {
    Hex.encode(this) shouldBe expected.uppercase()
}

internal fun String.hexBytes(): ByteArray = Hex.decode(this)
