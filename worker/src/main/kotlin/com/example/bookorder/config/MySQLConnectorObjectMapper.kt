package com.example.bookorder.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Configuration
class JacksonConfig {

    @Bean
    @Qualifier("mysqlConnectorObjectMapper")
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().apply {
            // snake_case를 camelCase로 변환
            propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE

            // JSON property 중 클래스에 없는 필드는 무시
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            // timestamp를 LocalDateTime으로 변환하기 위한 모듈 등록
            // JavaTimeModule 설정 수정
            val javaTimeModule = JavaTimeModule()
            javaTimeModule.addDeserializer(
                LocalDateTime::class.java,
                object : JsonDeserializer<LocalDateTime>() {
                    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
                        val timestamp = p.longValue
                        return LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(
                                timestamp / 1_000_000,  // 초 단위
                                (timestamp % 1_000_000) * 1000 // 나노초 단위로 변환
                            ),
                            ZoneId.of("Asia/Seoul")
                        )
                    }
                }
            )
            registerModule(javaTimeModule)

            // timestamp를 숫자로 직렬화/역직렬화
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        }
    }
}