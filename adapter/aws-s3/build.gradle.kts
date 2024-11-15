plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}

dependencies {
    implementation(project(":usecase:core"))

    implementation("software.amazon.awssdk:s3:2.20.26")
    implementation("software.amazon.awssdk:sts:2.20.26")

    implementation("org.springframework.boot:spring-boot-starter")

}
