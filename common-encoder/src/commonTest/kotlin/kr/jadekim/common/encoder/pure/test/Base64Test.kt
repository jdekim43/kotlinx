package kr.jadekim.common.encoder.pure.test

import kr.jadekim.common.encoder.Base64
import kr.jadekim.common.extension.utf8
import kotlin.test.Test
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun succeed() {
        val original = "testText1$"

        val encoded = Base64.encode(original.utf8())
        assertEquals("dGVzdFRleHQxJA==", encoded)

        val decoded = Base64.decode(encoded).utf8()
        assertEquals(original, decoded)
    }
}