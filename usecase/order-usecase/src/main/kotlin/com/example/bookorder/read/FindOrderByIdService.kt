package com.example.bookorder.read

import com.example.bookorder.order.OrderId
import com.example.bookorder.order.OrderPort
import com.example.bookorder.read.exception.OrderNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FindOrderByIdService(private val orderPort: OrderPort) : FindOrderByIdUseCase {
    /**
     * @description
     * 주문 ID로 주문을 조회합니다.
     */
    @Transactional(readOnly = true)
    override fun execute(orderId: OrderId): FindOrderByIdResponse {
        val order = orderPort.findById(orderId) ?: throw OrderNotFoundException.forOrderId(orderId)
        return FindOrderByIdResponse(
            orderId = order.getId().value,
            status = order.status,
        )
    }
}