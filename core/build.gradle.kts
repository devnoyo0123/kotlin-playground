import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "2.0.20"
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // 로깅을 위한 의존성
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    // 테스트를 위한 의존성
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}