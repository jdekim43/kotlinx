import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.dokka.javadoc) apply false
    alias(libs.plugins.jreleaser)

//    id("maven-publish")
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.multiplatform")
        plugin("org.jetbrains.dokka")
        plugin("org.jetbrains.dokka-javadoc")
        plugin("maven-publish")
    }

    group = "kr.jadekim"
    version = "3.0.0-beta1"

    repositories {
        mavenCentral()
    }

    configure<KotlinMultiplatformExtension> {
        jvmToolchain(8)

        jvm {
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }

        js {
            browser()
            nodejs()
        }

        iosArm64()
        iosSimulatorArm64()

        @Suppress("UNUSED_VARIABLE")
        sourceSets {
            commonTest.dependencies {
                implementation(kotlin("test"))
            }
            iosMain.dependencies {

            }
            jvmTest.dependencies {
                val junitVersion: String by project

                implementation(kotlin("test-junit5"))

                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
                compileOnly("org.junit.jupiter:junit-jupiter-api:$junitVersion")
                compileOnly("org.junit.jupiter:junit-jupiter-params:$junitVersion")
            }
        }
    }

    val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        dependsOn(tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml"))
        archiveClassifier.set("javadoc")
        from(tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml").flatMap { it.outputDirectory })
    }

    configure<PublishingExtension> {
        publications.withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set(project.name)
                description.set("Kotlin Commons")
                url.set("https://github.com/jdekim43/kotlin-common")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("jdekim43")
                        name.set("Jade Kim")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/jdekim43/kotlin-common.git")
                    developerConnection.set("scm:git:git://github.com/jdekim43/kotlin-common.git")
                    url.set("https://github.com/jdekim43/kotlin-common")
                }
            }
        }

        repositories {
            maven {
                setUrl(layout.buildDirectory.dir("staging-deploy"))
            }
        }
    }
}

jreleaser {
    project {
        author("Jade Kim")
        license.set("Apache-2.0")
        links {
            vcsBrowser.set("https://github.com/jdekim43/kotlin-common")
        }
        inceptionYear.set("2021")
    }

    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
        mode.set(Signing.Mode.FILE)
    }

    deploy {
        maven {
            mavenCentral {
                create("release") {
                    active.set(Active.RELEASE)
                    url.set("https://central.sonatype.com/api/v1/publisher")

                    subprojects.forEach {
                        stagingRepository(it.layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath)
                    }
                }
            }
            nexus2 {
                create("snapshot") {
                    active.set(Active.SNAPSHOT)
                    url.set("https://central.sonatype.com/repository/maven-snapshots")
                    snapshotUrl.set("https://central.sonatype.com/repository/maven-snapshots")
                    applyMavenCentralRules.set(true)
                    snapshotSupported.set(true)
                    closeRepository.set(true)
                    releaseRepository.set(true)

                    subprojects.forEach {
                        stagingRepository(it.layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath)
                    }
                }
            }
        }
    }

    release {
        github {
            repoOwner = "jdekim43"
        }
    }
}

tasks.register("publish") {
    group = "publishing"

    subprojects.forEach {
        dependsOn("${it.name}:publish")
    }

    finalizedBy(":jreleaserFullRelease")
}
