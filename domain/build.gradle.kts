import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
}

dependencies {
    implementation(project(":core"))  // domain 모듈에 대한 의존성

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