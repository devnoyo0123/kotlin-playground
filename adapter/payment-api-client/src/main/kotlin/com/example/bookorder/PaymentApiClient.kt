package com.example.bookorder

import com.example.bookorder.payment.PaymentId
import com.example.bookorder.payment.PaymentRequestTimeoutException
import com.example.bookorder.payment.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class PaymentApiClient(@Qualifier("paymentHttpClient") private val paymentApiClient: WebClient) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun requestPayment(paymentId: PaymentId): PaymentResponse? {
        logger.debug("{}.{} for paymentId: {}", this::class::simpleName, this::requestPayment::name, paymentId)

        val payload = makeTestPaymentRequest(paymentId)
        logger.debug("{}.{} payment with payload: {}", this::class::simpleName, this::requestPayment::name, payload)

        return paymentApiClient.post()
            .uri("payments/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(PaymentResponse::class.java)
            .timeout(Duration.ofSeconds(5))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> PaymentRequestTimeoutException("결제 요청 시간 초과", throwable)
                    else -> throwable
                }
            }
            .block()
    }


    private fun makeTestPaymentRequest(paymentId: PaymentId): PaymentRequest {
        return when (paymentId.value.toInt() % 3) {
            0 -> PaymentRequest(paymentKey = paymentId.value.toString()) // 정상 케이스
            1 -> PaymentRequest(paymentKey = paymentId.value.toString(), fail = true) // 실패 케이스
            2 -> PaymentRequest(paymentKey = paymentId.value.toString(), ex = true, delay = 20) // 예외 케이스
            else -> throw IllegalStateException("Unexpected remainder") // 사실상 발생할 수 없는 케이스
        }
    }

    fun getPaymentStatus(paymentId: PaymentId): PaymentResponse? {
        return paymentApiClient.get()
            .uri("/payments/{paymentKey}", paymentId.value)
            .retrieve()
            .bodyToMono(PaymentResponse::class.java)
            .timeout(Duration.ofSeconds(5))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> PaymentRequestTimeoutException("결제 요청 시간 초과", throwable)
                    else -> throwable
                }
            }
            .block()
    }

    fun cancelPayment(paymentId: PaymentId): PaymentResponse? {
        logger.debug("PaymentApiClient.Cancelling payment for paymentId: $paymentId")

        return paymentApiClient.post()
            .uri("payments/cancellation")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(PaymentRequest(paymentKey = paymentId.value.toString()))
            .retrieve()
            .bodyToMono(PaymentResponse::class.java)
            .timeout(Duration.ofSeconds(5))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> PaymentRequestTimeoutException("결제 요청 시간 초과", throwable)
                    else -> throwable
                }
            }
            .block()
    }
}

data class PaymentResponse(
    val status: PaymentStatus
)

data class PaymentRequest(
    val ex: Boolean = false,
    val paymentKey: String,
    val delay: Long = 0, // seconds
    val fail: Boolean = false
)