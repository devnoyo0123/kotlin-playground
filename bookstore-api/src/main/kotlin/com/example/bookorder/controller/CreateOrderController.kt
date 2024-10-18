package com.example.bookorder.controller

import com.example.bookorder.RestApiResponse
import com.example.bookorder.create.CreateOrderRequest
import com.example.bookorder.create.CreateOrderResponse
import com.example.bookorder.create.CreateOrderUseCase
import com.example.bookorder.create.OrderItemRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController("/v1/order")
class CreateOrderController(
    val createOrderService: CreateOrderUseCase
) {

    @PostMapping()
    fun createOrder(
        @Valid
        @RequestBody request: CreateOrder.Request
    ): ResponseEntity<RestApiResponse<CreateOrder.Response>> {
        val useCaseResponse = createOrderService.execute(request.toUseCaseRequest())
        return RestApiResponse.success(
            CreateOrder.Response(useCaseResponse.orderId.value).fromUseCaseResponse(),
            HttpStatus.OK
        )
    }
}

class CreateOrder {

    data class CreateOrderItemRequest(
        val bookId: Long,
        val quantity: Int
    )

    data class Request(
        val idempotencyKey: UUID,
        val items: List<CreateOrderItemRequest>
    )

    data class Response(
        val orderId: Long
    )
}

fun CreateOrder.Request.toUseCaseRequest(): CreateOrderRequest {
    return CreateOrderRequest(
        idempotencyKey = this.idempotencyKey.toString(),
        items = this.items.map {
            OrderItemRequest(
                bookId = it.bookId,
                quantity = it.quantity
            )
        }
    )
}

fun CreateOrder.Response.fromUseCaseResponse(): CreateOrder.Response {
    return CreateOrder.Response(
        orderId = this.orderId
    )
}