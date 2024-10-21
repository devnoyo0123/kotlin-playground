package com.example.bookorder.order


enum class OrderFailureReason(private val message: String) {
    MAXIMUM_RETRY_EXCEEDED("주문 재시도 제한을 초과했습니다."),
    INSUFFICIENT_STOCK("재고가 부족합니다."),
    DUPLICATE_ORDER("이미 처리중인 요청입니다."),
    PAYMENT_FAILED("결제에 실패했습니다."),
    INVALID_REQUEST("유효하지 않은 주문입니다.");

    fun formatMessage(vararg args: Any): String {
        return String.format(message, *args)
    }
}

enum class OrderSuccessReason(private val message: String) {
    ORDER_CREATED("주문이 성공적으로 접수되었습니다."),
    ALREADY_PAID("이미 처리된 주문입니다.");

    fun formatMessage(vararg args: Any): String {
        return String.format(message, *args)
    }
}