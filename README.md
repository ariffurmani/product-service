# product-service

REST microservice that manages the product catalogue and inventory for the e-commerce backend.

---

## Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [Architecture](#architecture)
4. [Getting Started](#getting-started)
5. [Configuration](#configuration)
6. [API Endpoints](#api-endpoints)
7. [Inter-Service Communication](#inter-service-communication)
8. [Database](#database)
9. [Running Tests](#running-tests)
10. [Docker](#docker)
11. [Environment Variables](#environment-variables)

---

## Overview

`product-service` owns the product catalogue, including product metadata, categories, and stock quantities. It handles creation, retrieval, update, soft-deletion, and stock adjustments for products. It does **not** own orders, payments, users, or any authentication/identity data — JWT tokens are validated locally using a shared secret issued by `user-service`.

---

## Tech Stack

| Layer              | Technology                          |
|--------------------|-------------------------------------|
| Language           | Java 17                             |
| Framework          | Spring Boot 3.5.5                   |
| Web                | Spring MVC (spring-boot-starter-web) |
| Persistence        | Spring Data JPA / Hibernate         |
| Database           | MySQL (mysql-connector-j)           |
| Security           | JJWT 0.12.5 (local JWT validation)  |
| HTTP Client        | Spring RestTemplate                 |
| Utilities          | Lombok                              |
| Build              | Maven (spring-boot-maven-plugin)    |

---

## Architecture

`product-service` is a standalone microservice within the e-commerce backend. It exposes a REST API consumed by other services (e.g. an order service that calls the stock decrement/increment endpoints). JWT-based authorisation is enforced by `ProductAuthorizationInterceptor` on all `/product/**` routes: read operations (`GET`/`HEAD`) require any valid token; write operations require the `ADMIN` role. The signing secret must match the one used by `user-service`. There are no synchronous HTTP calls made to other services at runtime; the `RestTemplate` bean is registered for future use.

```
┌─────────────┐        REST (JWT)        ┌─────────────────┐
│  API Client │ ───────────────────────► │ product-service │ ──► MySQL
│ / Gateway   │                          └─────────────────┘
└─────────────┘
                  shared JWT secret
                  ◄──────────────────►  user-service  (no direct HTTP call)
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8+ running on `localhost:3306` with a database named `ecom-backend`
- `JWT_SECRET_KEY` environment variable set (must match `user-service`)

### Clone, Build & Run

```bash
# Clone the repository
git clone <repository-url>
cd product-service

# Build (skip tests)
./mvnw clean package -DskipTests

# Run
JWT_SECRET_KEY=<your-base64-secret> ./mvnw spring-boot:run
```

---

## Configuration

Configuration is managed via `src/main/resources/application.properties`. There are no separate Spring profile files (`application-dev.properties` / `application-prod.properties`) in this codebase; profile-specific overrides should be supplied via environment variables or an external config source.

Key properties:

```properties
spring.application.name=product-service
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/ecom-backend
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret-key=${JWT_SECRET_KEY:}
```

---

## API Endpoints

All endpoints are prefixed with `/product` and protected by `ProductAuthorizationInterceptor`.  
`GET`/`HEAD` — any authenticated user. `POST`/`PUT`/`DELETE` — `ADMIN` role required.

| Method   | Endpoint                         | Description                              | Auth Required          |
|----------|----------------------------------|------------------------------------------|------------------------|
| `GET`    | `/product`                       | List all non-deleted products            | Bearer token           |
| `GET`    | `/product/{id}`                  | Get a single product by ID               | Bearer token           |
| `GET`    | `/product/category/{category}`   | List products by category name           | Bearer token           |
| `POST`   | `/product`                       | Create a new product                     | Bearer token (ADMIN)   |
| `PUT`    | `/product/{id}`                  | Replace an existing product              | Bearer token (ADMIN)   |
| `DELETE` | `/product/{id}`                  | Soft-delete a product                    | Bearer token (ADMIN)   |
| `POST`   | `/product/{id}/stock/increment`  | Add stock quantity to a product          | Bearer token (ADMIN)   |
| `POST`   | `/product/{id}/stock/decrement`  | Remove stock quantity from a product     | Bearer token (ADMIN)   |

---

## Inter-Service Communication

| Communicates With | Protocol | Purpose                                                                 |
|-------------------|----------|-------------------------------------------------------------------------|
| `user-service`    | —        | No runtime HTTP call; shares the JWT signing secret out-of-band to validate tokens locally |

> A `RestTemplate` bean is registered in `RestTemplateConfiguration` for future inter-service HTTP calls.

---

## Database

- **Type**: MySQL 8
- **Schema management**: Hibernate `ddl-auto=update` — tables are created/altered automatically on startup. No migration tool (Flyway/Liquibase) is present.
- **Database name**: `ecom-backend`

### JPA Entities

| Entity        | Table       | Description                                                        |
|---------------|-------------|--------------------------------------------------------------------|
| `BaseModel`   | *(superclass)* | Common fields: `id`, `isDeleted`, `createdAt`, `lastUpdatedAt`  |
| `Product`     | `product`   | `name`, `price`, `description`, `imageUrl`, `stockQuantity`, FK → `Category` |
| `Category`    | `category`  | `name`, one-to-many → `Product`                                    |

---

## Running Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ProductServiceApplicationTests

# Run tests and generate coverage report (if Jacoco is configured)
./mvnw verify
```

> Jacoco is **not** configured in `pom.xml`. To enable coverage reporting, add the `jacoco-maven-plugin` to the `<build><plugins>` section.

---

## Docker

No `Dockerfile` is present in this repository. You can build and run a container using the Spring Boot Maven plugin's built-in image builder (requires a local Docker daemon):

```bash
# Build OCI image using Cloud Native Buildpacks
./mvnw spring-boot:build-image -DskipTests

# Run the container
docker run --rm \
  -p 8080:8080 \
  -e JWT_SECRET_KEY=<your-base64-secret> \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/ecom-backend \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  product-service:0.0.1-SNAPSHOT
```

---

## Environment Variables

| Variable                    | Description                                                                 | Example                                      |
|-----------------------------|-----------------------------------------------------------------------------|----------------------------------------------|
| `JWT_SECRET_KEY`            | Base64-encoded (or raw) HMAC secret used to verify JWTs; must match `user-service` | `c2VjcmV0a2V5MTIzNDU2Nzg=`             |
| `SPRING_DATASOURCE_URL`     | JDBC URL for the MySQL database                                             | `jdbc:mysql://localhost:3306/ecom-backend`   |
| `SPRING_DATASOURCE_USERNAME`| MySQL username                                                              | `root`                                       |
| `SPRING_DATASOURCE_PASSWORD`| MySQL password                                                              | `root`                                       |
| `SERVER_PORT`               | HTTP port the service listens on (default `8080`)                          | `8080`                                       |

