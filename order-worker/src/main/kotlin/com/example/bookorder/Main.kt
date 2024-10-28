package com.example.bookorder

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrderWorkerApplication

fun main(args: Array<String>) {
    runApplication<OrderWorkerApplication>(*args) {
        webApplicationType = WebApplicationType.NONE
    }
}

