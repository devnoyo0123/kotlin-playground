import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "2.0.20"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // 로깅을 위한 의존성
    implementation("io.github.microutils:kotlin-logging:${libs.versions.kotlinlogging.get()}")

    // 테스트를 위한 의존성
    testImplementation("io.mockk:mockk:${libs.versions.mockk.get()}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}