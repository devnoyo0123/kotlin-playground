package com.example.bookorder

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.util.ContentCachingRequestWrapper
import java.nio.charset.StandardCharsets

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
        val httpRequest = (request as ServletWebRequest).request

        val requestBody = getRequestBody(httpRequest)

        logger.error("""
            Unhandled exception occurred:
            URI: ${request.getDescription(false)}
            Exception: ${ex.javaClass.simpleName}
            Message: ${ex.message}
            Method: ${httpRequest.method}
                    URL: ${httpRequest.requestURL}
                    Headers: ${getHeadersAsString(httpRequest)}
                    Body: $requestBody
            Stack trace:
        """.trimIndent(), ex)

        return RestApiResponse.fail(
            errorCode = "INTERNAL_SERVER_ERROR",
            message = ex.message ?: "An unexpected error occurred",
            status = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    private fun getRequestBody(request: HttpServletRequest): String {
        return if (request is ContentCachingRequestWrapper) {
            val requestBody = String(request.contentAsByteArray, StandardCharsets.UTF_8)
            if (requestBody.isEmpty()) "Empty body" else requestBody
        } else {
            "Unable to read request body"
        }
    }

    private fun getHeadersAsString(request: HttpServletRequest): String {
        return request.headerNames.toList()
            .joinToString(", ") { "$it: ${request.getHeader(it)}" }
    }
}
