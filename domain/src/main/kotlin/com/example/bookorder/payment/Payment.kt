package com.example.bookorder.payment

import com.example.bookorder.core.entity.Audit
import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import com.example.bookorder.order.OrderId

enum class PaymentStatus {
    PENDING,     // 결제 대기 중
    COMPLETED,   // 결제 완료
    FAILED,      // 결제 실패
}

@JvmInline
value class PaymentId(override val value: Long) : EntityId<Long> {
    companion object {
        fun of(id: Long?): PaymentId = id?.let { PaymentId(it) }
            ?: throw IllegalArgumentException("${PaymentId::class.simpleName} cannot be null")
    }
}

data class Payment(
    override val id: PaymentId? = null,
    val orderId: OrderId,
    var status: PaymentStatus = PaymentStatus.PENDING,
) : Audit(), BaseEntity<PaymentId> {
    fun markCompleted() {
        if (status == PaymentStatus.COMPLETED) {
            throw IllegalStateException("Payment already completed")
        }
        status = PaymentStatus.COMPLETED
    }

    fun markFailed() {
        status = PaymentStatus.FAILED
    }
}