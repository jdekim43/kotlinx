plugins {
    id("convention.kotlin-multiplatform")
    id("convention.publish")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":kotlinx"))
            implementation(project(":kotlinx-encoding"))

            api(libs.cryptography.core)
            implementation(libs.cryptography.provider.base)
            implementation(libs.cryptography.provider.optimal)

            implementation(kotlincrypto.hash.md)
            implementation(kotlincrypto.hash.sha1)
            implementation(kotlincrypto.hash.sha2)
            implementation(kotlincrypto.hash.sha3)
            implementation(kotlincrypto.hash.blake2)
            implementation(kotlincrypto.macs.hmac.md)
            implementation(kotlincrypto.macs.hmac.sha1)
            implementation(kotlincrypto.macs.hmac.sha2)
            implementation(kotlincrypto.macs.hmac.sha3)

            implementation(kt.kotlinx.coroutine)

            implementation(libs.bignum)
        }

        webMain.dependencies {
            implementation(kotlinWrappers.web)
        }
    }
}
