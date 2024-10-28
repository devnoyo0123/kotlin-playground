# 도서 주문 및 결제 시스템
이 프로젝트는 도서 주문 및 결제 처리를 위한 마이크로서비스 아키텍처 기반의 시스템입니다. Kotlin과 Spring Boot를 사용하여 구현되었으며, 주문 생성, 결제 처리, 그리고 불확실한 결제 상태 처리 등의 기능을 포함하고 있습니다.

### 상황
- 온라인 서점에서 책 구매를 위한 API를 개발해야 합니다.
- 결제 처리를 위해 외부 결제 API를 사용해야 합니다.
- 외부 결제 API는 33% 확률로 간헐적으로 실패합니다.
- 외부 결제 API는 33% 확률로 60초후 응답을 보냅니다.

### 요구사항
1. 책 구매 API 구현
   - 사용자의 책 구매 요청을 처리합니다.
   - 외부 결제 API를 이용해 결제를 진행합니다.
2. 안정적인 결제 처리
   - 불안정한 외부 API에 대응하여 안정적으로 결제를 처리해야 합니다.
3. 재고 관리
   - 주문 처리 시 책의 재고를 정확하게 관리해야 합니다.

### 핵심 기술적 과제
1. 멱등성 API 설계
   - 동일한 요청이 중복 처리되지 않도록 멱등성을 보장해야 합니다.
2. 동시성 제어
   - 다수의 동시 주문 처리 시 재고 관리의 정확성을 유지해야 합니다.
3. 외부 API 연동 최적화
   - 외부 API 호출 시 적절한 타임아웃을 설정해야 합니다.
   - API 호출 실패에 대한 재시도 메커니즘을 구현해야 합니다.
   - 타임아웃 발생 후 결제 상태 불확실성 처리 방안을 마련해야 합니다.
4. 장애 대응 및 복구
   - 외부 API의 간헐적 실패와 불안정성에 대응하는 방안을 마련해야 합니다.
5. 결제 상태 동기화
   - 타임아웃 이후 실제 결제 완료 여부를 확인하고 동기화하는 메커니즘을 구현해야 합니다.


## 시스템 아키텍처



## 주요 컴포넌트

### 1. 주문 생성 (CreateOrderService)

<details>
<summary>코드 보기</summary>
```kotlin

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
```
</details>

- 중복 주문 체크
- 책 정보 조회 및 재고 확인
- 주문 생성 및 재고 감소
- 낙관적 락을 사용한 동시성 제어


### 2. 결제 처리 (ProcessPaymentService)

<details>
<summary>코드 보기</summary>
```kotlin
@Transactional
override fun processPayment(orderEvent: OrderEvent): Payment {
    // ... (코드 내용)
}
```
</details>

- 결제 요청 및 상태 확인
- 타임아웃 발생 시 불확실한 결제 상태 처리
- 결제 완료 또는 실패 처리


### 3. 불확실한 결제 상태 처리 (HandleUncertainPayment)

<details>
<summary>코드 보기</summary>
```kotlin
@Retryable(
    value = [InValidPaymentStatusException::class],
    maxAttempts = 3,
    backoff = Backoff(delay = 1000, multiplier = 2.0, maxDelay = 30000),
    recover = "handleMaxRetryAttempts"
)
@Transactional(propagation = Propagation.REQUIRES_NEW)
fun retryPaymentStatus(payment: Payment): Payment {
    // ... (코드 내용)
}
```
</details>

- 결제 상태 재시도 로직
- 최대 재시도 횟수 초과 시 복구 메서드 실행