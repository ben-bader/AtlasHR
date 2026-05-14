# HRMS Spring Cloud Gateway

A high-performance API Gateway for the HRMS microservices architecture, built with Spring Cloud Gateway, Spring WebFlux, and Spring Security.

## 🎯 Features

- **Reactive Architecture**: Non-blocking request processing with Project Reactor
- **JWT Authentication**: HS256 token validation with same secret as auth-service
- **Service Routing**: Routes requests to multiple backend services
- **Rate Limiting**: Redis-backed rate limiting per user
- **CORS Support**: Pre-configured for Next.js frontend on :3000 and :3001
- **Global Exception Handling**: Consistent JSON error responses
- **Security Headers**: Spring Security WebFlux with CSRF protection
- **Health Checks**: Spring Boot Actuator with liveness/readiness probes
- **Structured Logging**: SLF4J with configurable log levels
- **Docker Ready**: Multi-stage Dockerfile and docker-compose for containerization

## 📋 Prerequisites

- Java 21 (JDK or JRE)
- Maven 3.8+
- Running backend microservices (auth-service, employee-service, etc.)
- Redis (for rate limiting)
- Docker & Docker Compose (optional, for containerization)

## 🚀 Quick Start

### Local Development

1. **Clone repository** (already done in monorepo)

2. **Configure environment**:
   ```bash
   cd apps/gateway-service
   cp .env.example .env
   ```

   Update `.env` with your configuration:
   ```env
   SERVER_PORT=8080
   JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
   FRONTEND_URL=http://localhost:3000
   ```

3. **Start Redis** (required for rate limiting):
   ```bash
   # Using Docker
   docker run -d -p 6379:6379 redis:7-alpine
   
   # Or use local installation
   redis-server
   ```

4. **Build and run**:
   ```bash
   # Build
   ./mvnw clean package

   # Run
   ./mvnw spring-boot:run

   # With environment variables
   export JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
   ./mvnw spring-boot:run
   ```

   Gateway will be available at `http://localhost:8080`

5. **Verify health**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

## 🐳 Docker Setup

### Quick Start (Standalone)

```bash
# Start gateway service with Redis
cd apps/gateway-service
docker-compose up -d

# View logs
docker-compose logs -f gateway-service

# Stop services
docker-compose down
```

### Using Shared Infrastructure

To run with the full HRMS infrastructure (postgres, rabbitmq, other services):

```bash
cd infrastructure/docker

# Create shared network first (if not exists)
docker network create hrms-network

# Start shared services
docker-compose up -d

# Start gateway in separate location
cd apps/gateway-service
docker-compose up -d
```

### Build Docker Image Only

```bash
docker build -t hrms-gateway-service:latest .
```

## 🔌 API Routes

