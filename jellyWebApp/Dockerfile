# Multi-stage build for Jelly Web Application
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copy the entire project from builder stage
COPY --from=builder /app/pom.xml ./pom.xml
COPY --from=builder /app/target ./target
COPY --from=builder /app/src/main/webapp ./src/main/webapp
COPY --from=builder /app/src/main/resources ./src/main/resources

# Install Maven and wget (needed for exec plugin and healthcheck)
RUN apt-get update && \
    apt-get install -y maven wget && \
    rm -rf /var/lib/apt/lists/*

# Expose port 8080
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application using Maven exec plugin
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.example.webapp.EmbeddedServer"]
