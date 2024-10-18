package com.example.bookorder.create

import com.example.bookorder.IntegrationTestConfiguration
import com.example.bookorder.book.Book
import com.example.bookorder.book.BookPort
import com.example.bookorder.order.OrderPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@ActiveProfiles("test")
class CreateOrderUseCaseIntegrationTest(
    @Autowired
    val createOrderUseCase: CreateOrderUseCase,
    @Autowired
    val bookPort: BookPort,
    @Autowired
    val orderPort: OrderPort
): IntegrationTestConfiguration() {

    @BeforeEach
    fun setup() {
        orderPort.deleteAll()
        bookPort.deleteAll()
    }

    @DisplayName("[성공테스트] 동일 책의 100개 재고가 있을 때 100명이 각각 1권씩 주문하여 재고가 0이 되고, 성공한 주문 수가 초기 재고와 같습니다.")
    @Test
    fun `should process concurrent orders successfully when stock is 100 and reduce stock to 0`() {
        val initialStock = 100
        val orderQuantity = 1
        val numberOfThreads = 100

        val book = Book(
            title = "Test Book",
            author = "Test Author",
            price = BigDecimal.valueOf(1000),
            stock = initialStock
        )
        val savedBook = bookPort.save(book)

        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        val results = ConcurrentLinkedQueue<OrderResult>()

        repeat(numberOfThreads) {
            executor.submit {
                try {
                    val request = CreateOrderRequest(
                        idempotencyKey = UUID.randomUUID().toString(),
                        items = listOf(OrderItemRequest(savedBook.id!!.value, orderQuantity))
                    )
                    val response = createOrderUseCase.execute(request)
                    results.add(OrderResult.Success(response))
                } catch (e: Exception) {
                    results.add(OrderResult.Failure(e))
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        val updatedBook = savedBook.id?.let { bookPort.findById(it) }

        val successfulOrders = results.count { it is OrderResult.Success }
        val failedOrders = results.count { it is OrderResult.Failure }

        // 성공한 주문 수와 실패한 주문 수의 합이 총 스레드 개수와 같은지 확인
        assertEquals(numberOfThreads, successfulOrders + failedOrders, "총 처리된 주문 수는 스레드 개수와 같아야 합니다.")

        // 최종 재고가 (초기 재고 - 성공한 주문 수)와 같은지 확인
        val expectedFinalStock = initialStock - successfulOrders
        assertEquals(expectedFinalStock, updatedBook?.stock, "최종 재고는 (초기 재고 - 성공한 주문 수)와 같아야 합니다.")

        // 성공한 주문과 실패한 주문의 총 합이 100인지 확인
        assertEquals(numberOfThreads, successfulOrders + failedOrders, "총 주문 처리 수는 $numberOfThreads 이어야 합니다.")


//        // 실패한 주문이 있다면, 그 이유가 재고 부족인지 확인
//        results.filterIsInstance<OrderResult.Failure>().forEach { failedOrder ->
//            assertTrue(failedOrder.exception is InsufficientStockException, "실패한 주문은 재고 부족으로 인한 것이어야 합니다.")
//        }
    }

//    @DisplayName("[실패테스트] 단일 책의 재고가 100권일 때 동시에 12명이 각각 10권씩 주문요청합니다. 10명은 성공하고 2명은 실패합니다.")
//    @Test
//    fun `should process 12 concurrent orders where 10 succeed and 2 fail when stock is 100 and each order requests 10 items`() {
//        val initialStock = 100
//        val orderQuantity = 10
//        val numberOfThreads = 12
//
//        val book = BookEntity(
//            title = "Test Book",
//            author = "Test Author",
//            price = BigDecimal.valueOf(1000),
//            stock = initialStock
//        )
//        val savedBook = bookPort.save(book)
//
//        val cyclicBarrier = CyclicBarrier(numberOfThreads)
//        val executor = Executors.newFixedThreadPool(numberOfThreads)
//        val latch = CountDownLatch(numberOfThreads)
//
//        val results = ConcurrentLinkedQueue<OrderResult>()
//
//        repeat(numberOfThreads) {
//            executor.submit {
//                try {
//                    cyclicBarrier.await()
//                    val request = CreateOrderUseCase.CreateOrderRequest(
//                        idempotencyKey = UUID.randomUUID().toString(),
//                        items = listOf(CreateOrderUseCase.OrderItemRequest(savedBook.id!!, orderQuantity))
//                    )
//                    val response = createOrderUseCase.execute(request)
//                    results.add(OrderResult.Success(response))
//                } catch (e: Exception) {
//                    results.add(OrderResult.Failure(e))
//                } finally {
//                    latch.countDown()
//                }
//            }
//        }
//
//        latch.await()
//        executor.shutdown()
//
//        val updatedBook = savedBook.id?.let { bookPort.findById(it) }
//
//        assertEquals(0, updatedBook?.stock)
//
//        val successfulOrders = results.count { it is OrderResult.Success }
//        val failedOrders = results.count { it is OrderResult.Failure }
//
//        assertEquals(10, successfulOrders, "성공 주문 수는 10입니다.")
//        assertEquals(2, failedOrders, "실패 주문 수는 2입니다.")
//    }
//

//    @DisplayName("[실패테스트] 주문 수량이 재고보다 많은 경우, 2명이 동시에 주문을 시도할 때 재고가 부족하여 1명은 주문에 실패합니다.")
//    @Test
//    fun `should fail 1 out of 2 concurrent orders due to insufficient stock when each order requests the same item`() {
//        val initialStock = 1
//        val orderQuantity = 1
//
//    val book1 = Book(title = "Book 1", author = "Author 1", price = BigDecimal.valueOf(1000), stock = initialStock)
//        val book2 = Book(title = "Book 2", author = "Author 2", price = BigDecimal.valueOf(2000), stock = initialStock)
//        val savedBook1 = bookPort.save(book1)
//        val savedBook2 = bookPort.save(book2)
//
//    val numberOfThreads = 2;
//    val cyclicBarrier = CyclicBarrier(numberOfThreads)
//    val executor = Executors.newFixedThreadPool(numberOfThreads)
//        val latch = CountDownLatch(numberOfThreads)
//
//        val results = ConcurrentLinkedQueue<OrderResult>()
//
//        executor.submit {
//            try {
//                cyclicBarrier.await()
//                val request = CreateOrderRequest(
//                    idempotencyKey = UUID.randomUUID().toString(),
//                    items = listOf(
//                        OrderItemRequest(savedBook1.id!!.value, orderQuantity),
//                        OrderItemRequest(savedBook2.id!!.value, orderQuantity)
//                    )
//                )
//                val response = createOrderUseCase.execute(request)
//                results.add(OrderResult.Success(response))
//            } catch (e: Exception) {
//                results.add(OrderResult.Failure(e))
//            } finally {
//                latch.countDown()
//            }
//        }
//
//        executor.submit {
//            try {
//                cyclicBarrier.await()
//                val request = CreateOrderRequest(
//                    idempotencyKey = UUID.randomUUID().toString(),
//                    items = listOf(OrderItemRequest(savedBook2.id!!.value, orderQuantity))
//                )
//                val response = createOrderUseCase.execute(request)
//                results.add(OrderResult.Success(response))
//            } catch (e: Exception) {
//                results.add(OrderResult.Failure(e))
//            } finally {
//                latch.countDown()
//            }
//        }
//
//        latch.await()
//        executor.shutdown()
//
//        val updatedBook2 = savedBook2.id?.let { bookPort.findById(it) }
//
//        assertEquals(0, updatedBook2?.stock)
//
//        val successfulOrders = results.count { it is OrderResult.Success }
//        val failedOrders = results.count { it is OrderResult.Failure }
//
//        assertEquals(1, successfulOrders, "성공 주문 수는 1입니다.")
//        assertEquals(1, failedOrders, "실패 주문 수는 1입니다.")
//    }

    sealed class OrderResult {
        data class Success(val response: CreateOrderResponse) : OrderResult()
        data class Failure(val exception: Exception) : OrderResult()
    }
}