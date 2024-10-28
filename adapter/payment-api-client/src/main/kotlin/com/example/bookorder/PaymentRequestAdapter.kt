package com.example.bookorder

import com.example.bookorder.payment.Payment
import com.example.bookorder.payment.PaymentId
import com.example.bookorder.payment.PaymentRequestPort
import com.example.bookorder.payment.PaymentStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PaymentRequestAdapter(val paymentApiClient: PaymentApiClient) : PaymentRequestPort {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun requestPayment(payment: Payment): PaymentStatus {
        logger.debug("{}.{} paymentId: {}", this::class.simpleName, this::requestPayment.name, payment.id)
        val paymentResponse = paymentApiClient.requestPayment(payment.id!!)
        logger.debug("result: {}", paymentResponse)
        return paymentResponse?.status ?: payment.status;
    }

    override fun getPaymentStatus(paymentId: PaymentId): PaymentStatus? {
        logger.debug("{}.{} paymentId: {}", this::class.simpleName, this::getPaymentStatus.name, paymentId)
        val paymentStatus = paymentApiClient.getPaymentStatus(paymentId)
        logger.debug("result: {}", paymentStatus)
        return paymentStatus?.status
    }

    override fun requestCancel(payment: Payment): PaymentStatus {
        logger.debug("{}.{} paymentId: {}", this::class.simpleName, this::requestCancel.name, payment.id)
        val paymentResponse = paymentApiClient.cancelPayment(payment.id!!)
        logger.debug("result: {}", paymentResponse)
        return paymentResponse?.status ?: payment.status;
    }


}