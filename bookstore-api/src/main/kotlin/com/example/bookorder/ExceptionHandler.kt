package com.example.bookorder

import com.example.bookorder.book.exception.InsufficientStockException
import com.example.bookorder.create.exception.DuplicateOrderException
import com.example.bookorder.create.exception.MaximumRetryException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.method.MethodValidationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class BookStoreApiExceptionHandler: AbstractGlobalExceptionHandler() {

    @ExceptionHandler(MaximumRetryException::class)
    fun handleMaximumRetryException(
        ex: MaximumRetryException,
        request: WebRequest
    ): ResponseEntity<RestApiResponse<Unit>> {
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