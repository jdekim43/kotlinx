package kim.jade.kotlinx.dsl

import kim.jade.kotlinx.annotation.Experimental

@Experimental
interface TransactionManager<O : TransactionManager.TransactionOptions> {

    companion object {
        private val managers = mutableMapOf<String, TransactionManager<TransactionOptions>>()

        @Suppress("UNCHECKED_CAST")
        fun <O : TransactionOptions> register(name: String, manager: TransactionManager<O>) {
            managers[name] = manager as TransactionManager<TransactionOptions>
        }

        suspend fun <T> inTransaction(
            name: String,
            options: (TransactionOptions.() -> Unit)? = null,
            block: suspend () -> T,
        ): T =
            managers[name]?.inTransaction(options, block)
                ?: throw IllegalArgumentException("No transaction manager found for key: $options")
    }

    open class TransactionOptions(
        var isolation: Int? = null,
        var readOnly: Boolean? = null,
    )

    fun createDefaultOptions(): O

    suspend fun <T> inTransaction(options: O?, block: suspend () -> T): T

    suspend fun <T> inTransaction(options: (O.() -> Unit)? = null, block: suspend () -> T): T =
        inTransaction(options?.let { createDefaultOptions().apply(it) }, block)
}

@Experimental
suspend fun <T> transactional(
    name: String,
    options: (TransactionManager.TransactionOptions.() -> Unit)? = null,
    block: suspend () -> T
): T =
    TransactionManager.inTransaction(name, options, block)
