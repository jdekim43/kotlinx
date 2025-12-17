kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }

        jvmMain.dependencies {
            compileOnly(libs.kotlinx.coroutine.core)
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
