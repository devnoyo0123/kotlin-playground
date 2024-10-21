package com.example.bookorder.controller.order

import com.example.bookorder.RestApiResponse
import com.example.bookorder.controller.order.dto.validator.ValidItemsList
import com.example.bookorder.create.CreateOrderRequest
import com.example.bookorder.create.CreateOrderResponse
import com.example.bookorder.create.CreateOrderUseCase
import com.example.bookorder.create.OrderItemRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CreateOrderController(
    val createOrderService: CreateOrderUseCase
) {

    @PostMapping("/v1/order")
    fun createOrder(
        @Valid
        @RequestBody request: CreateOrder.Request
    ): ResponseEntity<RestApiResponse<CreateOrder.Response>> {
        val useCaseResponse = createOrderService.execute(request.toUseCaseRequest())
        return RestApiResponse.success(
            data = useCaseResponse.toPayload(),
            message = useCaseResponse.message,
            HttpStatus.OK
        )
    }

}

class CreateOrder {
    data class CreateOrderItemRequest(
        @field:NotNull(value = "책 ID는 필수입니다.")
        val bookId: Long,
        @field:NotNull(value = "주문 수량은 필수입니다.")
        @field:Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
        val quantity: Int
    )

    data class Request(
        @field:NotNull(value = "Idempotency key는 필수입니다.")
        val idempotencyKey: UUID,
        @field:ValidItemsList
        val items: List<CreateOrderItemRequest>
    )

    data class Response(
        val orderId: Long,
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

fun CreateOrderResponse.toPayload(): CreateOrder.Response {
    return CreateOrder.Response(
        orderId = this.orderId.value,
    )
}