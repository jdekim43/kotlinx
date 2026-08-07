package kim.jade.kotlinx.exception

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.util.Properties

class ResourceExceptionDisplayMessageResolverTest : DescribeSpec({

    val en = Locale.from("en", "US")
    val ko = Locale.from("ko", "KR")

    describe("ResourceExceptionDisplayMessageResolver") {
        context("when Properties are loaded for a locale") {
            it("converts every property into a code-to-message entry") {
                val resolver = ResourceExceptionDisplayMessageResolver()
                resolver.load(
                    en,
                    Properties().apply {
                        setProperty("NOT_FOUND", "Not found")
                        setProperty("UNAUTHORIZED", "Unauthorized")
                    },
                )

                resolver.getMessage("NOT_FOUND", en) shouldBe "Not found"
                resolver.getMessage("UNAUTHORIZED", en) shouldBe "Unauthorized"
            }
        }

        context("when file and stream overloads are used") {
            it("loads both sources and closes the supplied stream") {
                val resolver = ResourceExceptionDisplayMessageResolver()
                val file = Files.createTempFile("exception-messages", ".properties").toFile()
                try {
                    file.writeText("FILE_ERROR=Loaded from file\n")
                    resolver.load(en, file)

                    val stream = CloseTrackingInputStream(
                        "STREAM_ERROR=Loaded from stream\n".encodeToByteArray(),
                    )
                    resolver.load(ko, stream)

                    resolver.getMessage("FILE_ERROR", en) shouldBe "Loaded from file"
                    resolver.getMessage("STREAM_ERROR", ko) shouldBe "Loaded from stream"
                    stream.closed shouldBe true
                } finally {
                    file.delete()
                }
            }
        }

        context("when resource paths or files are missing") {
            it("ignores them and retains the default message") {
                val resolver = ResourceExceptionDisplayMessageResolver()

                resolver.load("resource-that-does-not-exist")
                resolver.load(File("file-that-does-not-exist"))

                resolver.getMessage("unknown", en) shouldBe "An error occurred."
            }
        }
    }
})

private class CloseTrackingInputStream(bytes: ByteArray) : InputStream() {
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
