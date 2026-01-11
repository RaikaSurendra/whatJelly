#!/bin/bash

# Jelly Web Application - Run Script

echo "========================================"
echo "Jelly Web Application"
echo "========================================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven and try again"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 11+ and try again"
    exit 1
fi

echo "✓ Maven found: $(mvn -version | head -n 1)"
echo "✓ Java found: $(java -version 2>&1 | head -n 1)"
echo ""

# Clean and compile
echo "Building project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo ""
echo "✓ Build successful"
echo ""

# Run the embedded server
echo "Starting Jetty server..."
echo ""
mvn exec:java -Dexec.mainClass="com.example.webapp.EmbeddedServer"
