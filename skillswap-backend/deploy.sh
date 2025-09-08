#!/bin/bash

# SkillSwap Backend - Production Deployment Script
# Este script prepara y despliega el backend en producción

set -e  # Exit on any error

echo "🚀 SkillSwap Backend - Production Deployment"
echo "============================================="

# Variables
APP_NAME="skillswap-backend"
DOCKER_IMAGE="skillswap/backend"
VERSION=${1:-"latest"}
ENV_FILE=".env"

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
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    # Check if Docker Compose is installed
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    # Check if .env file exists
    if [ ! -f "$ENV_FILE" ]; then
        print_warning ".env file not found. Creating from template..."
        cp .env.example .env
        print_warning "Please edit .env file with your production values before continuing."
        read -p "Press Enter to continue after editing .env file..."
    fi
    
    print_status "Prerequisites check completed ✅"
}

# Build application
build_application() {
    print_status "Building application..."
    
    # Clean and build with Maven
    if command -v ./mvnw &> /dev/null; then
        ./mvnw clean package -DskipTests
    elif command -v mvn &> /dev/null; then
        mvn clean package -DskipTests
    else
        print_error "Maven not found. Please install Maven or use the Maven wrapper."
        exit 1
    fi
    
    print_status "Application built successfully ✅"
}

# Build Docker image
build_docker_image() {
    print_status "Building Docker image..."
    
    docker build -t $DOCKER_IMAGE:$VERSION .
    docker tag $DOCKER_IMAGE:$VERSION $DOCKER_IMAGE:latest
    
    print_status "Docker image built successfully ✅"
}

# Run database migrations
run_migrations() {
    print_status "Running database migrations..."
    
    # Start only database for migrations
    docker-compose -f docker-compose.prod.yml up -d postgres redis
    
    # Wait for database to be ready
    sleep 10
    
    # Run Flyway migrations
    if command -v ./mvnw &> /dev/null; then
        ./mvnw flyway:migrate -Dspring.profiles.active=prod
    else
        mvn flyway:migrate -Dspring.profiles.active=prod
    fi
    
    print_status "Database migrations completed ✅"
}

# Deploy application
deploy_application() {
    print_status "Deploying application..."
    
    # Stop existing containers
    docker-compose -f docker-compose.prod.yml down
    
    # Start all services
    docker-compose -f docker-compose.prod.yml up -d
    
    print_status "Application deployed successfully ✅"
}

# Health check
health_check() {
    print_status "Performing health check..."
    
    # Wait for application to start
    sleep 30
    
    # Check health endpoint
    for i in {1..5}; do
        if curl -f http://localhost:8080/api/health &> /dev/null; then
            print_status "Health check passed ✅"
            return 0
        fi
        print_warning "Health check attempt $i failed, retrying..."
        sleep 10
    done
    
    print_error "Health check failed after 5 attempts"
    return 1
}

# Show application status
show_status() {
    print_status "Application Status:"
    echo "==================="
    
    docker-compose -f docker-compose.prod.yml ps
    
    echo ""
    print_status "Application URLs:"
    echo "• API: http://localhost:8080"
    echo "• Health: http://localhost:8080/api/health"
    echo "• Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "• Actuator: http://localhost:8080/actuator"
    
    echo ""
    print_status "Logs command: docker-compose -f docker-compose.prod.yml logs -f"
}

# Cleanup function
cleanup() {
    print_status "Cleaning up..."
    docker system prune -f
}

# Main deployment flow
main() {
    echo "Starting deployment process..."
    
    check_prerequisites
    build_application
    build_docker_image
    run_migrations
    deploy_application
    
    if health_check; then
        show_status
        print_status "🎉 Deployment completed successfully!"
    else
        print_error "Deployment failed during health check"
        print_status "Checking logs..."
        docker-compose -f docker-compose.prod.yml logs --tail=50
        exit 1
    fi
}

# Handle script arguments
case "${1:-deploy}" in
    "deploy")
        main
        ;;
    "build")
        check_prerequisites
        build_application
        build_docker_image
        ;;
    "migrate")
        run_migrations
        ;;
    "health")
        health_check
        ;;
    "status")
        show_status
        ;;
    "logs")
        docker-compose -f docker-compose.prod.yml logs -f
        ;;
    "stop")
        docker-compose -f docker-compose.prod.yml down
        ;;
    "cleanup")
        cleanup
        ;;
    *)
        echo "Usage: $0 {deploy|build|migrate|health|status|logs|stop|cleanup}"
        echo ""
        echo "Commands:"
        echo "  deploy   - Full deployment (default)"
        echo "  build    - Build application and Docker image only"
        echo "  migrate  - Run database migrations only"
        echo "  health   - Check application health"
        echo "  status   - Show application status"
        echo "  logs     - Show application logs"
        echo "  stop     - Stop all services"
        echo "  cleanup  - Clean up Docker resources"
        exit 1
        ;;
esac