### Public Routes (No Authentication Required)

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /health` - Gateway health status
- `GET /actuator/health` - Detailed health information

### Protected Routes

All other routes require valid JWT in `Authorization: Bearer <token>` header.

#### Service Routes

| Path | Service | Port |
|------|---------|------|
| `/api/auth/**` | Auth Service | 8081 |
| `/api/users/**` | User Service | 8082 |
| `/api/hr/**` | Employee Service | 8083 |
| `/api/payroll/**` | Payroll Service | 8084 |

### Request Headers

Authenticated requests include these headers (injected by gateway):

- `X-User-Id` - User ID from JWT claims
- `X-Username` - Username from JWT claims
- `X-User-Roles` - Comma-separated roles from JWT claims

## 🔐 Security Architecture

### JWT Flow

1. **Client authenticates** with `/api/auth/login`
2. **Auth Service** validates credentials and issues JWT
3. **Client** stores JWT and RefreshToken
4. **Client** sends requests with `Authorization: Bearer <token>`
5. **Gateway** validates JWT (HS256 signature)
6. **Gateway** injects user headers and routes to service
7. **Service** receives authenticated request with user context

### Key Security Principles

- **Gateway is the security boundary** - all requests validated here
- **Shared JWT secret** - same secret as auth-service for compatibility
- **Public routes bypass** - /auth/* routes don't require JWT
- **HS256 Algorithm** - HMAC with SHA-256 for performance
- **Token expiration** - configurable via JWT_EXPIRATION env var

### Rate Limiting

- **Storage**: Redis
- **Key**: `rate_limit:{userId}`
- **Window**: 1 minute
- **Default limit**: 100 requests per minute per user
- **Disable**: Set `RATE_LIMIT_ENABLED=false`

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | Gateway server port |
| `JWT_SECRET` | (required) | JWT signing secret (same as auth-service) |
| `JWT_EXPIRATION` | 86400000 | Token expiration in milliseconds (24h) |
| `JWT_REFRESH_EXPIRATION` | 604800000 | Refresh token expiration (7d) |
| `REDIS_HOST` | localhost | Redis host |
| `REDIS_PORT` | 6379 | Redis port |
| `RATE_LIMIT_ENABLED` | true | Enable rate limiting |
| `RATE_LIMIT_REQUESTS_PER_MINUTE` | 100 | Max requests per minute per user |
| `FRONTEND_URL` | localhost:3000 | Comma-separated CORS origins |
| `LOG_LEVEL` | INFO | Root log level |
| `GATEWAY_LOG_LEVEL` | INFO | Gateway-specific log level |
| `SPRING_LOG_LEVEL` | WARN | Spring framework log level |

### application.yml

Main configuration file at `src/main/resources/application.yml`. Overridable via:
- Environment variables (highest priority)
- .env file (via IDE integration)
- System properties
- application.yml defaults

## 📊 Health Checks & Metrics

### Health Endpoints

```bash
# Simple health check
curl http://localhost:8080/actuator/health

# Detailed health with components
curl http://localhost:8080/actuator/health?show-details=when-authorized

# Liveness probe (Kubernetes)
curl http://localhost:8080/actuator/health/liveness

# Readiness probe (Kubernetes)
curl http://localhost:8080/actuator/health/readiness
```

### Metrics

```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

## 🧪 Testing

### Test Authentication Flow

```bash
# 1. Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123!",
    "email": "test@example.com"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123!"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "userId": "550e8400-e29b-41d4-a716-446655440000",
#   "username": "testuser"
# }

# 3. Use token in request
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X GET http://localhost:8080/api/hr/employees \
  -H "Authorization: Bearer $TOKEN"

# 4. Refresh token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "..."}'
```

### Test Rate Limiting

```bash
# Make 100+ requests quickly
for i in {1..105}; do
  curl -X GET http://localhost:8080/api/auth/login \
    -H "Authorization: Bearer $TOKEN"
done

# After 100 requests, should see rate limit errors
```

## 🐛 Troubleshooting

### JWT Validation Failed

**Error**: `{"status": 401, "error": "Unauthorized", "message": "Invalid or expired JWT token"}`

**Solutions**:
1. Verify `JWT_SECRET` matches auth-service secret
2. Check token is not expired (use online JWT decoder)
3. Ensure token format is `Authorization: Bearer <token>` (space required)

### Redis Connection Failed

**Error**: `Redis server refused connection`

**Solutions**:
1. Verify Redis is running: `redis-cli ping` (should reply PONG)
2. Check Redis host/port: `REDIS_HOST=localhost REDIS_PORT=6379`
3. Disable rate limiting if Redis unavailable: `RATE_LIMIT_ENABLED=false`

### Service Not Reachable

**Error**: `{"error": "Service Unavailable"}`

**Solutions**:
1. Verify downstream service is running
2. Check service URL in `GatewayConfig.java`
3. Verify Docker network: `docker network ls` (should show `hrms-network`)
4. Check container hostname resolution: `docker exec hrms-gateway-service nslookup hrms-auth-service`

### CORS Errors in Frontend

**Error**: `Access to XMLHttpRequest blocked by CORS policy`

**Solutions**:
1. Verify `FRONTEND_URL` includes your frontend origin
2. Check request has `Origin` header (browsers add automatically)
3. Verify credentials are allowed: `allowCredentials: true`

## 📚 Architecture

### Request Flow

```
┌─────────────────┐
│  Next.js Client │
└────────┬────────┘
         │ HTTP + JWT Bearer Token
         ▼
┌──────────────────────────────────────────┐
│     Spring Cloud Gateway (8080)          │
│  ✓ CORS validation                       │
│  ✓ JWT token validation (HS256)         │
│  ✓ Rate limiting (Redis)                │
│  ✓ User header injection                │
│  ✓ Route to service                     │
└──────┬──────────────────────────┬────────┘
       │                          │
       ▼ /api/auth               ▼ /api/hr
   ┌─────────────┐          ┌──────────────┐
   │ Auth Service│          │ Employee     │
   │  (8081)     │          │ Service      │
   └─────────────┘          │ (8083)       │
                            └──────────────┘
```

## 🚢 Production Deployment

### Docker Image

The provided Dockerfile implements:
- Multi-stage build for smaller image size
- Alpine Linux base for minimal footprint
- Non-root user execution (uid: 1000)
- Health checks for orchestration
- Proper signal handling for graceful shutdown

### Kubernetes Integration

Supports Kubernetes probes:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 40
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 5
```

### Environment Variables for Production

```env
JWT_SECRET=<strong-64-char-secret-from-vault>
FRONTEND_URL=https://app.example.com
REDIS_HOST=redis-cluster.internal
REDIS_PORT=6379
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=100
LOG_LEVEL=INFO
```

## 📝 Logging

### Log Locations

- **Console**: Real-time output (development)
- **File**: `logs/gateway.log` (rolling, max 10MB)

### Log Levels

- `DEBUG`: Detailed JWT validation, request routing
- `INFO`: Request start/end, configuration initialization
- `WARN`: Authentication failures, service unavailability
- `ERROR`: Unhandled exceptions, critical failures

### Structured Logging Example

```
2024-05-09 14:23:45 - JWT validated for user: john.doe (userId: 550e8400-e29b-41d4-a716-446655440000)
2024-05-09 14:23:45 - Route to service: auth-service (http://hrms-auth-service:8081/api/auth/login)
2024-05-09 14:23:46 - Request completed: status=200, duration=234ms
```

## 🔄 Service Migration from Node.js Express

This gateway replaces the previous Node.js + Express gateway with equivalent functionality:

| Feature | Node.js Express | Spring Cloud Gateway |
|---------|-----------------|---------------------|
| Framework | Express.js | Spring Cloud Gateway + WebFlux |
| Routing | Express Router | Spring Cloud Gateway Routes |
| JWT Validation | jsonwebtoken | JJWT |
| Rate Limiting | express-rate-limit | Redis + Custom Filter |
| CORS | cors middleware | Spring Security CORS |
| Error Handling | Express error handler | Global Exception Handler |
| Health Checks | Custom endpoint | Spring Boot Actuator |
| Performance | Blocking I/O | Reactive (non-blocking) |

## 🤝 Contributing

1. Follow Spring Boot conventions
2. Use reactive APIs (Mono/Flux) instead of blocking
3. Add comprehensive error handling
4. Document configuration changes
5. Test with `mvn test`

## 📄 License

MIT - See LICENSE file

## 📧 Support

For issues or questions:
1. Check troubleshooting section
2. Review logs: `docker logs hrms-gateway-service`
3. Check gateway health: `curl http://localhost:8080/actuator/health`
4. Verify downstream service: `curl http://localhost:8081/health`
