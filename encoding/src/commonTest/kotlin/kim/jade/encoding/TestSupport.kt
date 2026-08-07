package kim.jade.encoding

import io.kotest.matchers.shouldBe

internal fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
    values[index].toByte()
}

internal fun ByteArray.shouldHaveBytes(vararg expected: Int) {
    toList() shouldBe expected.map(Int::toByte)
}

internal fun ByteArray.shouldHaveBytes(expected: ByteArray) {
    toList() shouldBe expected.toList()
}
