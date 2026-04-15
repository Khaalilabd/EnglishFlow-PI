CREATE TYPE payment_status AS ENUM ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED');
CREATE TYPE payment_item_type AS ENUM ('COURSE', 'PACK');

CREATE TABLE payments (
    id          BIGSERIAL PRIMARY KEY,
    order_id    VARCHAR(64)  NOT NULL UNIQUE,
    token       VARCHAR(255),
    student_id  BIGINT       NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    student_email VARCHAR(255) NOT NULL,
    student_phone VARCHAR(50)  NOT NULL DEFAULT '',
    item_type   payment_item_type NOT NULL,
    item_id     BIGINT       NOT NULL,
    item_name   VARCHAR(500) NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    status      payment_status NOT NULL DEFAULT 'PENDING',
    transaction_id BIGINT,
    received_amount DECIMAL(10,2),
    cost        DECIMAL(10,2),
    payment_url VARCHAR(1000),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_student   ON payments(student_id);
CREATE INDEX idx_payments_status    ON payments(status);
CREATE INDEX idx_payments_item      ON payments(item_type, item_id);
CREATE INDEX idx_payments_order     ON payments(order_id);
