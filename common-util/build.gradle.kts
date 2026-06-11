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

        commonMain.dependencies {
            implementation(kotlincrypto.random.crypto.rand)

            compileOnly(kt.kotlinx.coroutine)
        }

        webMain.dependencies {
            implementation(kotlinWrappers.web)
        }
    }
}
