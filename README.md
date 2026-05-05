# Product Service - E-Commerce Backend

A Spring Boot microservice that provides RESTful APIs for product management in an e-commerce platform. This service handles product CRUD operations, category management, and soft-delete functionality.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
  - [Endpoints](#endpoints)
  - [Request/Response Models](#requestresponse-models)
  - [Error Responses](#error-responses)
- [Database](#database)
- [Project Structure](#project-structure)
- [API Testing with Postman](#api-testing-with-postman)
- [Validation Rules](#validation-rules)
- [Exception Handling](#exception-handling)

---

## Features

✅ **Product Management**
- Create, read, update, and delete products
- Soft-delete support (products marked as deleted, not physically removed)
- Search products by category
- List all active products

✅ **Category Management**
- Auto-create categories on product creation
- Search products by category name
- Support for multiple products per category

✅ **Robust Validation**
- Input validation for product data
- Null/blank string checks
- Price validation (must be positive)
- Consistent error messages

✅ **Exception Handling**
- Global exception handler via `@RestControllerAdvice`
- Proper HTTP status codes (400, 404, 500)
- Structured error responses

✅ **REST API**
- JSON request/response bodies
- Consistent API design
- Class-level request mapping (`/products`)

---

## Tech Stack

| Component | Version |
|-----------|---------|
| **Java** | 17+ |
| **Spring Boot** | 3.x |
| **Spring Data JPA** | 3.x |
| **MySQL** | 8.0+ |
| **Maven** | 3.8.x+ |
| **Lombok** | 1.18.x |

---

## Prerequisites

Before you begin, ensure you have installed:

1. **Java 17+** — Check with `java -version`
2. **Maven 3.8.x+** — Check with `mvn --version`
3. **MySQL 8.0+** — Check with `mysql --version`
4. **Git** (optional, for cloning)

---

## Installation & Setup

### 1. Clone or Navigate to the Project

```bash
cd /path/to/product-service
```

### 2. Create MySQL Database

Open MySQL client and create the database:

```bash
mysql -u root -p
```

```sql
CREATE DATABASE IF NOT EXISTS `ecom-backend`;
USE `ecom-backend`;
```

Exit MySQL:
```sql
EXIT;
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=product-service

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ecom-backend
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Update the `username` and `password` if your MySQL setup differs.**

### 4. Build the Project

```bash
cd /path/to/product-service
bash ./mvnw clean install
```

Or with Maven directly:

```bash
mvn clean install
```

---

## Running the Application

### Start the Service

```bash
bash ./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

### Expected Output

```
Started ProductServiceApplication in X.XXX seconds
```

The service runs on `http://localhost:8080` by default.

### Verify the Service is Running

```bash
curl http://localhost:8080/products
```

You should get a `200 OK` response with an empty JSON array `[]` (if the database is fresh).

---

## API Documentation

### Base URL

```
http://localhost:8080
```

### Authentication

Currently, no authentication is required. (Future releases may add JWT-based security.)

### Response Format

All responses are in **JSON** format with the following structure:

**Success Response (2xx):**
```json
{
  "id": 1,
  "name": "Product Name",
  "price": 99.99,
  "description": "Product description",
  "category": {
    "id": 1,
    "name": "electronics"
  },
  "imageUrl": "https://example.com/image.jpg",
  "isDeleted": false,
  "createdAt": "2026-05-05T10:00:00.000Z",
  "lastUpdatedAt": "2026-05-05T10:00:00.000Z"
}
```

**Error Response (4xx, 5xx):**
```json
{
  "message": "Error description"
}
```

---

## Endpoints

### 1. Get All Products

**Request**

```http
GET /products HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Response (200 OK)**

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "price": 1099.99,
    "description": "Apple smartphone",
    "category": {
      "id": 1,
      "name": "electronics"
    },
    "imageUrl": "https://example.com/images/iphone15.jpg",
    "isDeleted": false,
    "createdAt": "2026-05-05T10:30:00.000Z",
    "lastUpdatedAt": "2026-05-05T10:30:00.000Z"
  }
]
```

**cURL Example**

```bash
curl -X GET http://localhost:8080/products \
  -H "Content-Type: application/json"
```

---

### 2. Get Product by ID

**Request**

```http
GET /products/{id} HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Product ID (path parameter) |

**Response (200 OK)**

```json
{
  "id": 1,
  "name": "iPhone 15",
  "price": 1099.99,
  "description": "Apple smartphone",
  "category": {
    "id": 1,
    "name": "electronics"
  },
  "imageUrl": "https://example.com/images/iphone15.jpg",
  "isDeleted": false,
  "createdAt": "2026-05-05T10:30:00.000Z",
  "lastUpdatedAt": "2026-05-05T10:30:00.000Z"
}
```

**Error Response (404 Not Found)**

```json
{
  "message": "Product with id 999 not found"
}
```

**Error Response (400 Bad Request)**

```json
{
  "message": "Product id is required"
}
```

**cURL Example**

```bash
curl -X GET http://localhost:8080/products/1 \
  -H "Content-Type: application/json"
```

---

### 3. Create Product

**Request**

```http
POST /products HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Request Body (JSON)**

```json
{
  "name": "iPhone 15",
  "price": 1099.99,
  "description": "Apple smartphone",
  "category": "electronics",
  "imageUrl": "https://example.com/images/iphone15.jpg"
}
```

**Response (201 Created)**

```json
{
  "id": 1,
  "name": "iPhone 15",
  "price": 1099.99,
  "description": "Apple smartphone",
  "category": {
    "id": 1,
    "name": "electronics"
  },
  "imageUrl": "https://example.com/images/iphone15.jpg",
  "isDeleted": false,
  "createdAt": "2026-05-05T10:30:00.000Z",
  "lastUpdatedAt": "2026-05-05T10:30:00.000Z"
}
```

**Error Response (400 Bad Request)**

```json
{
  "message": "Product name is required"
}
```

**cURL Example**

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "price": 1099.99,
    "description": "Apple smartphone",
    "category": "electronics",
    "imageUrl": "https://example.com/images/iphone15.jpg"
  }'
```

---

### 4. Update Product

**Request**

```http
PUT /products/{id} HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Product ID (path parameter) |

**Request Body (JSON)**

```json
{
  "name": "iPhone 15 Pro",
  "price": 1299.99,
  "description": "Apple smartphone Pro model",
  "category": "electronics",
  "imageUrl": "https://example.com/images/iphone15pro.jpg"
}
```

**Response (200 OK)**

```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "price": 1299.99,
  "description": "Apple smartphone Pro model",
  "category": {
    "id": 1,
    "name": "electronics"
  },
  "imageUrl": "https://example.com/images/iphone15pro.jpg",
  "isDeleted": false,
  "createdAt": "2026-05-05T10:30:00.000Z",
  "lastUpdatedAt": "2026-05-05T11:00:00.000Z"
}
```

**Error Response (404 Not Found)**

```json
{
  "message": "Product with id 999 not found"
}
```

**Error Response (400 Bad Request)**

```json
{
  "message": "Product price must be a positive value"
}
```

**cURL Example**

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "price": 1299.99,
    "description": "Apple smartphone Pro model",
    "category": "electronics",
    "imageUrl": "https://example.com/images/iphone15pro.jpg"
  }'
```

---

### 5. Delete Product

**Request**

```http
DELETE /products/{id} HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | Product ID (path parameter) |

**Response (200 OK)**

```json
"Product deleted successfully"
```

**Error Response (404 Not Found)**

```json
{
  "message": "Product with id 999 not found"
}
```

**Error Response (400 Bad Request)**

```json
{
  "message": "Product id is required for deletion"
}
```

**cURL Example**

```bash
curl -X DELETE http://localhost:8080/products/1 \
  -H "Content-Type: application/json"
```

---

### 6. Get Products by Category

**Request**

```http
GET /products/category/{category} HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `category` | String | Category name (path parameter) |

**Response (200 OK)**

```json
[
  {
    "id": 1,
    "name": "iPhone 15",
    "price": 1099.99,
    "description": "Apple smartphone",
    "category": {
      "id": 1,
      "name": "electronics"
    },
    "imageUrl": "https://example.com/images/iphone15.jpg",
    "isDeleted": false,
    "createdAt": "2026-05-05T10:30:00.000Z",
    "lastUpdatedAt": "2026-05-05T10:30:00.000Z"
  }
]
```

**Error Response (404 Not Found)**

```json
{
  "message": "Category 'nonexistent' not found"
}
```

**Error Response (400 Bad Request)**

```json
{
  "message": "Category name is required"
}
```

**cURL Example**

```bash
curl -X GET http://localhost:8080/products/category/electronics \
  -H "Content-Type: application/json"
```

---

## Request/Response Models

### ProductRequestDto

Used for creating and updating products.

```json
{
  "name": "string (required, non-blank)",
  "price": "number (required, > 0)",
  "description": "string (optional)",
  "category": "string (required, non-blank)",
  "imageUrl": "string (optional)"
}
```

### Product Model (Response)

```json
{
  "id": "number",
  "name": "string",
  "price": "number",
  "description": "string",
  "category": {
    "id": "number",
    "name": "string"
  },
  "imageUrl": "string",
  "isDeleted": "boolean",
  "createdAt": "ISO 8601 timestamp",
  "lastUpdatedAt": "ISO 8601 timestamp"
}
```

### Category Model

```json
{
  "id": "number",
  "name": "string"
}
```

### Error Response

```json
{
  "message": "string (error description)"
}
```

---

## Error Responses

### HTTP Status Codes

| Status | Meaning | Scenario |
|--------|---------|----------|
| `200` | OK | Successful GET/PUT/DELETE operation |
| `201` | Created | Successful POST operation |
| `400` | Bad Request | Invalid input (null id, blank name, negative price, etc.) |
| `404` | Not Found | Product or category not found |
| `500` | Internal Server Error | Unexpected server-side error |

### Common Error Messages

| Error Message | Status | Cause |
|---------------|--------|-------|
| `Product id is required` | 400 | Null id in GET, PUT, or DELETE |
| `Product id is required for update` | 400 | Null id in PUT request |
| `Product id is required for deletion` | 400 | Null id in DELETE request |
| `Product with id X not found` | 404 | Product does not exist |
| `Product name is required` | 400 | Blank product name |
| `Product price must be a positive value` | 400 | Price ≤ 0 or null |
| `Product category is required` | 400 | Blank category |
| `Product data is required` | 400 | Null request body |
| `Category name is required` | 400 | Blank category parameter |
| `Category 'X' not found` | 404 | Category does not exist |

---

## Database

### Schema Overview

The application uses MySQL with the following tables:

**Products Table**
```sql
CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  price DOUBLE NOT NULL,
  description TEXT,
  image_url VARCHAR(500),
  category_id BIGINT,
  is_deleted BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP,
  last_updated_at TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES category(id)
);
```

**Categories Table**
```sql
CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE
);
```

### Soft Delete Behavior

Products are **never physically deleted** from the database. Instead, the `is_deleted` flag is set to `true`.

- `DELETE /products/{id}` sets `is_deleted = true`
- `GET /products` only returns products where `is_deleted = false`
- Soft-deleted products can be permanently removed via database administration if needed

---

## Project Structure

```
product-service/
├── src/main/java/com/furmani/productservice/
│   ├── ProductServiceApplication.java          # Spring Boot entry point
│   ├── advices/
│   │   └── ControllerAdvice.java               # Global exception handler
│   ├── configurations/
│   │   └── RestTemplateConfiguration.java      # REST client configuration
│   ├── controllers/
│   │   └── ProductController.java              # REST API endpoints
│   ├── dtos/
│   │   ├── ProductRequestDto.java              # Request DTO
│   │   └── ProductNotFoundDto.java             # Error response DTO
│   ├── exceptions/
│   │   ├── ProductNotFoundException.java       # Custom exception for 404
│   │   ├── CategoryNotFoundException.java      # Custom exception for category not found
│   │   └── InvalidProductData.java             # Custom exception for 400
│   ├── models/
│   │   ├── BaseModel.java                      # Base entity with auditing
│   │   ├── Product.java                        # Product JPA entity
│   │   └── Category.java                       # Category JPA entity
│   ├── repositories/
│   │   ├── ProductRepository.java              # Product JPA repository
│   │   └── CategoryRepository.java             # Category JPA repository
│   └── services/
│       ├── ProductService.java                 # Service interface
│       └── ProductServiceImpl.java              # Service implementation
├── src/main/resources/
│   └── application.properties                  # Application configuration
├── src/test/java/
│   └── ProductServiceApplicationTests.java     # Unit tests (if available)
├── pom.xml                                     # Maven configuration
├── mvnw & mvnw.cmd                             # Maven wrapper scripts
└── README.md                                   # This file
```

---

## API Testing with Postman

### Import the API Collection

1. Open Postman
2. Click **Import** → **Raw text**
3. Copy and paste the Postman collection JSON (see below)
4. Click **Import**

### Test Data

Use this sample data to test the API:

#### Sample Product 1: iPhone

```json
{
  "name": "iPhone 15",
  "price": 1099.99,
  "description": "Apple smartphone with 6.1-inch display",
  "category": "electronics",
  "imageUrl": "https://example.com/images/iphone15.jpg"
}
```

#### Sample Product 2: Running Shoes

```json
{
  "name": "Running Shoes X",
  "price": 89.5,
  "description": "Lightweight running shoes for marathon training",
  "category": "fashion",
  "imageUrl": "https://example.com/images/shoesx.jpg"
}
```

#### Sample Product 3: Mechanical Keyboard

```json
{
  "name": "Mechanical Keyboard Pro",
  "price": 149.0,
  "description": "RGB mechanical keyboard with cherry switches",
  "category": "electronics",
  "imageUrl": "https://example.com/images/keyboardpro.jpg"
}
```

### Recommended Test Sequence

1. **Create Product 1** → `POST /products` (iPhone)
2. **Create Product 2** → `POST /products` (Shoes)
3. **Create Product 3** → `POST /products` (Keyboard)
4. **Get All Products** → `GET /products`
5. **Get Product by ID** → `GET /products/1`
6. **Update Product** → `PUT /products/1` (with updated data)
7. **Get by Category** → `GET /products/category/electronics`
8. **Delete Product** → `DELETE /products/2`
9. **Verify Soft Delete** → `GET /products` (product 2 should be gone)

### Negative Test Cases

1. **Invalid Price (≤0)**
   - `POST /products` with `"price": 0`
   - Expected: `400 Bad Request` with message `"Product price must be a positive value"`

2. **Blank Product Name**
   - `POST /products` with `"name": "   "`
   - Expected: `400 Bad Request` with message `"Product name is required"`

3. **Missing Product ID**
   - `GET /products/999999`
   - Expected: `404 Not Found` with message `"Product with id 999999 not found"`

4. **Product Not Found for Update**
   - `PUT /products/999999` with valid data
   - Expected: `404 Not Found`

5. **Category Not Found**
   - `GET /products/category/unknown-category`
   - Expected: `404 Not Found` with message `"Category 'unknown-category' not found"`

6. **Blank Category**
   - `GET /products/category/   ` or `GET /products/category/` (with blank)
   - Expected: `400 Bad Request` with message `"Category name is required"`

---

## Validation Rules

### Product Name

- ✓ Required (non-null)
- ✓ Cannot be blank or whitespace-only
- Example: `"iPhone 15"` ✓ | `"   "` ✗

### Product Price

- ✓ Required (non-null)
- ✓ Must be positive (> 0)
- ✓ Type: Double/Number
- Example: `1099.99` ✓ | `0` ✗ | `-50` ✗

### Product Category

- ✓ Required (non-null)
- ✓ Cannot be blank or whitespace-only
- ✓ Auto-creates category if it doesn't exist
- Example: `"electronics"` ✓ | `"   "` ✗

### Product Description

- ✓ Optional (can be null or blank)
- Example: `"Apple smartphone"` ✓ | `null` ✓ | `""` ✓

### Product ImageUrl

- ✓ Optional (can be null or blank)
- Example: `"https://example.com/image.jpg"` ✓ | `null` ✓

---

## Exception Handling

The service uses a global exception handler (`ControllerAdvice`) to map exceptions to appropriate HTTP responses:

| Exception | HTTP Status | Example Response |
|-----------|-------------|------------------|
| `ProductNotFoundException` | 404 | `{ "message": "Product with id 1 not found" }` |
| `CategoryNotFoundException` | 404 | `{ "message": "Category 'unknown' not found" }` |
| `InvalidProductData` | 400 | `{ "message": "Product price must be a positive value" }` |

### Exception Types

**ProductNotFoundException**
- Thrown when a product is not found by ID
- HTTP Status: `404 Not Found`

**CategoryNotFoundException**
- Thrown when a category is not found by name
- HTTP Status: `404 Not Found`

**InvalidProductData**
- Thrown when product data fails validation
- HTTP Status: `400 Bad Request`
- Reasons: null input, blank fields, invalid price, missing required fields

---

## Development Notes

### Key Technologies

- **Spring Boot 3.x** — Web framework and dependency injection
- **Spring Data JPA** — ORM and database abstraction
- **Lombok** — Reduces boilerplate (Getter, Setter, Data annotations)
- **MySQL** — Relational database
- **Maven** — Build and dependency management

### Soft Delete Implementation

Products are never physically deleted. The `Product` entity has an `isDeleted` field that is set to `true` on deletion. All queries filter by `isDeleted = false`.

### REST Controller Design

- Base path: `/products`
- Resource-oriented design (nouns over verbs)
- Standard HTTP methods (GET, POST, PUT, DELETE)
- Consistent response format (JSON)

### Category Auto-Creation

When creating or updating a product, if the specified category doesn't exist, it is automatically created. This simplifies the API for clients.

---

## Troubleshooting

### Issue: "Connection refused" or database connection error

**Solution:**
1. Ensure MySQL is running: `mysql -u root -p`
2. Verify database exists: `SHOW DATABASES;`
3. Check `application.properties` for correct URL, username, and password

### Issue: Port 8080 already in use

**Solution:**
Change the port in `application.properties`:
```properties
server.port=8081
```

Then restart the application.

### Issue: Product not appearing after creation

**Solution:**
1. Verify the product was created with `GET /products`
2. Check MySQL logs for errors
3. Ensure `spring.jpa.hibernate.ddl-auto=update` in `application.properties`

### Issue: Soft-deleted product still visible

**Solution:**
This is expected behavior if you directly query the database. Use the API endpoints (`GET /products`) to see only active products.

---

## License

Copyright © 2026. All rights reserved.

---

## Contact & Support

For issues, questions, or feature requests, contact the development team or file an issue in the repository.

**Author:** Furmani Arif  
**Email:** furmani.arif@example.com  
**Last Updated:** May 5, 2026

---

## Changelog

### Version 1.0.0 (May 5, 2026)

- ✅ Initial release
- ✅ Product CRUD operations
- ✅ Category management with auto-creation
- ✅ Soft-delete functionality
- ✅ Global exception handling
- ✅ Input validation
- ✅ Comprehensive error responses

---

## Future Enhancements

- 🔄 JWT-based authentication
- 🔄 Pagination for product listing
- 🔄 Product filtering and sorting
- 🔄 API rate limiting
- 🔄 Logging and monitoring
- 🔄 Unit and integration tests
- 🔄 API versioning
- 🔄 Product reviews and ratings

