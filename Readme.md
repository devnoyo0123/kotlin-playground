## 책 구매 API 개발 (불안정한 외부 결제 API 연동)

### 상황
- 온라인 서점에서 책 구매를 위한 API를 개발해야 합니다.
- 결제 처리를 위해 외부 결제 API를 사용해야 합니다.
- 외부 결제 API는 20% 확률로 간헐적으로 실패합니다.
- 외부 결제 API는 때때로 불안정하거나 느릴 수 있습니다.

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
