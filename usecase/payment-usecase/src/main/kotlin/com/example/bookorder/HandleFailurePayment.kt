package com.example.bookorder

import com.example.bookorder.payment.Payment
import com.example.bookorder.payment.PaymentEvent
import com.example.bookorder.payment.PaymentPort
import com.example.bookorder.payment.PaymentRequestPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class HandleFailurePayment(
    private val paymentPort: PaymentPort,
    private val paymentRequestPort: PaymentRequestPort
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun cancelPayment(payment: Payment): Payment {
        paymentRequestPort.requestCancel(payment)
        return failPayment(payment)
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