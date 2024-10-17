package com.example.bookorder.payment

import com.example.bookorder.core.entity.BaseEntity
import com.example.bookorder.core.entity.EntityId
import com.example.bookorder.core.entity.Audit
import java.time.LocalDateTime

enum class PaymentAttemptStatus {
    SUCCESS,    // 결제 시도 성공
    FAILURE,    // 결제 시도 실패
    TIMEOUT     // 결제 시도 타임아웃
}

@JvmInline
value class PaymentAttemptId(override val value: Long) : EntityId<Long> {
    companion object {
        fun of(id: Long?): PaymentAttemptId = id?.let { PaymentAttemptId(it) }
            ?: throw IllegalArgumentException("${PaymentAttemptId::class.simpleName} cannot be null")
    }
}

data class PaymentAttempt(
    override val id: PaymentAttemptId? = null,
    val paymentId: Long,
    var status: PaymentAttemptStatus,
    val errorMessage: String? = null,
    val attemptTime: LocalDateTime = LocalDateTime.now()
) : Audit(), BaseEntity<PaymentAttemptId> {

}
