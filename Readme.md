# 도서 주문 및 결제 시스템
이 프로젝트는 도서 주문 및 결제 처리를 위한 마이크로서비스 아키텍처 기반의 시스템입니다. Kotlin과 Spring Boot를 사용하여 구현되었으며, 주문 생성, 결제 처리, 그리고 불확실한 결제 상태 처리 등의 기능을 포함하고 있습니다.

### 상황
- 온라인 서점에서 책 구매를 위한 API를 개발해야 합니다.
- 결제 처리를 위해 외부 결제 API를 사용해야 합니다.
- 외부 결제 API는 33% 확률로 간헐적으로 실패합니다.
- 외부 결제 API는 33% 확률로 30초후 응답을 보냅니다.

### 요구사항
1. 책 구매 API 구현
   - 사용자의 책 구매 요청을 처리합니다.
   - 외부 결제 API를 이용해 결제를 진행합니다.
2. 안정적인 결제 처리
   - 불안정한 외부 API에 대응하여 안정적으로 결제를 처리해야 합니다.
3. 재고 관리
   - 주문 처리 시 책의 재고를 정확하게 관리해야 합니다.

### 핵심 기술적 과제
- 멱등성 API 설계
   - 동일한 요청이 중복 처리 되지 않도록 멱등성을 보장해야 합니다.
- 동시성 제어
   - 다수의 동시 주문 처리시 재고관리의 정확성을 유지해야 합니다.
- 외부 API 연동
   - 외부 API 호출시 적절한 타임아웃을 설정해야합니다.
   - 타임아웃 발생후 결제 상태 불확실성 처리 방안을 마련해야합니다.
- 보상 트랜잭션
   - 결제 취소가 된 경우 책 주문 상태를 실패로 변경하고, 책 재고도 원복해야합니다.
- 분산락
   - 중복컨슘을 대비하여 중복처리 처리되면 안되는 외부 API, 재고 감소 중복 로직을 한번만 처리해야합니다.
- 데드레터큐
   - 주문 이벤트를 컨슈밍하는 결제 처리 UseCase가 실패하여 DLQ로 빠진경우 외부 API로 결제 취소 요청 및 결제 취소 처리해야합니다.

## 시스템 아키텍처
헥사고날 아키텍처 기반의 프로젝트입니다.
- adapter
- usecase
- domain
- common

## 주요 컴포넌트

### 1. bookstore-API
- 주문 생성 API

### 2. payment-worker
- 결제 처리 워커

### 3. order-worker
- 주문 처리 워커

### 4. external-api
- 외부 API 