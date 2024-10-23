package com.example.bookorder.config

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class KafkaPropertiesConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.kafka")
    fun kafkaProperties(): KafkaProperties {
        return KafkaProperties()
    }
}