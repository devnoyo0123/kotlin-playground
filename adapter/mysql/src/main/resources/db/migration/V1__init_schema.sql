CREATE TABLE IF NOT EXISTS book_tbl (
                                        id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        price      DECIMAL(38, 2) NOT NULL,
    stock      INT            NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) DEFAULT NULL,
    version    BIGINT         NOT NULL,
    author     VARCHAR(255)   NOT NULL,
    title      VARCHAR(255)   NOT NULL
    );

CREATE TABLE IF NOT EXISTS order_tbl (
                                         id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         total_amount    DECIMAL(38, 2) NOT NULL,
    created_at      DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at      DATETIME(6) DEFAULT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    status          ENUM ('FAILED', 'PAID', 'PAYMENT_UNCERTAIN', 'PENDING') NOT NULL,
    CONSTRAINT UKqkjuoxvcuko0cbvow1cd857fg UNIQUE (idempotency_key)
    );

CREATE INDEX idx_order_idempotency_key ON order_tbl (idempotency_key);

CREATE TABLE IF NOT EXISTS orderitem_tbl (
                                             id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             price      DECIMAL(38, 2) NOT NULL,
    quantity   INT            NOT NULL,
    book_id    BIGINT         NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) DEFAULT NULL,
    order_id   BIGINT         NOT NULL
    );

CREATE INDEX idx_orderitem_book_id ON orderitem_tbl (book_id);
CREATE INDEX idx_orderitem_order_id ON orderitem_tbl (order_id);