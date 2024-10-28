package com.example.bookorder

import com.example.bookorder.order.OrderEvent
import com.example.bookorder.payment.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 주문 이벤트를 받아 결제를 처리하는 서비스
 * 결제 처리 과정에서 예외가 발생하면 결제를 취소합니다.
 *
 * 결제 요청이 타임아웃되면 결제 상태를 조회하여 결제를 완료합니다.
 * 결제 상태가 실패이면 결제를 실패 처리합니다.
 * 결제가 완료되면 결제 이벤트를 저장합니다.
 * 결제가 실패하면 결제 이벤트를 저장합니다.
 * @exception PaymentRequestTimeoutException 결제 요청이 타임아웃되었을 때 발생하는 예외
 * @exception InValidPaymentStatusException 결제 상태가 유효하지 않을 때 발생하는 예외
 * @return Payment
 */
@Service
class ProcessPaymentService(
    private val paymentPort: PaymentPort,
    private val paymentRequestPort: PaymentRequestPort,
    private val handleUncertainPayment: HandleUncertainPayment,
    private val handleFailurePayment: HandleFailurePayment
) : ProcessPaymentUseCase {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun processPayment(orderEvent: OrderEvent): Payment {
        logger.debug("Processing payment for order: ${orderEvent.orderId}")

        // 중복 컨슘시에도 멱등성 보장을 위해 이미 결제된 주문이 있는지 확인합니다.
        paymentPort.findByOrderId(orderEvent.orderId)?.let {
            logger.info("Payment for order ${orderEvent.orderId} already exists")
            return it
        }

        val pendingPayment = Payment(
            orderId = orderEvent.orderId,
            status = PaymentStatus.PENDING
        )

        try {
            val paymentStatus = try {
                paymentRequestPort.requestPayment(pendingPayment)
            } catch (e: PaymentRequestTimeoutException) {
                // 결제 요청 타임아웃시 결제 재시도하지 않고
                // 결제 상태 조회를 재시도합니다.
                return handleUncertainPayment.retryPaymentStatus(pendingPayment)
            }

            return when (paymentStatus) {
                PaymentStatus.COMPLETED -> completePayment(pendingPayment)
                PaymentStatus.FAILED -> failPayment(pendingPayment)
                else -> throw InValidPaymentStatusException(InValidPaymentStatusException.MESSAGE)
            }
        } catch (e: Exception) {
            logger.error(" 예상치 못한 예외가 발생했습니다. $orderEvent", e)
            return handleFailurePayment.cancelPayment(pendingPayment)
        }
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