@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm")
    signing
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
}

signing {
    useGpgCmd()
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(
        groupId = "org.mrlem.pandoc",
        artifactId = "pandoc-kotlin",
        version = "0.3.0-SNAPSHOT",
    )

    pom {
        name = "Pandoc Kotlin library"
        description = "A Kotlin library for using Pandoc with a fluent, type-safe API"
        inceptionYear = "2026"
        url = "https://github.com/mrlem/pandoc-kotlin"

        licenses {
            license {
                name.set("GNU General Public License, version 3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                distribution.set("https://www.gnu.org/licenses/gpl-3.0.txt")
            }
        }
        developers {
            developer {
                id.set("mrlem")
                name.set("Sébastien Guillemin")
                url.set("https://github.com/mrlem")
            }
        }
        scm {
            url.set("https://github.com/mrlem/pandoc-kotlin")
            connection.set("scm:git:git://github.com/mrlem/pandoc-kotlin.git")
            developerConnection.set("scm:git:ssh://git@github.com/mrlem/pandoc-kotlin.git")
        }
        configureBasedOnAppliedPlugins()
    }

    repositories {
        mavenLocal()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xconsistent-data-class-copy-visibility"))
    }
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    }
}
