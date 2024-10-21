package com.example.bookorder.create

import com.example.bookorder.IntegrationTestConfiguration
import com.example.bookorder.book.Book
import com.example.bookorder.book.BookPort
import com.example.bookorder.create.exception.DuplicateOrderException
import com.example.bookorder.order.OrderPort
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.support.RetryTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("test")
class CreateOrderUseCaseIntegrationTest(
    @Autowired
    val createOrderUseCase: CreateOrderUseCase,
    @Autowired
    val bookPort: BookPort,
    @Autowired
    val orderPort: OrderPort,
    @Autowired val transactionTemplate: TransactionTemplate
) : IntegrationTestConfiguration() {

    @BeforeEach
    fun setup() {
        orderPort.deleteAll()
        bookPort.deleteAll()
    }

    @DisplayName("[성공테스트][동시성 재고테스트] 단일 책의 재고가 100권일 때 동시에 100명이 각각 1권씩 주문요청합니다. 최종 재고가 (초기 재고 - 성공한 주문 수)와 같은지 확인합니다.")
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
                }
                catch (e: Exception) {
                    results.add(OrderResult.Failure(e))
                }
                finally {
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
    }

    @DisplayName("[동시성 테스트] 동일한 idempotencyKey로 두 요청이 동시에 들어올 때, 하나는 성공하고 하나는 실패해야 합니다.")
    @Test
    fun `should process only one order successfully when two concurrent requests use the same idempotency key`() {
        val initialStock = 10
        val orderQuantity = 1
        val numberOfThreads = 2

        val book = Book(
            title = "Test Book",
            author = "Test Author",
            price = BigDecimal.valueOf(1000),
            stock = initialStock
        )
        val savedBook = bookPort.save(book)

        val idempotencyKey = UUID.randomUUID().toString()

        val cyclicBarrier = CyclicBarrier(numberOfThreads)
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        val results = ConcurrentLinkedQueue<OrderResult>()

        repeat(numberOfThreads) {
            executor.submit {
                try {
                    cyclicBarrier.await()
                    val request = CreateOrderRequest(
                        idempotencyKey = idempotencyKey,
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

        assertEquals(1, successfulOrders, "성공한 주문은 1개여야 합니다.")
        assertEquals(1, failedOrders, "실패한 주문은 1개여야 합니다.")
        assertEquals(initialStock - orderQuantity, updatedBook?.stock, "재고는 1만큼 감소해야 합니다.")

        // 실패한 주문의 예외 출력
        results.filterIsInstance<OrderResult.Failure>().forEach { failure ->
            println("실패한 주문의 예외: ${failure.exception}")
        }
    }


    @DisplayName("[실패테스트][동시성 재고테스트] 주문 수량이 재고보다 많은 경우, 2명이 동시에 주문을 시도할 때 재고가 부족하여 1명은 주문에 실패합니다.")
    @Test
    fun `should fail 1 out of 2 concurrent orders due to insufficient stock when each order requests the same item`() {
        val initialStock = 1
        val orderQuantity = 1

        val book1 = Book(title = "Book 1", author = "Author 1", price = BigDecimal.valueOf(1000), stock = initialStock)
        val book2 = Book(title = "Book 2", author = "Author 2", price = BigDecimal.valueOf(2000), stock = initialStock)
        val savedBook1 = bookPort.save(book1)
        val savedBook2 = bookPort.save(book2)

        val numberOfThreads = 2;
        val cyclicBarrier = CyclicBarrier(numberOfThreads)
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        val results = ConcurrentLinkedQueue<OrderResult>()

        executor.submit {
            try {
                cyclicBarrier.await()
                val request = CreateOrderRequest(
                    idempotencyKey = UUID.randomUUID().toString(),
                    items = listOf(
                        OrderItemRequest(savedBook1.id!!.value, orderQuantity),
                        OrderItemRequest(savedBook2.id!!.value, orderQuantity)
                    )
                )
                val response = createOrderUseCase.execute(request)
                results.add(OrderResult.Success(response))
            } catch (e: Exception) {
                results.add(OrderResult.Failure(e))
            } finally {
                latch.countDown()
            }
        }

        executor.submit {
            try {
                cyclicBarrier.await()
                val request = CreateOrderRequest(
                    idempotencyKey = UUID.randomUUID().toString(),
                    items = listOf(OrderItemRequest(savedBook2.id!!.value, orderQuantity))
                )
                val response = createOrderUseCase.execute(request)
                results.add(OrderResult.Success(response))
            } catch (e: Exception) {
                results.add(OrderResult.Failure(e))
            } finally {
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()

        val updatedBook2 = savedBook2.id?.let { bookPort.findById(it) }

        assertEquals(0, updatedBook2?.stock)

        val successfulOrders = results.count { it is OrderResult.Success }
        val failedOrders = results.count { it is OrderResult.Failure }

        assertEquals(1, successfulOrders, "성공 주문 수는 1입니다.")
        assertEquals(1, failedOrders, "실패 주문 수는 1입니다.")
        assertEquals(
            "InsufficientStockException",
            results.filter { it is OrderResult.Failure }.first().let { (it as OrderResult.Failure).exception::class.java.simpleName },
            "실패한 주문의 예외는 InsufficientStockException 이어야 합니다."
        )
    }

    @DisplayName("Retry 횟수 초과 시 예외 확인 (maxAttempts=1)")
    @Test
    fun `should throw exception after two attempts when max retry attempts are exceeded`() {
        // 테스트를 위한 Book 생성
        val book = Book(
            title = "Test Book",
            author = "Test Author",
            price = BigDecimal.valueOf(1000),
            stock = 10
        )
        val savedBook = bookPort.save(book)

        // CreateOrderRequest 생성
        val request = CreateOrderRequest(
            idempotencyKey = UUID.randomUUID().toString(),
            items = listOf(OrderItemRequest(savedBook.id!!.value, 1))
        )

        val attemptCount = AtomicInteger(0)

        // ObjectOptimisticLockingFailureException을 항상 발생시키는 모의 객체 생성
        val mockBookPort = object : BookPort by bookPort {
            override fun save(book: Book): Book {
                attemptCount.incrementAndGet()
                throw ObjectOptimisticLockingFailureException(Book::class.java, "Simulated conflict")
            }
        }

        // 모의 객체를 사용하는 CreateOrderService 생성
        val testCreateOrderService = CreateOrderService(mockBookPort, orderPort)

        // RetryTemplate 설정
        val retryTemplate = RetryTemplate().apply {
            setRetryPolicy(org.springframework.retry.policy.SimpleRetryPolicy(2))  // maxAttempts = 1 + 1 = 2
        }

        // 예외 발생 및 로깅
        try {
            retryTemplate.execute<Any, Exception> {
                transactionTemplate.execute {
                    testCreateOrderService.execute(request)
                }
                null
            }
            Assertions.fail("예외가 발생해야 합니다.")
        } catch (e: Exception) {
            println("발생한 예외 유형: ${e.javaClass.name}")
            println("예외 메시지: ${e.message}")
            e.printStackTrace()

            // 예외 유형 검증
            assertTrue(e is ObjectOptimisticLockingFailureException, "ObjectOptimisticLockingFailureException이 발생해야 합니다.")

            // 예외 메시지 검증
            assertTrue(e.message?.contains("Simulated conflict") == true, "예외 메시지에 'Simulated conflict'가 포함되어야 합니다.")
        }

        // 시도 횟수 검증
        assertEquals(2, attemptCount.get(), "메서드는 정확히 2번 호출되어야 합니다.")
    }

    @DisplayName("[동시성 테스트] 동일한 idempotencyKey로 두 요청이 동시에 들어올 때, 하나는 성공하고 하나는 예외가 발생해야 합니다.")
    @Test
    fun `should process only one order successfully and throw exception for the second when two concurrent requests use the same idempotency key`() {
        val initialStock = 10
        val orderQuantity = 1
        val numberOfThreads = 2

        val book = Book(
            title = "Test Book",
            author = "Test Author",
            price = BigDecimal.valueOf(1000),
            stock = initialStock
        )
        val savedBook = bookPort.save(book)

        val idempotencyKey = UUID.randomUUID().toString()

        val cyclicBarrier = CyclicBarrier(numberOfThreads)
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val latch = CountDownLatch(numberOfThreads)

        val results = ConcurrentLinkedQueue<OrderResult>()

        repeat(numberOfThreads) {
            executor.submit {
                try {
                    cyclicBarrier.await()
                    val request = CreateOrderRequest(
                        idempotencyKey = idempotencyKey,
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

        assertEquals(1, successfulOrders, "성공한 주문은 1개여야 합니다.")
        assertEquals(1, failedOrders, "실패한 주문은 1개여야 합니다.")
        assertEquals(initialStock - orderQuantity, updatedBook?.stock, "재고는 1만큼 감소해야 합니다.")

        // 실패한 주문의 예외 확인
        val failedException = results.filterIsInstance<OrderResult.Failure>().first().exception
        assertTrue(failedException is DuplicateOrderException, "중복된 idempotency key로 인한 예외가 발생해야 합니다.")
        assertEquals("이미 처리중인 요청입니다.", failedException.message)

        println("성공한 주문: $successfulOrders")
        println("실패한 주문: $failedOrders")
        println("실패한 주문의 예외: ${failedException.javaClass.simpleName} - ${failedException.message}")
    }

    sealed class OrderResult {
        data class Success(val response: CreateOrderResponse) : OrderResult()
        data class Failure(val exception: Exception) : OrderResult()
    }
}