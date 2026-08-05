package kim.jade.kotlinx.extension

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun String.utf8(): ByteArray = encodeToByteArray()

fun ByteArray.utf8(): String = decodeToString()

@OptIn(ExperimentalContracts::class)
fun String?.hasValue(blankIsValue: Boolean = false): Boolean {
    contract {
        returns(true) implies (this@hasValue != null)
    }

    return !hasNotValue(blankIsValue)
}

@OptIn(ExperimentalContracts::class)
fun String?.hasNotValue(blankIsValue: Boolean = false): Boolean {
    contract {
        returns(false) implies (this@hasNotValue != null)
    }

    return if (blankIsValue) isNullOrEmpty() else isNullOrBlank()
}