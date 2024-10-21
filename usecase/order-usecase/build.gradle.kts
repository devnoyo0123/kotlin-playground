import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":usecase:core"))

    implementation("org.springframework.boot:spring-boot-starter")

    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:${libs.versions.mysql.connector.java.get()}")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // 로깅을 위한 의존성
    implementation("io.github.microutils:kotlin-logging:2.1.23")

    // 테스트를 위한 의존성
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}