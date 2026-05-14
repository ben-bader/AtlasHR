# Spring Cloud Gateway Service - Implementation Guide

## Quick Links

- [README.md](README.md) - Complete documentation
- [Configuration](#configuration) - Environment setup
- [Troubleshooting](#troubleshooting) - Common issues and fixes

## What Changed?

### Old Architecture (Node.js Express)
```
gateway/ (Node.js)
├── src/
│   ├── index.js
│   ├── routes/
│   ├── middleware/
│   └── utils/
├── package.json
└── Dockerfile
```

### New Architecture (Spring Cloud Gateway)
```
apps/gateway-service/ (Java Spring Boot)
├── src/main/java/com/hrms/gateway/
│   ├── GatewayApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── GatewayConfig.java
│   │   └── RedisConfig.java
│   ├── security/
│   │   ├── JwtUtil.java
│   │   └── JwtAuthenticationFilter.java
│   ├── service/
│   │   └── RateLimitService.java
│   └── handler/
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   └── application.yml
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Key Improvements

### ✅ Reactive Architecture
- **Before**: Blocking I/O (Express.js)
- **After**: Non-blocking reactive (Spring WebFlux + Project Reactor)
- **Benefit**: Better throughput under high load

### ✅ Enterprise-Grade Security
- **Before**: Manual JWT validation
- **After**: Spring Security WebFlux with standard patterns
- **Benefit**: Regular security updates, best practices

### ✅ Rate Limiting
- **Before**: In-memory (limited to single instance)
- **After**: Redis-backed (works across instances)
- **Benefit**: Distributed rate limiting in microservices

### ✅ Error Handling
- **Before**: Custom Express error handlers
- **After**: Global exception handler with consistent JSON responses
- **Benefit**: Standardized error responses across all services

### ✅ Health Checks
- **Before**: Custom `/health` endpoint
- **After**: Spring Boot Actuator with liveness/readiness probes
- **Benefit**: Kubernetes-native health checks

## Configuration

### Environment Variables

Required (no defaults):
- `JWT_SECRET` - JWT signing secret (same as auth-service)

Optional (with defaults):
- `SERVER_PORT` - Default: 8080
- `REDIS_HOST` - Default: localhost
- `REDIS_PORT` - Default: 6379
- `FRONTEND_URL` - Default: http://localhost:3000
- `LOG_LEVEL` - Default: INFO

### Application Setup

```bash
# 1. Copy .env
cp .env.example .env

# 2. Update .env with your values
nano .env

# 3. Build
./mvnw clean package

# 4. Run
./mvnw spring-boot:run
```

## Troubleshooting

### JWT Secret Mismatch

**Problem**: Tokens from auth-service are rejected
**Solution**: Ensure `JWT_SECRET` matches auth-service config

```bash
# Check gateway secret
echo $JWT_SECRET

# Compare with auth-service
echo $JWT_SECRET
```

### Redis Connection Error

**Problem**: "Cannot get a resource, pool error"
**Solution**: Verify Redis is running

```bash
# Start Redis
docker run -d -p 6379:6379 redis:7-alpine

# Or disable rate limiting
RATE_LIMIT_ENABLED=false ./mvnw spring-boot:run
```

### Service Not Reachable

**Problem**: 502 Bad Gateway errors
**Solution**: Verify downstream service URLs in `GatewayConfig.java`

```bash
# Test auth-service
curl http://localhost:8081/actuator/health

# Test employee-service
curl http://localhost:8083/actuator/health
```

### CORS Issues in Frontend

**Problem**: "Access to XMLHttpRequest blocked by CORS policy"
**Solution**: Add frontend URL to FRONTEND_URL env var

```bash
# For multiple origins
FRONTEND_URL=http://localhost:3000,http://localhost:3001
```

## API Testing

### Get Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "refreshToken": "...",
#   "userId": "...",
#   "username": "admin"
# }
```

### Use Token

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/hr/employees \
  -H "Authorization: Bearer $TOKEN"
```

### Check Health

```bash
curl http://localhost:8080/actuator/health
```

## Docker

### Local Development

```bash
docker-compose up -d
docker-compose logs -f gateway-service
docker-compose down
```

### Production

```bash
docker build -t hrms-gateway-service:1.0.0 .
docker run -d \
  -e JWT_SECRET=your-secret \
  -e FRONTEND_URL=https://app.example.com \
  -p 8080:8080 \
  hrms-gateway-service:1.0.0
```

## Monitoring

### View Logs

```bash
# Docker container
docker logs hrms-gateway-service

# File
tail -f logs/gateway.log

# With timestamps
tail -f logs/gateway.log | grep "ERROR\|WARN"
```

### Metrics

```bash
# Memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Request count
curl http://localhost:8080/actuator/metrics/http.server.requests
```

## Integration with Next.js Frontend

The gateway is compatible with existing frontend configuration:

```typescript
// src/lib/api/axios.ts
const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_URL,
});

// Already configured to:
// 1. Add JWT token to all requests
// 2. Refresh token on 401
// 3. Handle CORS
```

**No frontend changes needed!** The gateway is drop-in replacement for the Express gateway.

## Files to Delete

The old Node.js gateway should be removed:

```bash
rm -rf gateway/
```

And remove from root pom.xml if it was referenced (it wasn't in this case).

## Next Steps

1. ✅ Gateway service created
2. ✅ Security configured
3. ✅ Routes configured
4. ✅ Docker support added
5. → Build and test the gateway
6. → Verify JWT validation
7. → Test rate limiting
8. → Verify CORS with frontend
9. → Delete old `gateway/` directory
10. → Deploy to Docker

## Additional Resources

- [Spring Cloud Gateway Docs](https://docs.spring.io/spring-cloud-gateway/docs/)
- [Spring Security WebFlux](https://docs.spring.io/spring-security/reference/index.html)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [Project Reactor](https://projectreactor.io/)

## Support

For issues:
1. Check `logs/gateway.log`
2. Verify environment variables
3. Check downstream services are running
4. Review README.md for detailed documentation
