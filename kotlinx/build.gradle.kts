plugins {
    id("convention.kotlin-multiplatform")
    id("convention.publish")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
//        all {
//            languageSettings.optIn("kotlin.RequiresOptIn")
//            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
//        }

        commonMain.dependencies {
            compileOnly(kt.kotlinx.coroutine)
        }

        nativeMain.dependencies {
            api(kt.kotlinx.coroutine)
        }

        webMain.dependencies {
            api(kt.kotlinx.coroutine)
            implementation(kotlinWrappers.node)
        }
    }
}
