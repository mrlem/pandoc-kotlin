plugins {
    kotlin("jvm")
    application
}

application {
    mainClass = "org.mrlem.pandoc.sample.MainKt"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":library"))
    implementation(libs.kotlinx.coroutines.core)
}