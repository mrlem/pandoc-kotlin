plugins {
    alias(libs.plugins.kotlin.jvm) apply true
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
