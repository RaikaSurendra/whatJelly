# Docker Deployment Guide

Complete guide for running the Jelly Web Application in Docker containers.

## 📦 Quick Start with Docker

### Prerequisites

- Docker installed ([Get Docker](https://docs.docker.com/get-docker/))
- Docker Compose installed (included with Docker Desktop)

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

Access the application at: **http://localhost:8080**

### Option 2: Using Docker CLI

```bash
# Build the image
docker build -t jelly-webapp:latest .

# Run the container
docker run -d \
  --name jelly-webapp \
  -p 8080:8080 \
  jelly-webapp:latest

# View logs
docker logs -f jelly-webapp

# Stop and remove
docker stop jelly-webapp
docker rm jelly-webapp
```

---

## 🏗️ Docker Architecture

### Multi-Stage Build

The Dockerfile uses a multi-stage build for optimization:

1. **Builder Stage** (maven:3.9-eclipse-temurin-11)
   - Downloads dependencies
   - Compiles Java code
   - Packages the application
   
2. **Runtime Stage** (eclipse-temurin:11-jre-alpine)
   - Smaller base image (Alpine Linux)
   - Only includes runtime dependencies
   - Runs the application

### Benefits

- **Smaller image size** - Only runtime dependencies included
- **Better security** - Minimal attack surface
- **Faster deployments** - Less data to transfer
- **Layer caching** - Dependencies cached separately from code

---

## 🔧 Configuration

### Environment Variables

Configure the application using environment variables:

```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m    # JVM memory settings
  - SERVER_PORT=8080                # Application port
```

### Custom Port

Run on a different port:

```bash
docker run -d \
  -p 9090:8080 \
  --name jelly-webapp \
  jelly-webapp:latest
```

Access at: http://localhost:9090

### Memory Limits

Set container memory limits:

```bash
docker run -d \
  --memory="1g" \
  --memory-swap="1g" \
  -p 8080:8080 \
  jelly-webapp:latest
```

---

## 🔄 Development Mode

### Live Editing with Volume Mounts

Edit Jelly pages without rebuilding:

```yaml
# In docker-compose.yml
volumes:
  - ./src/main/webapp/pages:/app/webapp/pages
```

Or with Docker CLI:

```bash
docker run -d \
  -p 8080:8080 \
  -v $(pwd)/src/main/webapp/pages:/app/webapp/pages \
  jelly-webapp:latest
```

Now you can edit `.jelly` files and refresh the browser!

### Development Compose File

Create `docker-compose.dev.yml`:

```yaml
version: '3.8'

services:
  jelly-webapp:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    volumes:
      - ./src/main/webapp/pages:/app/webapp/pages
      - ./logs:/app/logs
    environment:
      - JAVA_OPTS=-Xmx512m -Xms256m -Ddebug=true
```

Run with:

```bash
docker-compose -f docker-compose.dev.yml up
```

---

## 📊 Monitoring

### View Logs

```bash
# Follow logs
docker logs -f jelly-webapp

# Last 100 lines
docker logs --tail 100 jelly-webapp

# With timestamps
docker logs -t jelly-webapp
```

### Health Check

The container includes a health check:

```bash
# Check container health
docker inspect --format='{{.State.Health.Status}}' jelly-webapp

# View health check logs
docker inspect --format='{{range .State.Health.Log}}{{.Output}}{{end}}' jelly-webapp
```

### Resource Usage

```bash
# Real-time stats
docker stats jelly-webapp

# One-time snapshot
docker stats --no-stream jelly-webapp
```

---

## 🚀 Production Deployment

### Best Practices

1. **Use specific tags** (not `latest`)

```bash
docker build -t jelly-webapp:1.0.0 .
docker tag jelly-webapp:1.0.0 jelly-webapp:latest
```

2. **Set resource limits**

```yaml
services:
  jelly-webapp:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

3. **Use restart policies**

```yaml
restart: unless-stopped
```

4. **Enable health checks**

Already included in Dockerfile and docker-compose.yml

5. **Use secrets for sensitive data**

```bash
echo "db_password" | docker secret create db_pass -
```

### With Nginx Reverse Proxy

Create `nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    upstream jelly-app {
        server jelly-webapp:8080;
    }

    server {
        listen 80;
        server_name localhost;

        location / {
            proxy_pass http://jelly-app;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

Update `docker-compose.yml`:

```yaml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - jelly-webapp
    networks:
      - jelly-network

  jelly-webapp:
    # ... existing config ...
    expose:
      - "8080"  # Not published, only accessible via nginx
```

---

## 🔍 Troubleshooting

### Container Won't Start

```bash
# Check logs
docker logs jelly-webapp

# Check events
docker events --filter container=jelly-webapp

# Inspect container
docker inspect jelly-webapp
```

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Or use a different port
docker run -d -p 9090:8080 jelly-webapp:latest
```

### Out of Memory

```bash
# Increase memory limit
docker run -d --memory="2g" -p 8080:8080 jelly-webapp:latest

# Or adjust JAVA_OPTS
docker run -d \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  -p 8080:8080 \
  jelly-webapp:latest
```

### Application Not Responding

```bash
# Enter container
docker exec -it jelly-webapp sh

# Check Java process
ps aux | grep java

# Check listening ports
netstat -tuln | grep 8080
```

### Rebuild Without Cache

```bash
# Docker Compose
docker-compose build --no-cache

# Docker CLI
docker build --no-cache -t jelly-webapp:latest .
```

---

## 🧹 Cleanup

### Remove Containers

```bash
# Stop and remove
docker-compose down

# Remove with volumes
docker-compose down -v
```

### Remove Images

```bash
# Remove specific image
docker rmi jelly-webapp:latest

# Remove all unused images
docker image prune -a
```

### Complete Cleanup

```bash
# Stop all containers
docker stop $(docker ps -aq)

# Remove all containers
docker rm $(docker ps -aq)

# Remove all images
docker rmi $(docker images -q)

# Remove all volumes
docker volume prune

# Remove all networks
docker network prune
```

---

## 📦 Registry Integration

### Push to Docker Hub

```bash
# Login
docker login

# Tag image
docker tag jelly-webapp:latest yourusername/jelly-webapp:latest

# Push
docker push yourusername/jelly-webapp:latest
```

### Pull and Run

```bash
docker pull yourusername/jelly-webapp:latest
docker run -d -p 8080:8080 yourusername/jelly-webapp:latest
```

### Private Registry

```bash
# Tag for private registry
docker tag jelly-webapp:latest registry.example.com/jelly-webapp:latest

# Push
docker push registry.example.com/jelly-webapp:latest
```

---

## 🔐 Security

### Best Practices

1. **Run as non-root user**

```dockerfile
# Add to Dockerfile
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser
USER appuser
```

2. **Scan for vulnerabilities**

```bash
docker scan jelly-webapp:latest
```

3. **Use official base images**

Already using `eclipse-temurin:11-jre-alpine`

4. **Keep images updated**

```bash
docker pull eclipse-temurin:11-jre-alpine
docker build -t jelly-webapp:latest .
```

5. **Limit capabilities**

```yaml
security_opt:
  - no-new-privileges:true
cap_drop:
  - ALL
```

---

## 📈 Scaling

### Multiple Instances

```yaml
services:
  jelly-webapp:
    # ... config ...
    deploy:
      replicas: 3
```

Run with:

```bash
docker-compose up --scale jelly-webapp=3
```

### Load Balancer

Add nginx load balancer:

```nginx
upstream jelly-cluster {
    server jelly-webapp-1:8080;
    server jelly-webapp-2:8080;
    server jelly-webapp-3:8080;
}
```

---

## 🎯 Quick Commands Reference

```bash
# Build
docker build -t jelly-webapp:latest .

# Run
docker run -d -p 8080:8080 --name jelly-webapp jelly-webapp:latest

# Logs
docker logs -f jelly-webapp

# Stop
docker stop jelly-webapp

# Start
docker start jelly-webapp

# Restart
docker restart jelly-webapp

# Remove
docker rm -f jelly-webapp

# Shell access
docker exec -it jelly-webapp sh

# Inspect
docker inspect jelly-webapp

# Stats
docker stats jelly-webapp
```

---

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Alpine Linux](https://alpinelinux.org/)

---

**Congratulations!** Your Jelly Web Application is now containerized and ready for deployment anywhere Docker runs! 🎉
