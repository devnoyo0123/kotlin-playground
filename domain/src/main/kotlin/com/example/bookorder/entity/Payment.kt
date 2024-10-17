package com.example.bookorder.entity

import com.example.bookorder.core.BaseEntity
import com.example.bookorder.core.EntityId
import java.math.BigDecimal

enum class PaymentStatus {
    PENDING,     // 결제 대기 중
    COMPLETED,   // 결제 완료
    FAILED,      // 결제 실패
    UNCERTAIN    // 결제 상태 불확실 (타임아웃 등으로 인해 상태 확인 필요)
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
    val orderId: Long,
    val amount: BigDecimal,
    var status: PaymentStatus,
    val paymentMethod: String,
    var externalTransactionId: String? = null
) : Audit(), BaseEntity<PaymentId> {
    fun markCompleted(externalTransactionId: String) {
        if (status == PaymentStatus.COMPLETED) {
            throw IllegalStateException("Payment already completed")
        }
        this.externalTransactionId = externalTransactionId
        status = PaymentStatus.COMPLETED
    }

    fun markFailed() {
        status = PaymentStatus.FAILED
    }

    fun markUncertain() {
        status = PaymentStatus.UNCERTAIN
    }
}