package com.example.bookorder

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object MySQLConnectorObjectMapper {
    val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addDeserializer(
            LocalDateTime::class.java,
            object : JsonDeserializer<LocalDateTime>() {
                override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
                    val timestamp = p.longValue
                    return LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(
                            timestamp / 1_000_000,
                            (timestamp % 1_000_000) * 1000
                        ),
                        ZoneId.of("Asia/Seoul")
                    )
                }
            }
        )
        registerModule(javaTimeModule)

        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
    }
}