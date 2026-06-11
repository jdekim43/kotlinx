plugins {
    id("convention.kotlin-multiplatform")
    id("convention.publish")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common-util"))
            implementation(project(":common-encoder"))

            implementation(kt.kotlinx.coroutine)

            implementation(libs.ionspin.bignum)

            implementation(kotlincrypto.hash.md)
            implementation(kotlincrypto.hash.sha1)
            implementation(kotlincrypto.hash.sha2)
            implementation(kotlincrypto.hash.sha3)
            implementation(kotlincrypto.macs.hmac.md)
            implementation(kotlincrypto.macs.hmac.sha1)
            implementation(kotlincrypto.macs.hmac.sha2)
            implementation(kotlincrypto.macs.hmac.sha3)
        }
        webMain.dependencies {
            implementation(libs.cryptography.core)
        }
        nativeMain.dependencies {
            implementation(libs.cryptography.core)
        }
    }
}
