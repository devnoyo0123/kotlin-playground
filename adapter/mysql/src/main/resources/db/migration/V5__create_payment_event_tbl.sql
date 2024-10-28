CREATE TABLE payment_event_tbl
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at   DATETIME(6) DEFAULT NULL,
    payment_id   BIGINT                NOT NULL,
    status       VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_payment_event_tbl PRIMARY KEY (id),
    constraint UK_payment_event_tbl_payment_id
        unique (payment_id)
);

CREATE INDEX idx_payment_event_tbl_payment_id ON payment_event_tbl (payment_id);