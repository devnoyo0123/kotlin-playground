package com.example.bookorder

import com.example.bookorder.book.exception.InsufficientStockException
import com.example.bookorder.create.exception.DuplicateOrderException
import com.example.bookorder.create.exception.OrderMaximumRetryException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class BookStoreApiExceptionHandler: AbstractGlobalExceptionHandler() {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(OrderMaximumRetryException::class)
    fun handleMaximumRetryException(
        ex: OrderMaximumRetryException,
        request: WebRequest
    ): ResponseEntity<RestApiResponse<Unit>> {
        logger.error("Maximum retry attempts exceeded: ${ex.message}", ex)

        return RestApiResponse.fail(
            errorCode = "MAXIMUM_RETRY_EXCEEDED",
            message = ex.message ?: "Maximum retry attempts exceeded",
            status = HttpStatus.TOO_MANY_REQUESTS
        )
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStockException(
        ex: InsufficientStockException,
        request: WebRequest
    ): ResponseEntity<RestApiResponse<Unit>> {
        logger.warn("Insufficient stock error: ${ex.message}")  // warn 레벨 사용 (비즈니스 로직 예외)

        return RestApiResponse.fail(
            errorCode = "INSUFFICIENT_STOCK",
            message = ex.message ?: "Insufficient stock",
            status = HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(DuplicateOrderException::class)
    fun handleDuplicateOrderException(
        ex: DuplicateOrderException,
        request: WebRequest
    ): ResponseEntity<RestApiResponse<Unit>> {
        logger.warn("Duplicate order detected: ${ex.message}")  // warn 레벨 사용 (비즈니스 로직 예외)

        return RestApiResponse.fail(
            errorCode = "DUPLICATE_ORDER",
            message = ex.message ?: "Duplicate order detected",
            status = HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<RestApiResponse<Unit>> {

        logger.warn("""
            Invalid request detected:
            URI: ${request.getDescription(false)}
            Errors: ${ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }}
        """.trimIndent())

        return RestApiResponse.fail(
            errorCode = "INVALID_REQUEST",
            message = "요청값이 올바르지 않습니다.",
            validations = ex.bindingResult.fieldErrors.map {
                RestApiResponse.ValidationError(it.field, it.defaultMessage ?: "Invalid value")
            },
            status = HttpStatus.BAD_REQUEST
        )
    }
}