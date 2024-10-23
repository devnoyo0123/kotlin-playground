package com.example.bookorder

import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.slf4j.LoggerFactory

/**
 * 전역 예외 처리를 위한 추상 클래스입니다.
 * 전체적으로 동일한 포맷을 가지는 상황에서 쓸 목적으로 만들었습니다.
 */
open class AbstractGlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<RestApiResponse<Unit>> {

        logger.error("""
            Unhandled exception occurred:
            URI: ${request.getDescription(false)}
            Exception: ${ex.javaClass.simpleName}
            Message: ${ex.message}
            Stack trace:
        """.trimIndent(), ex)

        return RestApiResponse.fail(
            errorCode = "INTERNAL_SERVER_ERROR",
            message = ex.message ?: "An unexpected error occurred",
            status = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
