package kim.jade.kotlinx.dsl

inline fun <T> Boolean.then(block: () -> T) = if (this) block() else null
