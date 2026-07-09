plugins {
    kotlin("jvm")
    application
}

application {
    mainClass = "org.mrlem.pandoc.sample.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mrlem.pandoc:pandoc-kotlin:0.2.0")
    implementation(libs.kotlinx.coroutines.core)
}