import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}

dependencies {
    implementation(project(":usecase:core"))

    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.redisson:redisson-spring-boot-starter:${libs.versions.redisson.get()}")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}