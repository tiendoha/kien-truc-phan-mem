# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a microservices-based Order Management and Payment System built with **Spring Boot 3.1.5** and **Java 17**, implementing **Domain-Driven Design (DDD)** and **Clean Architecture** principles. The system consists of two main services that communicate via **Apache Kafka** for event-driven messaging:

- **Order Service** (Port 8081): Handles order lifecycle, items, vouchers, ratings, and statistics
- **Payment Service** (Port 8082): Manages payment processing and customer credits

## Architecture Highlights

### Clean Architecture Implementation
The project follows strict layer separation with **hexagonal architecture**:

**Order System Modules:**
- `order_domain_core`: Pure domain logic, entities, and value objects
- `order_application_service`: Application use cases, DTOs, and ports/interfaces
- `order_dataaccess`: Database persistence with JPA entities and repositories
- `order_messaging`: Kafka message handling and publishing
- `order_container`: REST API controllers and Spring Boot configuration

**Payment System Modules:**
- `payment_domain_core`: Payment domain logic and value objects
- `payment_application_service`: Payment use cases and ports
- `payment_dataaccess`: Payment persistence layer
- `payment_messaging`: Payment event handling
- `payment_container`: Payment REST API and configuration

### Key Domain Concepts
- **Order Aggregate**: Orders with items, voucher support, ratings, and status tracking
- **Payment Aggregate**: Payment processing with customer credit management
- **Event-Driven Communication**: Kafka topics for order-payment coordination
- **Database Schema**: Separate schemas for "order" and "payment" entities in PostgreSQL

## Development Commands

### Docker Infrastructure
```bash
# Start all services (Kafka, PostgreSQL, Order Service, Payment Service)
docker-compose up --build

# Stop all services
docker-compose down

# View logs for specific service
docker-compose logs -f order-service
docker-compose logs -f payment-service
```

### Maven Build Commands
```bash
# Build entire Order System
./order_system/mnw clean package -DskipTests

# Build specific Order System module
cd order_system/order_container && ../mnw clean package

# Build entire Payment System
./payment-system/mnw clean package -DskipTests

# Build specific Payment System module
cd payment-system/payment_container && ../mnw clean package

# Run tests
./order_system/mnw test
./payment-system/mnw test

# Install to local repository
./order_system/mnw install
./payment-system/mnw install
```

### Running Services Locally
```bash
# Order Service (Port 8081)
cd order_system/order_container
../mnw spring-boot:run

# Payment Service (Port 8082)
cd payment-system/payment_container
../mnw spring-boot:run
```

### Database Operations
```bash
# Connect to PostgreSQL
docker exec -it order-postgres psql -U postgres -d order_db

# View order tables
\dt "order".*

# View payment tables
\dt "payment".*
```

## Key Configuration Profiles

### Application Profiles
- **default**: Local development (localhost:5433, localhost:9092)
- **docker**: Docker container networking (service names, internal ports)

### Database Configuration
- **Order Database**: `order_db` with schema "order"
- **Payment Database**: Same database with schema "payment"
- **Port Exposed**: 5433 (to avoid conflicts with local PostgreSQL)

### Kafka Configuration
- **Bootstrap Servers**: localhost:9092 (local), order-kafka:9092 (docker)
- **Key Topics**:
  - `payment.order.request`: Order → Payment requests
  - `payment.order.response`: Payment → Order responses

## Domain Model Insights

### Order Entity Features
- **Status Flow**: PENDING → PAID/APPROVED → (optional rating)
- **Voucher Support**: Percentage/amount discounts with validation
- **Order Updates**: Item modification allowed only in PENDING status
- **Rating System**: 1-5 star ratings with comments for completed orders
- **Statistics**: Revenue, order counts, and analytics by date/customer

### Payment Entity Features
- **Credit Management**: Customer wallet system with credit entries
- **Payment Processing**: Integration with order lifecycle
- **Status Tracking**: Payment completion and failure handling

### Key Value Objects
- `OrderId`, `CustomerId`, `RestaurantId`, `TrackingId`: UUID-based identifiers
- `OrderItem`: Product, quantity, price, and subtotal calculations
- `Voucher`: Discount calculation logic (percentage/amount)
- `OrderStatistics`: Aggregated analytics data

## API Endpoints

### Order Service (Port 8081)
```bash
# Create Order
POST /orders
Content-Type: application/vnd.api.v1+json

# Get Orders by Customer
GET /orders?customerId={uuid}

# Update Order (items only)
PUT /orders/{orderId}

# Add Rating
POST /orders/{orderId}/rating

# Get Statistics
GET /orders/statistics?customerId={uuid}&startDate={date}&endDate={date}
```

### Payment Service (Port 8082)
- Payment processing endpoints
- Credit management endpoints
- Kafka message handling for order coordination

## Testing Approach

### Current Test Structure
- Basic Spring Boot test classes in each module
- Test location: `src/test/java/.../ApplicationTests.java`
- Tests are minimal and can be significantly expanded

### Recommended Testing Enhancements
- **Unit Tests**: Domain logic in `order_domain_core` and `payment_domain_core`
- **Integration Tests**: Repository layers and Kafka message flows
- **API Tests**: REST endpoint testing with MockMvc
- **Contract Tests**: Kafka message format validation
- **End-to-End Tests**: Full order-to-payment workflows

## Development Guidelines

### When Working with Domain Logic
- **Business Rules**: Keep domain logic pure in `*_domain_core` modules
- **Entity Design**: Protect invariants and use static factory methods
- **Value Objects**: Use for concepts with no identity (UUID wrappers, money, etc.)
- **Exception Handling**: Use domain-specific exceptions like `OrderDomainException`

### When Working with Application Services
- **Use Case Handlers**: One handler per business use case (Create, Update, Query, etc.)
- **Port Adapters**: Implement interfaces to connect to external systems
- **DTO Mapping**: Separate request/response DTOs from domain entities
- **Message Publishing**: Use publishers for cross-service communication

### When Working with Data Access
- **Entity Mapping**: Map JPA entities to domain entities using mappers
- **Repository Pattern**: Implement domain repositories with JPA
- **Transaction Management**: Handle transactions at application service layer
- **Schema Separation**: Use different schemas for different bounded contexts

### When Working with Messaging
- **Event Design**: Use immutable DTOs for Kafka messages
- **Error Handling**: Implement proper error handling for message failures
- **Message Serialization**: Use JSON with trusted package configuration
- **Consumer Groups**: Use appropriate group IDs for load balancing

## Common Debugging Scenarios

### Kafka Message Issues
```bash
# Check Kafka topics
docker exec -it order-kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume messages for debugging
docker exec -it order-kafka kafka-console-consumer --topic payment.order.response --bootstrap-server localhost:9092 --from-beginning
```

### Database Connection Issues
- Verify PostgreSQL is healthy: `curl http://localhost:5433` (won't work but checks port)
- Check container health: `docker ps` to see if order-postgres is healthy
- Review schema initialization in Docker logs

### Build Dependency Issues
- Run `./order_system/mnw clean install` from root to build all modules
- Verify inter-module dependencies in `pom.xml` files
- Check that all modules are properly listed in parent POMs