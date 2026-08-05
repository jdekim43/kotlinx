plugins {
    id("convention.kotlin-multiplatform")
    id("convention.publish")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":kotlinx"))
            api(libs.bignum)
        }
    }
}
