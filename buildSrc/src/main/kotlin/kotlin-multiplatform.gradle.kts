package convention

import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("io.kotest")
}

val libraries = extensions.getByType<VersionCatalogsExtension>().named("libs")
val javaToolchains = extensions.getByType<JavaToolchainService>()
// bignum 0.3.10 is built for Java 17; CI already provisions Java 21.
val testJavaLauncher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(11)

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    //web
    js {
        browser()
        nodejs()

        compilerOptions {
            target = "es2015"
        }
    }

    //apple
    macosArm64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    watchosArm64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosSimulatorArm64()

    //desktop
    linuxX64()
    linuxArm64()
    mingwX64()
    macosArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(libraries.findLibrary("kotest-assertions-core").get())
            implementation(libraries.findLibrary("kotest-framework-engine").get())
        }

        jvmTest.dependencies {
            runtimeOnly(libraries.findLibrary("kotest-runner-junit5").get())
        }
    }
}

tasks.withType<Test>().configureEach {
    javaLauncher.set(testJavaLauncher)
    useJUnitPlatform()
}
