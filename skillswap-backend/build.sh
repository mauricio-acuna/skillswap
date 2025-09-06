#!/bin/bash

# SkillSwap Backend Build Script
# Uses Docker to build the project when Maven is not available locally

set -e

echo "🚀 SkillSwap Backend Build Script"
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    exit 1
fi

# Default action
ACTION=${1:-compile}

case $ACTION in
    "clean")
        print_status "Cleaning project..."
        docker run --rm -v "$(pwd)":/app -w /app maven:3.9.4-openjdk-17-slim mvn clean
        ;;
    "compile")
        print_status "Compiling project..."
        docker run --rm -v "$(pwd)":/app -w /app maven:3.9.4-openjdk-17-slim mvn clean compile
        ;;
    "test")
        print_status "Running tests..."
        docker run --rm -v "$(pwd)":/app -w /app maven:3.9.4-openjdk-17-slim mvn test
        ;;
    "package")
        print_status "Packaging application..."
        docker run --rm -v "$(pwd)":/app -w /app maven:3.9.4-openjdk-17-slim mvn clean package -DskipTests
        ;;
    "install")
        print_status "Installing dependencies..."
        docker run --rm -v "$(pwd)":/app -w /app maven:3.9.4-openjdk-17-slim mvn dependency:resolve
        ;;
    "run")
        print_status "Starting development environment..."
        docker-compose up --build
        ;;
    "run-bg")
        print_status "Starting development environment in background..."
        docker-compose up --build -d
        ;;
    "stop")
        print_status "Stopping development environment..."
        docker-compose down
        ;;
    "logs")
        print_status "Showing application logs..."
        docker-compose logs -f skillswap-backend
        ;;
    "help"|*)
        echo "Usage: $0 [ACTION]"
        echo ""
        echo "Actions:"
        echo "  clean     - Clean the project"
        echo "  compile   - Compile the project"
        echo "  test      - Run tests"
        echo "  package   - Package the application"
        echo "  install   - Install dependencies"
        echo "  run       - Start development environment"
        echo "  run-bg    - Start development environment in background"
        echo "  stop      - Stop development environment"
        echo "  logs      - Show application logs"
        echo "  help      - Show this help message"
        ;;
esac

print_status "Operation completed!"
