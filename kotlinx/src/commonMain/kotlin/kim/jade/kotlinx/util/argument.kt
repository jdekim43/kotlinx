package kim.jade.kotlinx.util

typealias Arguments = Map<String, List<String>>

fun parseArguments(vararg args: String): Arguments {
    val result = mutableMapOf<String, MutableList<String>>()

    for (arg in args) {
        val tokens = arg.split("=")

        val key = tokens[0]
        val value = if (tokens.size > 1) {
            tokens.slice(1 until tokens.size).joinToString("=")
        } else {
            ""
        }

        if (key.startsWith("--")) {
            result.getOrPut(key.substring(2)) { mutableListOf() }.add(value)
            continue
        }

        if (key.startsWith("-")) {
            result.getOrPut(key.substring(1)) { mutableListOf() }.add(value)
            continue
        }
    }

    return result
}