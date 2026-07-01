plugins {
    kotlin("jvm") version "2.0.21"
    `maven-publish`
}

group = "org.mrlem"
version = "0.1.0-SNAPSHOT"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.mrlem"
            artifactId = "pandoc-kt"
            version = project.version as String
            
            from(components["java"])
            
            pom {
                name.set("pandoc-kt")
                description.set("A Kotlin library for using Pandoc with a fluent, type-safe API")
                url.set("https://github.com/mrlem/pandoc-kt")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("mrlem")
                        name.set("Mr Lem")
                    }
                }
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xconsistent-data-class-copy-visibility"))
    }
}

tasks.test {
    useJUnitPlatform()
}

// Use Java 21 for compatibility
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
