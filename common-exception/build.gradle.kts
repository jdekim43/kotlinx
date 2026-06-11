plugins {
    id("convention.kotlin-multiplatform")
    id("convention.publish")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }

        jvmMain.dependencies {
            compileOnly(kt.kotlinx.coroutine)
        }
//        val jsMain by getting {
//            dependencies {
//                val kryptoVersion: String by project
//
//                implementation("com.soywiz.korlibs.krypto:krypto-js:$kryptoVersion")
//            }
//        }
    }
}
