CREATE TABLE payment_tbl
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at   DATETIME(6) DEFAULT NULL,
    order_id     BIGINT                NOT NULL,
    status       VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_payment_tbl PRIMARY KEY (id),
    constraint UK_payment_tbl_order_id
        unique (order_id)
);

CREATE INDEX idx_payment_tbl_order_id ON payment_tbl (order_id);
