package com.example.bookorder.payment

import com.example.bookorder.order.OrderId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class PaymentAdapter(
    private val paymentRepository: PaymentRepository,
    private val paymentEventRepository: PaymentEventRepository
) : PaymentPort {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun save(payment: Payment): Payment {
        logger.debug("Saving payment: {}", payment)

        val savedPayment = paymentRepository.save(PaymentEntityConverter.toEntity(payment))
        return PaymentEntityConverter.toModel(savedPayment)
    }

    override fun save(paymentEvent: PaymentEvent): PaymentEvent {
        logger.debug("Saving payment event: {}", paymentEvent)
        val savedPaymentEvent = paymentEventRepository.save(PaymentEntityConverter.toPaymentEventEntity(paymentEvent))
        return PaymentEntityConverter.toPaymentEventModel(savedPaymentEvent)
    }

    override fun findById(paymentId: PaymentId): Payment? {
        return paymentRepository.findByIdOrNull(paymentId.value)?.let {
            PaymentEntityConverter.toModel(it)
        }
    }

    override fun findByOrderId(orderId: OrderId): Payment? {
        return paymentRepository.findByOrderId(orderId.value)?.let {
            PaymentEntityConverter.toModel(it)
        }
    }
}

object PaymentEntityConverter {
    fun toEntity(payment: Payment): PaymentEntity {
        return PaymentEntity(
            id = payment.getEntityIdOrNull()?.value,
            orderId = payment.orderId.value,
            status = payment.status,
            createdAt = payment.createdAt,
            updatedAt = payment.updatedAt,
            deletedAt = payment.deletedAt
        )
    }

    fun toModel(entity: PaymentEntity): Payment {
        return Payment(
            id = PaymentId.of(entity.id),
            orderId = OrderId.of(entity.orderId),
            status = entity.status,
        ).apply {
            createdAt = entity.createdAt
            updatedAt = entity.updatedAt
            deletedAt = entity.deletedAt
        }
    }

    fun toPaymentEventEntity(paymentEvent: PaymentEvent): PaymentEventEntity {
        return PaymentEventEntity(
            id = paymentEvent.getEntityIdOrNull()?.value,
            paymentId = paymentEvent.paymentId.value,
            status = paymentEvent.status,
            createdAt = paymentEvent.createdAt,
            updatedAt = paymentEvent.updatedAt,
            deletedAt = paymentEvent.deletedAt
        )
    }

    fun toPaymentEventModel(paymentEventEntity: PaymentEventEntity): PaymentEvent {
        return PaymentEvent(
            id = PaymentEventId.of(paymentEventEntity.id),
            paymentId = PaymentId.of(paymentEventEntity.paymentId),
            status = paymentEventEntity.status,
        ).apply {
            createdAt = paymentEventEntity.createdAt
            updatedAt = paymentEventEntity.updatedAt
            deletedAt = paymentEventEntity.deletedAt
        }
    }
}