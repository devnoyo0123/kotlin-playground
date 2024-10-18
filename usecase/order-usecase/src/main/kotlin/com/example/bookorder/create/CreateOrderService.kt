package com.example.bookorder.create

import com.example.bookorder.book.Book
import com.example.bookorder.book.BookId
import com.example.bookorder.book.BookPort
import com.example.bookorder.book.exception.InsufficientStockException
import com.example.bookorder.order.Order
import com.example.bookorder.order.OrderItem
import com.example.bookorder.order.OrderPort
import com.example.bookorder.order.OrderStatus
import org.slf4j.LoggerFactory
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
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
        value = [ObjectOptimisticLockingFailureException::class],
        maxAttempts = 10,
        listeners = ["CreateOrderUseCaseRetryListener"]
    )
    @Transactional
    override fun execute(request: CreateOrderRequest): CreateOrderResponse {
        logger.info("Attempting to create order with idempotencyKey: ${request.idempotencyKey}")

        // 1. 중복 주문 체크
        orderPort.findByIdempotencyKey(request.idempotencyKey)?.let {
            logger.info("Duplicate order found for idempotencyKey: ${request.idempotencyKey}")
            return CreateOrderResponse(it.getEntityId(), it.status)
        }

        // 2. 책 정보 조회 및 재고 확인
        val bookQuantities = validateAndCreateBookQuantities(request.items)

        // 3. 실제 주문 생성 및 재고 감소
        return createOrderAndUpdateStock(request.idempotencyKey, bookQuantities)

    }

    private fun validateAndCreateBookQuantities(items: List<OrderItemRequest>): List<Pair<Book, Int>> {
        val bookIds = items.map { BookId.of(it.bookId) }
        val books = bookPort.findByIds(bookIds)
        val bookMap = books.associateBy { it.getEntityId() }

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
                bookId = book.getEntityId(),
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

        val savedOrder = orderPort.save(order)
        logger.info("Order created successfully: ${savedOrder.getEntityId()}")
        return CreateOrderResponse(savedOrder.getEntityId(), savedOrder.status)
    }
}

@Component("CreateOrderUseCaseRetryListener")
class CreateOrderUseCaseRetryListener : RetryListener {
    override fun <T : Any, E : Throwable> onError(
        context: RetryContext,
        callback: RetryCallback<T, E>,
        throwable: Throwable
    ) {
        val maxAttempts = context.getAttribute(RetryContext.MAX_ATTEMPTS) as Int
        println("retrying ${context.retryCount} of $maxAttempts")
        if (context.retryCount == maxAttempts) {
            // 최대 시도 횟수 초과 시 실행할 로직
            println("최대 재시도 횟수를 초과했습니다.")
        }
    }
}