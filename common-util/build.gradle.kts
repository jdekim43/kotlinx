kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
        }

        commonMain.dependencies {
            implementation(kotlincrypto.random.crypto.rand)

            compileOnly(libs.kotlinx.coroutine.core)
        }

        jvmMain.dependencies {
            compileOnly(libs.kotlinx.coroutine.core)
        }

        webMain.dependencies {
            implementation(kotlinWrappers.web)
        }
    }
}
