package com.example.bookorder

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class WorkerApplication

fun main(args: Array<String>) {
    runApplication<WorkerApplication>(*args) {
        // web application type을 NONE으로 설정
        webApplicationType = WebApplicationType.NONE
    }
}