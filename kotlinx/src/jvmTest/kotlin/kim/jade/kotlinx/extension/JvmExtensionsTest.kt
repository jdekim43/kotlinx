package kim.jade.kotlinx.extension

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.util.Properties

class JvmExtensionsTest : DescribeSpec({
    describe("typed Properties accessors") {
        val properties = Properties().apply {
            setProperty("boolean", "true")
            setProperty("short", "12")
            setProperty("int", "123")
            setProperty("long", "1234")
            setProperty("float", "1.5")
            setProperty("double", "2.5")
            setProperty("string", "text")
        }

        context("given valid scalar values") {
            withData(typedPropertyAccessorVectors) { vector ->
                it("parses the expected value") {
                    vector.read(properties) shouldBe vector.expected
                }
            }
        }

        context("when a property is missing") {
            context("using a nullable accessor") {
                withData(nullablePropertyAccessorVectors) { read ->
                    it("returns null") {
                        read(properties) shouldBe null
                    }
                }
            }

            context("using an accessor with a default value") {
                withData(defaultPropertyAccessorVectors) { vector ->
                    it("returns the supplied fallback") {
                        vector.read(properties) shouldBe vector.expected
                    }
                }
            }
        }

        context("given a malformed numeric value") {
            withData(malformedNumberAccessors) { read ->
                it("propagates NumberFormatException") {
                    val malformed = Properties().apply { setProperty("number", "not-a-number") }

                    shouldThrow<NumberFormatException> { read(malformed) }
                }
            }
        }
    }

    describe("Properties loading extensions") {
        context("given readable files, a missing file, and a stream") {
            it("loads in order, skips missing input, and closes the stream") {
                val first = Files.createTempFile("properties-first", ".properties").toFile()
                val second = Files.createTempFile("properties-second", ".properties").toFile()
                try {
                    first.writeText("shared=first\nfirst=1\n")
                    second.writeText("shared=second\nsecond=2\n")
                    val properties = Properties()

                    properties.load(first, File(first.parentFile, "missing.properties"), second)

                    properties.getProperty("first") shouldBe "1"
                    properties.getProperty("second") shouldBe "2"
                    properties.getProperty("shared") shouldBe "second"

                    val stream = TrackingInputStream("stream=value\n".encodeToByteArray())
                    properties.load(listOf(stream))
                    properties.getProperty("stream") shouldBe "value"
                    stream.closed shouldBe true
                } finally {
                    first.delete()
                    second.delete()
                }
            }
        }

        context("given a directory tree") {
            it("loads file contents into dot-separated property names") {
                val directory = Files.createTempDirectory("file-properties").toFile()
                val nested = File(directory, "database").apply { mkdir() }
                val deeper = File(nested, "credentials").apply { mkdir() }
                try {
                    File(directory, "host").writeText("localhost")
                    File(nested, "port").writeText("5432\n")
                    File(deeper, "username").writeText("admin")
                    val properties = Properties()

                    properties.loadFileBasedProperties(directory, prefix = "app.")

                    properties.getProperty("app.host") shouldBe "localhost"
                    properties.getProperty("app.database.port") shouldBe "5432\n"
                    properties.getProperty("app.database.credentials.username") shouldBe "admin"
                } finally {
                    directory.deleteRecursively()
                }
            }
        }

        context("given a regular file as the file-based-properties root") {
            it("ignores it") {
                val file = Files.createTempFile("not-a-directory", ".txt").toFile()
                try {
                    val properties = Properties()
                    properties.loadFileBasedProperties(file)

                    properties.isEmpty shouldBe true
                } finally {
                    file.delete()
                }
            }
        }

        context("when only JVM system properties are requested") {
            it("imports them without loading environment variables") {
                val key = "kim.jade.kotlinx.test.system-property"
                val previous = System.getProperty(key)
                try {
                    System.setProperty(key, "system-value")
                    val properties = Properties()

                    properties.loadFromSystem(environmentVariables = false, properties = true)

                    properties.getProperty(key) shouldBe "system-value"
                } finally {
                    if (previous == null) System.clearProperty(key) else System.setProperty(key, previous)
                }
            }
        }
    }

    describe("KClass.qualifiedOrSimpleName") {
        context("given named and local classes") {
            it("returns a stable class name") {
                class LocalType

                String::class.qualifiedOrSimpleName shouldBe "kotlin.String"
                LocalType::class.qualifiedOrSimpleName shouldBe LocalType::class.java.name
            }
        }
    }
})

private data class PropertyAccessorVector(
    val read: (Properties) -> Any?,
    val expected: Any?,
)

private val typedPropertyAccessorVectors = mapOf(
    "Boolean" to PropertyAccessorVector({ it.getBoolean("boolean") }, true),
    "Short" to PropertyAccessorVector({ it.getShort("short") }, 12.toShort()),
    "Int" to PropertyAccessorVector({ it.getInt("int") }, 123),
    "Long" to PropertyAccessorVector({ it.getLong("long") }, 1234L),
    "Float" to PropertyAccessorVector({ it.getFloat("float") }, 1.5f),
    "Double" to PropertyAccessorVector({ it.getDouble("double") }, 2.5),
    "String" to PropertyAccessorVector({ it.getString("string") }, "text"),
)

private val nullablePropertyAccessorVectors = mapOf<String, (Properties) -> Any?>(
    "Boolean" to { it.getBoolean("missing") },
    "Short" to { it.getShort("missing") },
    "Int" to { it.getInt("missing") },
    "Long" to { it.getLong("missing") },
    "Float" to { it.getFloat("missing") },
    "Double" to { it.getDouble("missing") },
    "String" to { it.getString("missing") },
)

private val defaultPropertyAccessorVectors = mapOf(
    "Boolean" to PropertyAccessorVector({ it.getBoolean("missing", false) }, false),
    "Short" to PropertyAccessorVector({ it.getShort("missing", 2) }, 2.toShort()),
    "Int" to PropertyAccessorVector({ it.getInt("missing", 3) }, 3),
    "Long" to PropertyAccessorVector({ it.getLong("missing", 4) }, 4L),
    "Float" to PropertyAccessorVector({ it.getFloat("missing", 5f) }, 5f),
    "Double" to PropertyAccessorVector({ it.getDouble("missing", 6.0) }, 6.0),
    "String" to PropertyAccessorVector({ it.getString("missing", "fallback") }, "fallback"),
)

private val malformedNumberAccessors = mapOf<String, (Properties) -> Any?>(
    "Int" to { it.getInt("number") },
    "Double" to { it.getDouble("number") },
)

private class TrackingInputStream(bytes: ByteArray) : InputStream() {
    private val delegate = ByteArrayInputStream(bytes)
    var closed: Boolean = false
        private set

    override fun read(): Int = delegate.read()

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int = delegate.read(buffer, offset, length)

    override fun close() {
        closed = true
        delegate.close()
    }
}
