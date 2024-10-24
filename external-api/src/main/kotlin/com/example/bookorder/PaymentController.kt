package com.example.bookorder

import jakarta.annotation.PreDestroy
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

    @PostMapping
    fun processPayment(@RequestBody request: PaymentRequest): CompletableFuture<ResponseEntity<PaymentResponse>> {
        // 초기 상태 저장
        paymentStatus[request.paymentKey] = "PENDING"

        val future = CompletableFuture<ResponseEntity<PaymentResponse>>()

        if (request.ex) {
            // 60초 후에 상태를 업데이트하고 응답을 반환
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

    // 리소스 정리를 위한 소멸자
    @PreDestroy
    fun cleanup() {
        executorService.shutdown()
    }
}