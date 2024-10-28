package com.example.bookorder.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class PaymentApiClientConfig {

    @Value("\${payment.api.base-url}")
    lateinit var paymentApiBaseUrl: String

    @Bean
    @Qualifier("paymentHttpClient")
    fun paymentHttpClient(): WebClient {
        return WebClient.builder()
            .baseUrl(paymentApiBaseUrl)
            .build()
    }
}