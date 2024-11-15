plugins {
    kotlin("plugin.jpa") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}

dependencies {

    implementation(project(":adapter:aws-s3"))
    implementation(project(":adapter:mysql"))
    implementation(project(":common:rest"))
    implementation(project(":common:config"))

    implementation(project(":domain"))
    implementation(project(":usecase:core"))
    implementation(project(":usecase:order-usecase"))
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    testImplementation(kotlin("test"))
    // 테스트를 위한 의존성
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.testcontainers:testcontainers:${libs.versions.testcontainers.get()}")
}

tasks.withType<Test> {
    // Integration Test 에서 사용하는 환경변수 설정
    systemProperty("project.root", rootProject.projectDir.absolutePath)
}