package com.example.bookorder

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * API 응답 객체
 * 정상적인 응답과 에러 응답의 형태를 맞추기 위해 사용합니다.
 */
class RestApiResponse<T>(
    val meta: Meta,
    val data: T?
) {
    enum class Status {
        SUCCESS, FAIL;

        fun getValue(): String = name
    }

    data class Meta(
        val status: Status,
        val message: String? = null,
        val errorCode: String? = null,
        val validations: List<ValidationError>? = null
    )

    data class ValidationError(
        val fieldName: String,
        val message: String
    )

    companion object {
        fun <T> success(data: T, status: HttpStatus): ResponseEntity<RestApiResponse<T>> {
            val meta = Meta(Status.SUCCESS)
            return ResponseEntity(RestApiResponse(meta, data), status)
        }

        fun success(message: String, status: HttpStatus): ResponseEntity<RestApiResponse<Unit>> {
            val meta = Meta(Status.SUCCESS, message)
            return ResponseEntity(RestApiResponse(meta, null), status)
        }

        fun <T> fail(errorCode: String, message: String, status: HttpStatus): ResponseEntity<RestApiResponse<T>> {
            val meta = Meta(Status.FAIL, message, errorCode)
            return ResponseEntity(RestApiResponse<T>(meta, null), status)
        }

        fun <T> fail(errorCode: String, message: String, validations: List<ValidationError>, status: HttpStatus): ResponseEntity<RestApiResponse<T>> {
            val meta = Meta(Status.FAIL, message, errorCode, validations)
            return ResponseEntity(RestApiResponse<T>(meta, null), status)
        }
    }
}