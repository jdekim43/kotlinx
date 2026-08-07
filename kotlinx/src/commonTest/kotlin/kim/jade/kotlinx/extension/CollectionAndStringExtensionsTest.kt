package kim.jade.kotlinx.extension

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withIts
import io.kotest.matchers.shouldBe

class CollectionAndStringExtensionsTest : DescribeSpec({

    describe("nullable value predicates") {
        context("when applied to collections") {
            withIts(collectionPredicateCases) { case ->
                case.value.hasValue() shouldBe case.hasValue
                case.value.hasNotValue() shouldBe !case.hasValue
            }
        }

        context("when applied to strings") {
            withIts(stringPredicateCases) { case ->
                case.value.hasValue(blankIsValue = case.blankIsValue) shouldBe case.hasValue
                case.value.hasNotValue(blankIsValue = case.blankIsValue) shouldBe !case.hasValue
            }
        }
    }

    describe("sequentialGroupBy") {
        context("given repeated adjacent and non-adjacent keys") {
            it("groups only adjacent equal keys") {
                val values = listOf("a1", "a2", "b1", "a3", "c1", "c2")

                values.sequentialGroupBy { it.first() } shouldBe listOf(
                    'a' to listOf("a1", "a2"),
                    'b' to listOf("b1"),
                    'a' to listOf("a3"),
                    'c' to listOf("c1", "c2"),
                )
            }
        }

        context("given an empty list") {
            it("produces no groups") {
                emptyList<Int>().sequentialGroupBy { it } shouldBe emptyList()
            }
        }
    }

    describe("withValue") {
        context("when using the iterable overload") {
            it("inserts defaults for missing sorted values") {
                val keys = listOf(1, 2, 3, 4)
                val values = listOf(Row(1, "one"), Row(3, "three"))

                keys.withValue(values, Row::key) { Row(it, "default-$it") }.toList() shouldBe listOf(
                    Row(1, "one"),
                    Row(2, "default-2"),
                    Row(3, "three"),
                    Row(4, "default-4"),
                )
            }

            it("preserves remaining values after every key has matched") {
                val keys = listOf(1, 2)
                val values = listOf(Row(1, "one"), Row(2, "two"), Row(3, "three"))

                keys.withValue(values, Row::key) { Row(it, "default-$it") }.toList() shouldBe values
            }
        }

        context("when using the map overload") {
            it("follows key order and creates defaults for absent keys") {
                listOf("b", "a", "c").withValue(mapOf("a" to 1, "b" to 2)) { -1 }.toList() shouldBe
                    listOf(2, 1, -1)
            }
        }

        context("before and after iterating the returned sequence") {
            it("is lazy and can be iterated more than once") {
                var defaultCalls = 0
                val aligned = listOf(1, 2).withValue(emptyMap<Int, String>()) {
                    defaultCalls += 1
                    "value-$it"
                }

                defaultCalls shouldBe 0
                aligned.toList() shouldBe listOf("value-1", "value-2")
                aligned.toList() shouldBe listOf("value-1", "value-2")
                defaultCalls shouldBe 4
            }
        }
    }

    describe("UTF-8 helpers") {
        context("given multilingual, emoji, and empty text") {
            it("round-trips between strings and bytes") {
                val value = "안녕하세요, Kotlin 🚀"

                value.utf8().utf8() shouldBe value
                "".utf8().utf8() shouldBe ""
            }
        }
    }

    describe("Boolean.toBinaryInt") {
        context("given either boolean value") {
            withIts(booleanBinaryCases) { case ->
                case.value.toBinaryInt() shouldBe case.expected
            }
        }
    }
})

private data class CollectionPredicateCase(
    val value: Collection<Int>?,
    val hasValue: Boolean,
)

private val collectionPredicateCases = mapOf(
    "treats null as having no value" to CollectionPredicateCase(null, false),
    "treats an empty collection as having no value" to CollectionPredicateCase(emptyList(), false),
    "treats a populated collection as having a value" to CollectionPredicateCase(listOf(1), true),
)

private data class StringPredicateCase(
    val value: String?,
    val blankIsValue: Boolean = false,
    val hasValue: Boolean,
)

private val stringPredicateCases = mapOf(
    "treats null as having no value" to StringPredicateCase(null, hasValue = false),
    "treats an empty string as having no value" to StringPredicateCase("", hasValue = false),
    "treats a blank string as having no value by default" to StringPredicateCase("  \n", hasValue = false),
    "treats a blank string as a value when configured" to
        StringPredicateCase("  \n", blankIsValue = true, hasValue = true),
    "treats non-blank text as having a value" to StringPredicateCase("value", hasValue = true),
)

private data class Row(
    val key: Int,
    val value: String,
)

private data class BooleanBinaryCase(
    val value: Boolean,
    val expected: Int,
)

private val booleanBinaryCases = mapOf(
    "maps false to zero" to BooleanBinaryCase(false, 0),
    "maps true to one" to BooleanBinaryCase(true, 1),
)
