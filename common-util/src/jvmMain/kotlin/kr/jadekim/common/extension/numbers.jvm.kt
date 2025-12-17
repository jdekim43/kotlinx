package kr.jadekim.common.extension

import java.math.BigDecimal

fun BigDecimal.equalValue(other: BigDecimal): Boolean {
    return compareTo(other) == 0
}

fun BigDecimal.notEqualValue(other: BigDecimal) = !equalValue(other)

infix fun BigDecimal.equal(other: BigDecimal) = equalValue(other)

infix fun BigDecimal.notEqual(other: BigDecimal) = !equalValue(other)