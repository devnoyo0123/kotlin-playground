package com.example.bookorder.payment

import com.example.bookorder.core.entity.Audit
import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId


@JvmInline
value class PaymentEventId(override val value: Long) : EntityId<Long> {
    companion object {
        fun of(id: Long?): PaymentEventId = id?.let { PaymentEventId(it) }
            ?: throw IllegalArgumentException("${PaymentEventId::class.simpleName} cannot be null")
    }
}

data class PaymentEvent(
    override val id: PaymentEventId? = null,
    val paymentId: PaymentId,
    val status: PaymentStatus,
): Audit(), BaseEntity<PaymentEventId>