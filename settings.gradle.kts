rootProject.name = "kotlin-common"

include(
    "common-util",
    "common-exception",
    "common-encoder",
    "common-crypto"
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("kotlinWrappers") {
            from("org.jetbrains.kotlin-wrappers:kotlin-wrappers-catalog:2025.11.12")
        }

        create("kotlincrypto") {
            from("org.kotlincrypto:version-catalog:0.8.0")
        }
    }
}