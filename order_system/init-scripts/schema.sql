-- Create schema
CREATE SCHEMA IF NOT EXISTS "order";

-- Orders table (main aggregate)
CREATE TABLE "order".orders (
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    tracking_id UUID NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    order_status VARCHAR NOT NULL,
    failure_messages VARCHAR,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

-- Create SEQUENCE for order_items.id
CREATE SEQUENCE "order".order_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Order items table (aggregate members)
CREATE TABLE "order".order_items (
     id BIGINT NOT NULL DEFAULT nextval('"order".order_items_id_seq'),
     order_id UUID NOT NULL,
     product_id UUID NOT NULL,
     price NUMERIC(10, 2) NOT NULL,
     quantity INTEGER NOT NULL,
     sub_total NUMERIC(10, 2) NOT NULL,
     CONSTRAINT order_items_pkey PRIMARY KEY (id),
     CONSTRAINT uk_order_items_order_id UNIQUE (order_id, id),
     CONSTRAINT fk_order_id FOREIGN KEY (order_id)
         REFERENCES "order".orders(id)
);

-- Ensure sequence ownership
ALTER SEQUENCE "order".order_items_id_seq OWNED BY "order".order_items.id;

-- Create indexes
CREATE INDEX idx_orders_tracking_id
    ON "order".orders(tracking_id);

CREATE INDEX idx_orders_customer_id
    ON "order".orders(customer_id);

ALTER TABLE "order".orders
    ADD COLUMN original_price NUMERIC(10, 2),
    ADD COLUMN discount NUMERIC(10, 2) DEFAULT 0,
    ADD COLUMN voucher_code VARCHAR;

ALTER TABLE "order".orders
    ADD COLUMN rating INTEGER,
    ADD COLUMN comment VARCHAR;

UPDATE "order".orders SET original_price = price;