-- Create schema for Payment Service
CREATE SCHEMA IF NOT EXISTS "payment";

-- Credit Entry table (customer's wallet/credit)
CREATE TABLE "payment".credit_entry (
                                        id UUID NOT NULL,
                                        customer_id UUID NOT NULL UNIQUE,
                                        total_credit NUMERIC(10, 2) NOT NULL,
                                        CONSTRAINT credit_entry_pkey PRIMARY KEY (id)
);

-- Payment table
CREATE TABLE "payment".payment (
                                   id UUID NOT NULL,
                                   order_id UUID NOT NULL,
                                   customer_id UUID NOT NULL,
                                   price NUMERIC(10, 2) NOT NULL,
                                   payment_status VARCHAR NOT NULL,
                                   created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                   CONSTRAINT payment_pkey PRIMARY KEY (id)
);

-- Seed data for credit (TESTING)
INSERT INTO "payment".credit_entry (id, customer_id, total_credit)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb1f', 'd215b5f8-0249-4dc5-89a3-51fd148cfb40', 100.00);


INSERT INTO "payment".credit_entry (id, customer_id, total_credit)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 50.00);
