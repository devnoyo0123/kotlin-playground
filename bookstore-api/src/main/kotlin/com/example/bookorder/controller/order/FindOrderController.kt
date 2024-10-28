package com.example.bookorder.controller.order

import com.example.bookorder.RestApiResponse
import com.example.bookorder.controller.order.dto.OrderItemDto
import com.example.bookorder.order.OrderId
import com.example.bookorder.order.OrderStatus
import com.example.bookorder.read.FindOrderByIdResponse
import com.example.bookorder.read.FindOrderByIdUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController()
class FindOrderController(
    val findOrderByIdUseCase: FindOrderByIdUseCase
) {
    @GetMapping("/v1/order/{orderId}")
    fun findOrderById(@PathVariable orderId: Long) : ResponseEntity<RestApiResponse<FindOrderById.Response>> {
        val findOrderByIdResponse: FindOrderByIdResponse = findOrderByIdUseCase.execute(OrderId.of(orderId))
        return RestApiResponse.success(
            data = findOrderByIdResponse.toPayload(),
            message = findOrderByIdResponse.message,
            HttpStatus.OK
        )
    }
}

class FindOrderById {
    data class Response(
        val orderId: Long,
        val totalAmount: BigDecimal,
        val status: OrderStatus,
        val orderItems: List<OrderItemDto>
    )
}

fun FindOrderByIdResponse.toPayload(): FindOrderById.Response {
    return FindOrderById.Response(
        orderId = this.orderId,
        totalAmount = this.totalAmount,
        status = this.status,
        orderItems = this.orderItems.map {
            OrderItemDto(
                id = it.getEntityIdOrThrow().value,
                bookId = it.bookId.value,
                quantity = it.quantity,
                price = it.price,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    )
}