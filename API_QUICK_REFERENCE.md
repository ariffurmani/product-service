# Product Service - API Quick Reference

## Base URL
```
http://localhost:8080
```

## Content-Type
```
application/json
```

---

## Endpoints Summary

| Method | Endpoint | Status | Description |
|--------|----------|--------|-------------|
| `GET` | `/products` | 200 | Get all active products |
| `GET` | `/products/{id}` | 200, 404, 400 | Get product by ID |
| `POST` | `/products` | 201, 400 | Create new product |
| `PUT` | `/products/{id}` | 200, 404, 400 | Update product |
| `DELETE` | `/products/{id}` | 200, 404, 400 | Delete (soft) product |
| `GET` | `/products/category/{category}` | 200, 404, 400 | Get products by category |

---

## Request Body Model (ProductRequestDto)

```json
{
  "name": "string (required, non-blank)",
  "price": "number (required, > 0)",
  "description": "string (optional)",
  "category": "string (required, non-blank)",
  "imageUrl": "string (optional)"
}
```

---

## HTTP Status Codes

| Code | Meaning | When |
|------|---------|------|
| `200` | OK | Successful GET/PUT/DELETE |
| `201` | Created | Successful POST |
| `400` | Bad Request | Invalid input (validation failed) |
| `404` | Not Found | Product/category not found |
| `500` | Server Error | Unexpected error |

---

## Common Error Messages

### 400 Bad Request Errors

| Message | Cause |
|---------|-------|
| `Product id is required` | Null ID in GET request |
| `Product id is required for update` | Null ID in PUT request |
| `Product id is required for deletion` | Null ID in DELETE request |
| `Product name is required` | Blank/null product name |
| `Product price must be a positive value` | Price ≤ 0 or null |
| `Product category is required` | Blank/null category |
| `Product data is required` | Null request body |
| `Category name is required` | Blank category parameter |

### 404 Not Found Errors

| Message | Cause |
|---------|-------|
| `Product with id X not found` | Product ID doesn't exist |
| `Category 'X' not found` | Category doesn't exist |

---

## Example Requests

### 1. Create a Product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15",
    "price": 1099.99,
    "description": "Apple smartphone",
    "category": "electronics",
    "imageUrl": "https://example.com/iphone15.jpg"
  }'
```

**Response (201 Created):**
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
  "imageUrl": "https://example.com/iphone15.jpg",
  "isDeleted": false,
  "createdAt": "2026-05-05T10:30:00.000Z",
  "lastUpdatedAt": "2026-05-05T10:30:00.000Z"
}
```

---

### 2. Get All Products

```bash
curl -X GET http://localhost:8080/products \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
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
    "imageUrl": "https://example.com/iphone15.jpg",
    "isDeleted": false,
    "createdAt": "2026-05-05T10:30:00.000Z",
    "lastUpdatedAt": "2026-05-05T10:30:00.000Z"
  }
]
```

---

### 3. Get Product by ID

```bash
curl -X GET http://localhost:8080/products/1 \
  -H "Content-Type: application/json"
```

**Response (200 OK):** [Same as above]

**Error (404 Not Found):**
```json
{
  "message": "Product with id 1 not found"
}
```

---

### 4. Update Product

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "price": 1299.99,
    "description": "Apple smartphone Pro",
    "category": "electronics",
    "imageUrl": "https://example.com/iphone15-pro.jpg"
  }'
```

**Response (200 OK):** Updated product object

---

### 5. Delete Product (Soft Delete)

```bash
curl -X DELETE http://localhost:8080/products/1 \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
"Product deleted successfully"
```

---

### 6. Get Products by Category

```bash
curl -X GET http://localhost:8080/products/category/electronics \
  -H "Content-Type: application/json"
