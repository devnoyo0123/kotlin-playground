package com.example.bookorder.create

import com.example.bookorder.book.BookId
import com.example.bookorder.book.BookPort
import com.example.bookorder.book.exception.InsufficientStockException
import com.example.bookorder.create.exception.OrderCreationException
import com.example.bookorder.distributed_lock.DistributedLock
import com.example.bookorder.order.Order
import com.example.bookorder.order.OrderItem
import com.example.bookorder.order.OrderPort
import com.example.bookorder.order.OrderStatus
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class CreateOrderService(
    private val bookPort: BookPort,
    private val orderPort: OrderPort
) : CreateOrderUseCase {

    private val logger = LoggerFactory.getLogger(CreateOrderService::class.java)

    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500)
    )
    @Transactional(readOnly = true)
    override fun execute(request: CreateOrderRequest): CreateOrderResponse {
        logger.info("Attempting to create order with idempotencyKey: ${request.idempotencyKey}")

        // 1. 중복 주문 체크
        orderPort.findByIdempotencyKey(request.idempotencyKey)?.let {
            logger.info("Duplicate order found for idempotencyKey: ${request.idempotencyKey}")
            return CreateOrderResponse(it.getId(), it.status)
        }

        // 2. 책 정보 조회 및 재고 확인
        val orderItems = validateAndCreateOrderItems(request.items)

        // 3. 실제 주문 생성 및 재고 감소
        return createOrderAndUpdateStock(request.idempotencyKey, orderItems)
    }

    private fun validateAndCreateOrderItems(items: List<OrderItemRequest>): List<OrderItem> {
        return items.map { item ->
            val book = bookPort.findById(BookId.of(item.bookId))
                ?: throw IllegalArgumentException("Book not found: ${item.bookId}")
            if (!book.canFulfillOrder(item.quantity)) {
                throw InsufficientStockException("Insufficient stock for book: ${book.getId()}")
            }
            OrderItem(book = book, quantity = item.quantity, price = book.price)
        }
    }

    @DistributedLock(
        keys = ["#orderItems.![book.id.value]"],
        waitTime = 10,
        leaseTime = 5
    )
    fun createOrderAndUpdateStock(idempotencyKey: String, orderItems: List<OrderItem>): CreateOrderResponse {
        try {
            // 재고 감소
            orderItems.forEach { item ->
                item.book.decreaseStock(item.quantity)
                bookPort.save(item.book)
            }

            // 주문 생성
            val totalAmount = orderItems.sumOf { it.price * BigDecimal(it.quantity) }
            val order = Order(
                idempotencyKey = idempotencyKey,
                totalAmount = totalAmount,
                status = OrderStatus.PENDING,
                orderItems = orderItems
            )

            val savedOrder = orderPort.save(order)
            logger.info("Order created successfully: ${savedOrder.id}")
            return CreateOrderResponse(savedOrder.getId(), savedOrder.status)
        } catch (e: Exception) {
            logger.error("Error occurred while creating order", e)
            throw OrderCreationException("Failed to create order", e)
        }
    }
}