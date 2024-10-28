package com.example.bookorder.create

import com.example.bookorder.book.Book
import com.example.bookorder.book.BookId
import com.example.bookorder.book.BookPort
import com.example.bookorder.book.exception.InsufficientStockException
import com.example.bookorder.create.exception.CreateOrderUseCaseExceptionWrapper
import com.example.bookorder.order.*
import org.slf4j.LoggerFactory
import org.springframework.orm.ObjectOptimisticLockingFailureException
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

    @CreateOrderUseCaseExceptionWrapper
    @Retryable(
        value = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 5,
    )
    @Transactional
    override fun execute(request: CreateOrderRequest): CreateOrderResponse {
        logger.debug("Attempting to create order with idempotencyKey: ${request.idempotencyKey}")

        // 1. 중복 주문 체크
        orderPort.findByIdempotencyKey(request.idempotencyKey)?.let {
            logger.debug("Duplicate order found for idempotencyKey: ${request.idempotencyKey}")
            return CreateOrderResponse(it.getEntityIdOrThrow(), it.status, OrderSuccessReason.ALREADY_PAID.formatMessage())
        }

        // 2. 책 정보 조회 및 재고 확인
        val bookQuantities = validateAndCreateBookQuantities(request.items)

        // 3. 실제 주문 생성 및 재고 감소
        return createOrderAndUpdateStock(request.idempotencyKey, bookQuantities)
    }

    private fun validateAndCreateBookQuantities(items: List<OrderItemRequest>): List<Pair<Book, Int>> {
        val bookIds = items.map { BookId.of(it.bookId) }
        val books = bookPort.findByIds(bookIds)
        val bookMap = books.associateBy { it.getEntityIdOrThrow() }

        return items.map { item ->
            val bookId = BookId.of(item.bookId)
            val book = bookMap[bookId] ?: throw IllegalArgumentException("Book not found: ${item.bookId}")
            if (book.canFulfillOrder(item.quantity).not()) {
                throw InsufficientStockException.forBookId(bookId)
            }
            book to item.quantity
        }
    }

    fun createOrderAndUpdateStock(idempotencyKey: String, bookQuantities: List<Pair<Book, Int>>): CreateOrderResponse {

        bookQuantities.map { (book, quantity) ->
            book.decreaseStock(quantity)
            bookPort.save(book)
        }

        // OrderItem 생성
        val orderItems = bookQuantities.map { (book, quantity) ->
            OrderItem(
                bookId = book.getEntityIdOrThrow(),
                quantity = quantity,
                price = book.price
            )
        }

        // 주문 생성
        val totalAmount = orderItems.sumOf { it.price * BigDecimal(it.quantity) }
        val order = Order(
            idempotencyKey = idempotencyKey,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            orderItems = orderItems
        )

        val createdOrder = orderPort.save(order)
        orderPort.save(
            OrderEvent(
                orderId = createdOrder.getEntityIdOrThrow(),
                orderStatus = createdOrder.status,
                totalAmount = createdOrder.totalAmount
            )
        )
        logger.debug("Order created successfully: ${createdOrder.getEntityIdOrThrow()}")
        return CreateOrderResponse(
            createdOrder.getEntityIdOrThrow(),
            createdOrder.status,
            OrderSuccessReason.ORDER_CREATED.formatMessage()
        )
    }
}
