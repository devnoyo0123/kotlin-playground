CREATE TABLE order_created_event_tbl
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   datetime              NOT NULL,
    updated_at   datetime              NOT NULL,
    deleted_at   datetime              NULL,
    order_id     BIGINT                NOT NULL,
    total_amount DECIMAL               NOT NULL,
    order_status VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_order_created_event_tbl PRIMARY KEY (id),
    constraint UK_order_created_event_tbl_order_id
        unique (order_id)
);

CREATE INDEX idx_order_created_event_tbl_order_id ON order_created_event_tbl (order_id);
