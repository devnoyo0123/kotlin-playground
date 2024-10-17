import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.example.bookorder"
version = "1.0-SNAPSHOT"


kotlin {
    jvmToolchain(21)
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
        }
    }
}