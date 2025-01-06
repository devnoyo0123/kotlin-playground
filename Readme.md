# Kotlin Bookstore Project

## 프로젝트 구조

이 프로젝트는 클린 아키텍처 원칙을 기반으로 설계된 서점 시스템입니다.

### 아키텍처 개요

```
📦 프로젝트 루트
├── 🏛 domain           # 핵심 비즈니스 엔티티 및 규칙
├── 🔄 usecase          # 비즈니스 로직 및 유스케이스
├── 🔌 adapter          # 외부 시스템 어댑터
├── 🌐 external-api     # 외부 API 인터페이스
├── 📡 bookstore-api    # 서점 API 엔드포인트
├── 👷 order-worker     # 주문 처리 워커
├── 💳 payment-worker   # 결제 처리 워커
├── 🔧 common           # 공통 유틸리티 및 설정
└── 📦 container        # 컨테이너 설정
```

### 계층 구조

#### 1. 핵심 계층
- **Domain Layer** (`domain/`)
  - 비즈니스 엔티티
  - 핵심 비즈니스 규칙
  - 도메인 이벤트

- **Usecase Layer** (`usecase/`)
  - 애플리케이션 비즈니스 로직
  - 트랜잭션 처리
  - 도메인 객체 조작

#### 2. 인터페이스 계층
- **Adapter Layer** (`adapter/`)
  - 데이터베이스 어댑터
  - 외부 서비스 어댑터
  - 메시징 어댑터

- **API Layer**
  - `bookstore-api/`: REST API 엔드포인트
  - `external-api/`: 외부 시스템 연동

#### 3. 워커 모듈
- **Order Worker** (`order-worker/`)
  - 주문 처리 및 관리
  - 재고 관리

- **Payment Worker** (`payment-worker/`)
  - 결제 처리
  - 결제 상태 관리

#### 4. 공통 모듈
- **Common** (`common/`)
  - 공통 유틸리티
  - 공유 설정
  - 기반 클래스

### 기술 스택
- Language: Kotlin
- JVM Version: Java 21
- Build Tool: Gradle (Kotlin DSL)
- Testing: JUnit

