package kim.jade.kotlinx.io

import node.process.process

actual fun environmentVariable(name: String): String? {
    return process.env[name]
}