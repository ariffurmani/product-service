#!/bin/bash

# Product Service - Quick Start Guide
# ====================================

echo "============================================"
echo "Product Service - Quick Start Script"
echo "============================================"
echo ""

# Check if Java is installed
echo "[1/4] Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi
echo "✓ Java found: $(java -version 2>&1 | head -1)"
echo ""

# Check if MySQL is running
echo "[2/4] Checking MySQL connection..."
mysql -u root -p"root" -h localhost 2>/dev/null -e "SELECT 1" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✓ MySQL is running and accessible"

    # Create database if it doesn't exist
    echo "   Creating/Verifying database 'ecom-backend'..."
    mysql -u root -p"root" -h localhost -e "CREATE DATABASE IF NOT EXISTS \`ecom-backend\`;" 2>/dev/null
    echo "   ✓ Database ready"
else
    echo "❌ Cannot connect to MySQL. Please ensure:"
    echo "   - MySQL is installed and running"
    echo "   - Default credentials (user: root, password: root) are correct"
    echo "   - Update credentials in src/main/resources/application.properties if different"
    exit 1
fi
echo ""

# Build the project
echo "[3/4] Building the project..."
echo "   Running: mvn clean install..."
mvn clean install -q

if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "❌ Build failed. Check errors above."
    exit 1
fi
echo ""

# Start the application
echo "[4/4] Starting Product Service..."
echo "✓ Service starting on http://localhost:8080"
echo ""
echo "============================================"
echo "✓ Ready for Testing!"
echo "============================================"
echo ""
echo "📋 API Endpoints:"
echo "   GET    http://localhost:8080/products"
echo "   POST   http://localhost:8080/products"
echo "   GET    http://localhost:8080/products/{id}"
echo "   PUT    http://localhost:8080/products/{id}"
echo "   DELETE http://localhost:8080/products/{id}"
echo "   GET    http://localhost:8080/products/category/{category}"
echo ""
echo "📋 Import Postman Collection:"
echo "   1. Open Postman"
echo "   2. Click 'Import'"
echo "   3. Select 'POSTMAN_COLLECTION.json' from this directory"
echo "   4. Run the collection to test all endpoints"
echo ""
echo "📖 Documentation:"
echo "   - See README.md for complete API documentation"
echo ""
echo "Press Ctrl+C to stop the service"
echo ""

# Start Spring Boot
bash ./mvnw spring-boot:run

