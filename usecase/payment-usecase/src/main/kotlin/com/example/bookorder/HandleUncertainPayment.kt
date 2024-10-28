package com.example.bookorder

import com.example.bookorder.payment.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 결제요청에 대한 응답이 불명확할때 처리하는 API 상태 조회를 재시도하면서
 * 최대 재시도 횟수를 초과하면 복구 메서드를 호출합니다.
 *
 * @exception InValidPaymentStatusException 결제 상태가 유효하지 않을 때 발생하는 예외
 * @return Payment
 */
@Component
class HandleUncertainPayment(
    private val paymentPort: PaymentPort,
    private val paymentRequestPort: PaymentRequestPort
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Retryable(
        value = [InValidPaymentStatusException::class, PaymentRequestTimeoutException::class],
        maxAttempts = 6,
        backoff = Backoff(delay = 1000, multiplier = 2.0, maxDelay = 30000),
        recover = "handleMaxRetryAttempts" // 새로운 복구 메서드 지정
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // 새로운 트랜잭션 시작
    fun retryPaymentStatus(payment: Payment): Payment {
        logger.debug("Retrying payment status for payment: ${payment.getEntityIdOrThrow()}")

        val paymentStatus = paymentRequestPort.getPaymentStatus(payment.getEntityIdOrThrow())
        logger.debug("Payment status: {}", paymentStatus)
        return when (paymentStatus) {
            PaymentStatus.COMPLETED -> completePayment(payment)
            PaymentStatus.FAILED -> failPayment(payment)
            else -> throw InValidPaymentStatusException(InValidPaymentStatusException.MESSAGE)
        }
    }

    // 최대 재시도 횟수 초과 시 호출되는 복구 메서드
    @Recover
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleMaxRetryAttempts(e: Throwable, payment: Payment): Payment {
        logger.error("Max retry attempts exceeded for payment: ${payment.getEntityIdOrThrow()}", e)
        // 여기서 필요한 추가 로직을 수행할 수 있습니다.
        // 예를 들어, 알림을 보내거나 특별한 처리를 할 수 있습니다.
        return failPayment(payment)
    }

    private fun completePayment(payment: Payment): Payment {
        payment.markCompleted()
        val savedPayment = savePayment(payment)
        return savedPayment
    }

    private fun failPayment(payment: Payment): Payment {
        payment.markFailed()
        val savedPayment = savePayment(payment)
        return savedPayment
    }

    private fun savePayment(payment: Payment): Payment {
        val savedPayment = paymentPort.save(payment)
        paymentPort.save(PaymentEvent(paymentId = savedPayment.getEntityIdOrThrow(), status = savedPayment.status))
        return savedPayment
    }
}