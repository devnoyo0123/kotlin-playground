import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "2.0.20"
}

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.redisson:redisson-spring-boot-starter:${libs.versions.redisson.get()}")
    implementation("org.springframework.boot:spring-boot-starter-aop")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}