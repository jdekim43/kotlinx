package kim.jade.kotlinx.exception

import java.io.PrintStream
import java.io.PrintWriter

private fun Iterable<Throwable>.distinctList(): List<Throwable> {
    val distinctExceptions = LinkedHashSet<Throwable>()

    for (exception in this) {
        if (exception is CompositeException) {
            distinctExceptions.addAll(exception.exceptions)
        } else {
            distinctExceptions.add(exception)
        }
    }

    return distinctExceptions.toList()
}

private val lineSeparator: String = System.lineSeparator()

private fun StringBuilder.indent(depth: Int): StringBuilder {
    repeat(depth) { append(' ') }

    return this
}

class CompositeException private constructor(val exceptions: List<Throwable>) : Exception() {

    constructor(exceptions: Iterable<Throwable>) : this(exceptions.distinctList())

    @Suppress("unused")
    constructor(vararg exceptions: Throwable) : this(exceptions.asIterable())

    val size = exceptions.size

    override val message: String = "$size exceptions occurred."

    override val cause: Throwable? by lazy {
        if (size <= 1) {
            return@lazy exceptions.firstOrNull()
        }

        val seenCauses = HashMap<Throwable, Boolean>()
        val messageBuilder = StringBuilder()
        messageBuilder.append("Multiple exceptions (${size})")
            .append(lineSeparator)

        for (exception in exceptions) {
            var depth = 0
            var inner: Throwable? = exception
            while (inner != null) {
                messageBuilder.indent(depth)
                    .append("|-- ")
                    .append(inner::class.qualifiedName)
                    .append(": ")
                val innerMessage = inner.message
                if (innerMessage != null && innerMessage.contains(lineSeparator)) {
                    messageBuilder.append(lineSeparator)
                    for (line in innerMessage.split(lineSeparator)) {
                        messageBuilder.indent(depth + 2)
                            .append(line)
                            .append(lineSeparator)
                    }
                } else {
                    messageBuilder.append(innerMessage)
                        .append(lineSeparator)
                }

                messageBuilder.indent(depth + 2)

                val stackTrace = inner.stackTrace
                if (stackTrace.isNotEmpty()) {
                    messageBuilder.append("at ")
                        .append(stackTrace[0])
                        .append(lineSeparator)
                }

                if (seenCauses.containsKey(inner)) {
                    inner.cause?.let {
                        messageBuilder.indent(depth + 2)
                            .append("|-- ")
                            .append("(cause not expanded again) ")
                            .append(it::class.qualifiedName)
                            .append(": ")
                            .append(it.message)
                            .append(lineSeparator)
                    }

                    break
                } else {
                    seenCauses[inner] = true

                    inner = inner.cause
                    depth += 1
                }
            }
        }

        ExceptionOverview(messageBuilder.toString().trim())
    }

    override fun printStackTrace() {
        printStackTrace(System.err)
    }

    override fun printStackTrace(s: PrintStream) {
        printStackTrace(WrappedPrintStream(s))
    }

    override fun printStackTrace(s: PrintWriter) {
        printStackTrace(WrappedPrintWriter(s))
    }

    private fun printStackTrace(output: PrintStreamOrWriter) {
        output.append(this).append('\n')
        for (stackElement in stackTrace) {
            output.append("\tat ").append(stackElement).append('\n')
        }

        var i = 1
        for (e in exceptions) {
            output.append("  ComposedException ").append(i).append(" :\n")
            appendStackTrace(output, e, "\t")
            i += 1
        }

        output.append('\n')
    }

    private fun appendStackTrace(output: PrintStreamOrWriter, e: Throwable, prefix: String) {
        output.append(prefix).append(e).append('\n')
        for (stackElement in e.stackTrace) {
            output.append("\t\tat ").append(stackElement).append('\n')
        }
        if (e.cause != null) {
            output.append("\tCaused by: ")
            appendStackTrace(output, e.cause!!, "")
        }
    }

    class ExceptionOverview(message: String?) : RuntimeException(message) {

        override fun fillInStackTrace(): Throwable {
            return this
        }
    }

    private abstract class PrintStreamOrWriter {

        abstract fun append(o: Any): PrintStreamOrWriter
    }

    private class WrappedPrintStream(private val stream: PrintStream) : PrintStreamOrWriter() {

        override fun append(o: Any): PrintStreamOrWriter {
            stream.print(o)

            return this
        }
    }

    private class WrappedPrintWriter(private val writer: PrintWriter) : PrintStreamOrWriter() {

        override fun append(o: Any): PrintStreamOrWriter {
            writer.print(o)

            return this
        }
    }
}