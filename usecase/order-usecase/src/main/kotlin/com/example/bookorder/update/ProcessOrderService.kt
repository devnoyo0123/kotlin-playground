package com.example.bookorder.update

import com.example.bookorder.core.exception.NotFoundException
import com.example.bookorder.order.Order
import com.example.bookorder.order.OrderPort
import com.example.bookorder.payment.PaymentEvent
import com.example.bookorder.payment.PaymentPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProcessOrderService(
    private val orderPort: OrderPort,
    private val paymentPort: PaymentPort,
    private val orderProcessors: List<OrderProcessor>
) : ProcessOrderUseCase {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun processOrder(paymentEvent: PaymentEvent): Order {
        logger.debug("Processing order for payment: ${paymentEvent.paymentId}")

        val payment = paymentPort.findById(paymentEvent.paymentId) ?: throw NotFoundException(NotFoundException.MESSAGE)
        val order = orderPort.findById(payment.orderId) ?: throw NotFoundException(NotFoundException.MESSAGE)

        // 중복 컨슘을 대비하기위해 멱등성 보장을 하도록 이미 처리된 주문인지 확인하는 로직이 필요합니다.
        if(checkIfOrderAlreadyProcessed(order)) {
            return order
        }

        val processor = orderProcessors.find { it.support(payment.status) }
            ?: throw IllegalStateException("No processor found for payment status: ${payment.status}")

        return processor.process(order, payment.status)
    }

    private fun checkIfOrderAlreadyProcessed(order: Order): Boolean {
        if (order.isCompleted()) {
            logger.info("Order ${order.getEntityIdOrThrow()} already completed")
            return true
        }
        if (order.isFailed()) {
            logger.info("Order ${order.getEntityIdOrThrow()} already failed")
            return true
        }

        return false
    }
}