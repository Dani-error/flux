plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jetbrains.dokka") version "2.0.0"
    java
}

group = "io.github.dani-error"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin test API + JUnit 5 support
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // JUnit Jupiter engine (for both Java & Kotlin)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("documentation"))
    }
}

tasks.build {
    dependsOn(tasks.dokkaGenerate)
}