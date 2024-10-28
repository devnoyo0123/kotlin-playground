plugins {
    kotlin("plugin.jpa") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}

dependencies {

    implementation(project(":common:util"))

    // Kotlin 기본 의존성
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Spring Boot 기본 (web 제외)
    implementation("org.springframework.boot:spring-boot-starter")

    // JSON 처리 (필요한 경우)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")  // Java 8 Date/Time 지원
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

}