```

**Response (200 OK):** Array of products in that category

**Error (404 Not Found):**
```json
{
  "message": "Category 'electronics' not found"
}
```

---

## Validation Rules Quick Check

### Product Name
- ✓ Required
- ✓ Cannot be blank or whitespace
- Example: `"iPhone 15"` ✓

### Price
- ✓ Required
- ✓ Must be > 0
- Example: `1099.99` ✓ | `0` ✗ | `-50` ✗

### Category
- ✓ Required
- ✓ Cannot be blank or whitespace
- ✓ Auto-creates if doesn't exist
- Example: `"electronics"` ✓

### Description & ImageUrl
- ✓ Optional (can be null or empty)

---

## Testing Checklist

### Happy Path 🟢

- [ ] Create product 1 (electronics)
- [ ] Create product 2 (fashion)
- [ ] Get all products → Should see 2 products
- [ ] Get product by ID → Should return correct product
- [ ] Update product → Should reflect changes
- [ ] Get by category → Should filter correctly
- [ ] Delete product → Should soft-delete
- [ ] Get all products → Deleted product should not appear

### Error Cases 🔴

- [ ] Create with price = 0 → 400 Bad Request
- [ ] Create with blank name → 400 Bad Request
- [ ] Create with blank category → 400 Bad Request
- [ ] Get non-existent product → 404 Not Found
- [ ] Get non-existent category → 404 Not Found
- [ ] Delete non-existent product → 404 Not Found
- [ ] Update non-existent product → 404 Not Found

---

## Environment Variables (for Postman)

```
baseUrl = http://localhost:8080
product_id_1 = <auto-populated after first create>
product_id_2 = <auto-populated after second create>
product_id_3 = <auto-populated after third create>
```

---

## Soft Delete Behavior

**Important:** Deleted products are NOT physically removed from the database.

- `DELETE /products/{id}` → Sets `isDeleted = true`
- `GET /products` → Only returns products where `isDeleted = false`
- `GET /products/{id}` → Returns 404 if product is soft-deleted
- Direct database query → Will show deleted products (this is intentional)

---

## Tips & Tricks

### 1. Postman Collection Import
```
1. Open Postman
2. Click "Import"
3. Select "POSTMAN_COLLECTION.json"
4. Variables auto-populate from response IDs
```

### 2. Test Request Template (Postman)
```
Use {{baseUrl}} instead of http://localhost:8080
Use {{product_id_1}} for dynamic ID references
```

### 3. Quick Test All Endpoints
```
1. Import the Postman collection
2. Select "Run Collection"
3. All requests execute in sequence
4. View test results in the Summary tab
```

### 4. Database Investigation
```bash
# Connect to MySQL
mysql -u root -p ecom-backend

# Check products
SELECT * FROM product;

# Check categories
SELECT * FROM category;

# View soft-deleted products
SELECT * FROM product WHERE is_deleted = true;
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused to localhost:8080 | Ensure service is running on that port |
| MySQL connection error | Check username/password in application.properties |
| Database not found | Run `CREATE DATABASE ecom-backend;` in MySQL |
| Port 8080 already in use | Change port: `server.port=8081` in properties |
| Product not appearing | Verify response status is 201 (Created) |

---

## Sample Test Data

### iPhone 15
```json
{
  "name": "iPhone 15",
  "price": 1099.99,
  "description": "Apple smartphone with 6.1-inch display",
  "category": "electronics",
  "imageUrl": "https://example.com/iphone15.jpg"
}
```

### Running Shoes
```json
{
  "name": "Running Shoes X",
  "price": 89.5,
  "description": "Lightweight running shoes",
  "category": "fashion",
  "imageUrl": "https://example.com/shoes.jpg"
}
```

### Mechanical Keyboard
```json
{
  "name": "Mechanical Keyboard Pro",
  "price": 149.0,
  "description": "RGB mechanical keyboard",
  "category": "electronics",
  "imageUrl": "https://example.com/keyboard.jpg"
}
```

---

## Documentation Links

- 📖 **Full API Documentation** → See `README.md`
- 🚀 **Quick Start** → Run `./QUICK_START.sh`
- 📬 **Postman Collection** → Import `POSTMAN_COLLECTION.json`
- 📋 **This File** → `API_QUICK_REFERENCE.md`

---

**Last Updated:** May 5, 2026  
**Version:** 1.0.0

