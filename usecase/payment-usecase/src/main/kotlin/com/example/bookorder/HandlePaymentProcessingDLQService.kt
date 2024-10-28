package com.example.bookorder

import com.example.bookorder.core.exception.NotFoundException
import com.example.bookorder.payment.Payment
import com.example.bookorder.payment.PaymentEvent
import com.example.bookorder.payment.PaymentPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class HandlePaymentProcessingDLQService
    (
    private val handleFailurePayment: HandleFailurePayment,
    private val paymentPort: PaymentPort

) : HandlePaymentProcessingDLQUseCase {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun cancelPayment(paymentEvent: PaymentEvent) {
        logger.debug("Cancelling payment by ${paymentEvent.paymentId}")

        paymentPort.findById(paymentEvent.paymentId)?.let {
            handleFailurePayment.cancelPayment(it)
        } ?: throw NotFoundException.forId(paymentEvent.paymentId, Payment::class::simpleName.toString())
    }
}