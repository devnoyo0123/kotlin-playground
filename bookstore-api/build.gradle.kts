plugins {
    kotlin("plugin.jpa") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}

dependencies {

    implementation(project(":adapter:mysql"))
    implementation(project(":adapter:redis"))
    implementation(project(":core:rest"))
    implementation(project(":domain"))
    implementation(project(":usecase:core"))
    implementation(project(":usecase:order-usecase"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    testImplementation(kotlin("test"))
}
