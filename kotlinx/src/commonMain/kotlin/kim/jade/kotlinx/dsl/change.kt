package kim.jade.kotlinx.dsl

class ChangeContext<T>(val before: T, val after: T) {

    inline fun T.to(to: T, crossinline block: () -> Unit) {
        if (this@ChangeContext.before == this && this@ChangeContext.after == to) {
            block()
        }
    }

    inline fun case(change: Pair<T, T>, crossinline block: () -> Unit) = change.first.to(change.second, block)
}

/**
 * fun testFunction(a: Int, b: Int) {
 *     matchChanges(a, b) {
 *         1.to(3) { //or case(1 to 3)
 *             // if a == 1 and b == 3, executed
 *         }
 *         case(2 to 4) { //or 2.to(4)
 *             // if a == 1 and b == 3, not executed
 *         }
 *     }
 * }
 */
inline fun <T> matchChanges(before: T, after: T, crossinline block: ChangeContext<T>.() -> Unit) {
    ChangeContext(before, after).block()
}
