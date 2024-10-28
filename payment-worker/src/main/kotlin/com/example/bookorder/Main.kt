package com.example.bookorder

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext


@SpringBootApplication
class PaymentWorkerApplication

fun main(args: Array<String>) {
    val context: ApplicationContext = runApplication<PaymentWorkerApplication>(*args) {
        // web application type을 NONE으로 설정
        webApplicationType = WebApplicationType.NONE
    }

    // 등록된 빈 이름 출력
    val beanNames = context.beanDefinitionNames
    println("등록된 빈 목록:")
    beanNames.sorted().forEach { println(it) }
}