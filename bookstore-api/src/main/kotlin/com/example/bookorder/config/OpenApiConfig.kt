package com.example.bookorder.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                .title("Book Order API")
                .version("1.0")
                .description("API for managing book orders")
//                .contact(Contact().name("API Support").email("support@example.com"))
//                .license(License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0"))
            )
            .servers(listOf(
                Server().url("http://localhost:8080").description("Local server"),
                Server().url("https://api.example.com").description("Production server")
            ))
    }
}