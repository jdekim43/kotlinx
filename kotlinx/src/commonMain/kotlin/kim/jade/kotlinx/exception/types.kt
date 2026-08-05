package kim.jade.kotlinx.exception

import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

typealias ErrorCode = String

@JvmInline
value class Locale private constructor(val value: String) {
    companion object {

        @JvmStatic
        fun from(language: String, region: String, script: String? = null): Locale {
            if (language.length != 2) {
                throw IllegalArgumentException("Invalid language code: $language")
            }

            if (script != null && script.length != 4) {
                throw IllegalArgumentException("Invalid script code: $script")
            }

            if (region.length != 2) {
                throw IllegalArgumentException("Invalid region code: $region")
            }

            val language = language.lowercase()
            val script = script?.toCharArray()?.apply { set(0, get(0).uppercaseChar()) }?.concatToString()
            val region = region.uppercase()

            return if (script == null) {
                Locale("$language-$region")
            } else {
                Locale("$language-$script-$region")
            }
        }

        @JvmStatic
        fun from(value: String): Locale {
            val tokens = value.split("-")

            var (language, script, region) = when (tokens.size) {
                2 -> Triple(tokens[0], null, tokens[1])
                3 -> Triple(tokens[0], tokens[1], tokens[2])
                else -> throw IllegalArgumentException("Invalid locale format: $value")
            }

            return from(language, region, script)
        }
    }
}