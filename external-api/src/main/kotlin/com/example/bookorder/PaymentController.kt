package com.example.bookorder

import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.*

data class PaymentRequest(
    val ex: Boolean,
    val paymentKey: String,
    val delay: Long,
    val fail: Boolean
)

data class PaymentResponse(
    val status: String
)

@RestController
@RequestMapping("/api/payments")
class PaymentController {
    private val paymentStatus = ConcurrentHashMap<String, String>()
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/confirm")
    fun processPayment(@RequestBody request: PaymentRequest): CompletableFuture<ResponseEntity<PaymentResponse>> {
        logger.info("Processing payment request: $request")
        // 초기 상태 저장
        paymentStatus[request.paymentKey] = "PENDING"

        val future = CompletableFuture<ResponseEntity<PaymentResponse>>()

        if (request.ex) {
            executorService.schedule({
                paymentStatus[request.paymentKey] = "COMPLETED"
                future.complete(
                    ResponseEntity.ok(
                        PaymentResponse(status = "COMPLETED")
                    )
                )
            }, request.delay, TimeUnit.SECONDS)
        } else if(request.fail) {
            // 즉시 상태 업데이트하고 응답
            paymentStatus[request.paymentKey] = "FAILED"
            future.complete(
                ResponseEntity.ok(
                    PaymentResponse(status = "FAILED")
                )
            )
        } else {
            // 즉시 상태 업데이트하고 응답
            paymentStatus[request.paymentKey] = "COMPLETED"
            future.complete(
                ResponseEntity.ok(
                    PaymentResponse(status = "COMPLETED")
                )
            )
        }

        return future
    }

    @GetMapping("/{paymentKey}")
    fun getPaymentStatus(@PathVariable paymentKey: String): ResponseEntity<PaymentResponse> {
        val status = paymentStatus[paymentKey] ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(PaymentResponse(status = status))
    }

    @PostMapping("/cancellation")
    fun cancelPayment(@RequestBody request: PaymentRequest): ResponseEntity<PaymentResponse> {
        logger.info("Cancelling payment request: $request")
        // 초기 상태 저장

        paymentStatus[request.paymentKey] = "CANCELLED"

        return ResponseEntity.ok(
            PaymentResponse(status = "CANCELLED")
        )
    }

    // 리소스 정리를 위한 소멸자
    @PreDestroy
    fun cleanup() {
        executorService.shutdown()
    }
}