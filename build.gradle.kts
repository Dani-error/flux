import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.lang.Integer.parseInt

plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.dokka") version "2.0.0"
    java
    id("com.vanniktech.maven.publish") version "0.33.0"
}

defaultTasks("clean", "build")

group = project.property("GROUP")!!.toString()
version = project.property("VERSION_NAME")!!.toString()

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin test API + JUnit 5 support
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // JUnit Jupiter engine (for both Java & Kotlin)
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.3")
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

val javaVersion = project.property("JAVA_VERSION")!!.toString()

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(javaVersion))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(parseInt(javaVersion))
}

tasks.withType<Jar> {
    from(rootProject.file("LICENSE"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

extensions.configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()

    publishing {
        repositories {
            maven {
                name = "githubPackages"
                url = uri("https://maven.pkg.github.com/Dani-error/flux")
                credentials(PasswordCredentials::class)
            }
        }
    }

    pom {
        name = "Flux"
        description = project.property("DESCRIPTION")!!.toString()
        inceptionYear = project.property("INCEPTION_YEAR")!!.toString()
        url = project.property("PROJECT_URL")!!.toString()
        licenses {
            license {
                name = project.property("LICENSE_NAME")!!.toString()
                url = project.property("LICENSE_URL")!!.toString()
            }
        }
        ciManagement {
            system.set(project.property("CI_SYSTEM")!!.toString())
            url.set(project.property("CI_URL")!!.toString())
        }
        developers {
            developer {
                id = project.property("DEVELOPER_ID")!!.toString()
                name = project.property("DEVELOPER_NAME")!!.toString()
                url = project.property("DEVELOPER_URL")!!.toString()
            }
        }
        scm {
            val repoUrl = project.property("PROJECT_URL")!!.toString()
            url = repoUrl
            connection = "scm:git:git://github.com/Dani-error/flux.git"
            developerConnection = "scm:git:ssh://git@github.com/Dani-error/flux.git"
        }
    }
}