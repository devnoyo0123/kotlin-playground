plugins {
    kotlin("plugin.jpa") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management.get()
}

dependencies {


    implementation(project(":common:kafka"))
    implementation(project(":common:util"))
    implementation(project(":common:config"))
    implementation(project(":domain"))  // domain 모듈에 대한 의존성

    implementation(project(":usecase:payment-usecase"))
    implementation(project(":adapter:mysql"))
    implementation(project(":adapter:payment-api-client"))
    implementation(project(":adapter:kafka"))

    // Kotlin 기본 의존성
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")


    // Spring Boot 기본 (web 제외)
    implementation("org.springframework.boot:spring-boot-starter")

    // JSON 처리 (필요한 경우)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")  // Java 8 Date/Time 지원

    implementation("org.springframework.boot:spring-boot-starter-webflux:${libs.versions.spring.boot.get()}")
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.104.Final:osx-aarch_64")

    // 로깅
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // 모니터링 (선택사항)
     implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 테스트 관련
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")

}