# Use OpenJDK 17 with Maven
FROM maven:3.9.4-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Create app user
RUN groupadd -r skillswap && useradd -r -g skillswap skillswap

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p logs && chown skillswap:skillswap logs

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown skillswap:skillswap app.jar

# Switch to app user
USER skillswap

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
