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
    implementation("org.mrlem.pandoc:pandoc-kt:0.1.0")
}