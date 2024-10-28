package com.example.bookorder.order

enum class OrderSuccessReason(private val message: String) {
    ORDER_CREATED("주문이 성공적으로 접수되었습니다."),
    ALREADY_PAID("이미 처리된 주문입니다.");

    fun formatMessage(vararg args: Any): String {
        return String.format(message, *args)
    }
}