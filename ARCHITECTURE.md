# HRMS Microservices Architecture Guide

## 🏗️ System Overview

The HRMS platform is a **production-ready microservices architecture** with proper separation of concerns, event-driven communication, and API gateway pattern.

```
┌─────────────────┐
│    CLIENT/APP   │
└────────┬────────┘
         │ HTTP
         ▼
┌──────────────────────────┐
│    API GATEWAY (3000)    │  ◄─ Single Entry Point
│  - JWT Verification      │
│  - Route Orchestration   │
│  - Rate Limiting         │
│  - CORS Handling         │
└──────────┬─────────────────────────────┬──────────────┐
           │ HTTP                        │ HTTP         │
           ▼                             ▼              ▼
    ┌─────────────────┐        ┌──────────────────┐
    │ AUTH SERVICE    │        │ EMPLOYEE SERVICE │
    │   (port 8081)   │        │   (port 8083)    │
    └─────────────────┘        └──────────────────┘
           │ Events                      │ Events
           └──────────────┬──────────────┘
                          │
                          ▼
                  ┌───────────────────┐
                  │  RABBITMQ (5672)  │ ◄─ Event Bus
                  │ Async Communication
                  └───────────────────┘
                          │
        ┌───────────────┬──┴──────────────┬──────────────┐
        │ PostgreSQL    │ Redis (6379)    │ Each Service │
        │  (5432)       │ (Cache/Sessions)│ Has Own DB   │
        │               │                 │              │
        │ - hrms_auth   │                 │ - hrms_auth  │
        │ - hrms_employee                 │ - hrms_emp   │
        │ - ...future   │                 │ - ...future  │
        └───────────────┴──────────────────┴──────────────┘
```

---

## 🔀 Communication Patterns

### 1. **Client → Gateway** (Synchronous)
```
GET /api/employees
  ↓ (with Authorization header)
API Gateway validates JWT
  ↓
Extracts: userId, username, roles
  ↓
Injects headers: X-User-Id, X-User-Roles, X-Username
  ↓
Proxies to Employee Service (http://hrms-employee-service:8083)
```

### 2. **Services → Services** (Asynchronous via RabbitMQ)
```
Auth Service
  ├─ On user registration
  │   └─ Publishes: user.created event
  │
Employee Service
  ├─ Listens on: employee.user.queue
  ├─ Receives: user.created event
  └─ Action: Auto-creates employee profile
```

---

## 🐳 Docker Network Architecture

All services communicate using **Docker container hostnames** (NOT localhost):

| Service | Container Hostname | Port | Protocol |
|---------|-------------------|------|----------|
| PostgreSQL | `hrms-postgres` | 5432 | TCP |
| RabbitMQ | `hrms-rabbitmq` | 5672 | AMQP |
| Redis | `hrms-redis` | 6379 | TCP |
| Auth Service | `hrms-auth-service` | 8081 | HTTP |
| Employee Service | `hrms-employee-service` | 8083 | HTTP |
| API Gateway | `api-gateway` | 3000 | HTTP |

### Environment Configuration
All services use environment variables with **Docker hostnames**:

```properties
# Auth Service
SPRING_DATASOURCE_URL=jdbc:postgresql://hrms-postgres:5432/hrms_auth
SPRING_RABBITMQ_HOST=hrms-rabbitmq
SPRING_DATA_REDIS_HOST=hrms-redis

# Employee Service
SPRING_DATASOURCE_URL=jdbc:postgresql://hrms-postgres:5432/hrms_employee
SPRING_RABBITMQ_HOST=hrms-rabbitmq
SPRING_DATA_REDIS_HOST=hrms-redis
```

---

## 🔐 Security Architecture

### JWT Flow
1. **Auth Service Issues JWT**
   - User calls: `POST /api/auth/login`
   - Auth Service validates credentials
   - Issues JWT with: `sub` (userId), `username`, `roles`
   - JWT signed with `JWT_SECRET`

2. **Gateway Verifies JWT**
   - Client sends: `Authorization: Bearer <token>`
   - Gateway verifies token against `JWT_SECRET`
   - Extracts: userId, username, roles
   - Creates headers for downstream services

3. **Employee Service Trusts Gateway**
   - Receives headers: `X-User-Id`, `X-User-Roles`
   - Applies `GatewayAuthenticationFilter`
   - Does NOT re-verify JWT (gateway already did)
   - Injects identity into SecurityContext

### Key Security Principle
> **Gateway is the security boundary. Services trust the gateway.**

---

## 📡 Event-Driven Architecture

### Event Flow

#### User Registration
```java
Auth Service
  ├─ POST /api/auth/register
  ├─ Create user in DB
  ├─ Publish event: user.created
  │   {
  │     "eventType": "user.created",
  │     "userId": "123e4567-e89b-12d3-a456-426614174000",
  │     "username": "john.doe",
  │     "email": "john@example.com",
  │     "roles": "ROLE_USER"
  │   }
  └─
     Topic Exchange: hrms.exchange
       ↓
     Routing Key: user.created
       ↓
Employee Service Queue: employee.user.queue
  ├─ Listen: UserEventListener
  ├─ Receives: user.created
  └─ Auto-create employee profile
```

#### Events Published
1. **user.created** - When new user registers
2. **user.deleted** - When user is deleted

#### Events Consumed
1. **Employee Service** listens to all user events
   - Queue: `employee.user.queue`
   - Auto-manages employee lifecycle

---

## 🏃 API Routes

### Public Routes (No Authentication)
```
POST   /api/auth/register          # Register new user
POST   /api/auth/login             # Login & get JWT
POST   /api/auth/refresh           # Refresh expired token
GET    /api/auth/validate          # Validate token format
```

### Protected Routes (Requires JWT)
```
GET    /api/employees              # List all employees
GET    /api/employees/:id          # Get employee
POST   /api/employees              # Create employee
PUT    /api/employees/:id          # Update employee
DELETE /api/employees/:id          # Delete employee
POST   /api/employees/:id/skills   # Add skill
POST   /api/employees/:id/transfer # Transfer employee
```

---

## 🚀 Deployment

### Prerequisites
- Docker & Docker Compose
- `JWT_SECRET` environment variable set

### Start the System
```bash
cd /d:/Hrms-SFE/AtlasHR

# Using comprehensive docker-compose.yml
docker-compose up -d

# View logs
docker-compose logs -f api-gateway
docker-compose logs -f auth-service
docker-compose logs -f employee-service

# Health check
curl http://localhost:3000/health
```

### Service Status
```bash
# Check all services
docker-compose ps

# Check specific service logs
docker-compose logs auth-service --tail=50

# Stop all
docker-compose down

# Restart
docker-compose restart
```

---

## 📊 Health Endpoints

All services expose health check endpoints:

```bash
# Gateway
curl http://localhost:3000/health

# Auth Service
curl http://localhost:8081/actuator/health

# Employee Service  
curl http://localhost:8083/actuator/health
```

Response format:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "rabbit": {"status": "UP"},
    "redis": {"status": "UP"}
  }
}
```

---

## 🔄 Request Flow Example

### Scenario: Get Employee List

```
1. CLIENT REQUEST
   GET /api/employees
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

2. API GATEWAY (3000)
   ├─ Receives request
   ├─ Extracts Bearer token
   ├─ Verifies JWT signature (using JWT_SECRET)
   ├─ Extracts: userId = "123e4567", username = "john.doe", roles = ["ROLE_USER"]
   ├─ Creates headers:
   │   X-User-Id: 123e4567
   │   X-User-Roles: ["ROLE_USER"]
   │   X-Username: john.doe
   ├─ Rewrites path: /api/employees → /employees
   └─ Proxies to http://hrms-employee-service:8083/employees

3. EMPLOYEE SERVICE (8083)
   ├─ Receives request with X-User-Id header
   ├─ GatewayAuthenticationFilter validates header
   ├─ Injects authentication into SecurityContext
   ├─ EmployeeController processes request
   ├─ Queries PostgreSQL: SELECT * FROM employees
   └─ Returns JSON: [{ id: 1, name: "John" }, ...]

4. API GATEWAY RESPONSE
   ├─ Receives 200 OK with JSON
   ├─ Forwards to client
   └─ Response: [{ id: 1, name: "John" }, ...]
```

---

## 🔧 Configuration Files

### Gateway (.env)
```env
PORT=3000
NODE_ENV=production
JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
FRONTEND_URL=http://hrms-frontend:3000
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=100
```

### Auth Service (application.properties)
```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://hrms-postgres:5432/hrms_auth
spring.rabbitmq.host=hrms-rabbitmq
spring.data.redis.host=hrms-redis
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
management.endpoints.web.exposure.include=health,info
```

### Employee Service (application.properties)
```properties
server.port=8083
spring.datasource.url=jdbc:postgresql://hrms-postgres:5432/hrms_employee
spring.rabbitmq.host=hrms-rabbitmq
spring.data.redis.host=hrms-redis
management.endpoints.web.exposure.include=health,info
```

---

## 🐛 Troubleshooting

### Services can't communicate
**Issue**: `Connection refused to hrms-postgres`
**Solution**: Ensure Docker network is created and all containers joined
```bash
docker network ls | grep hrms-network
docker network inspect hrms-network
```

### JWT validation fails at gateway
**Issue**: `Invalid token` error
**Solution**: Ensure `JWT_SECRET` matches in:
- `.env` (Gateway)
- `application.properties` (Auth Service)

### Employee Service returns 401
**Issue**: `Missing X-User-Id header`
**Solution**: This means request bypassed gateway. Always route through gateway at port 3000.

### RabbitMQ events not processed
**Issue**: Employee doesn't auto-create on user registration
**Solution**: Check queue bindings
```bash
docker exec hrms-rabbitmq rabbitmqctl list_queues name
docker exec hrms-rabbitmq rabbitmqctl list_bindings
```

---

## 📝 Data Models

### PostgreSQL Databases

**hrms_auth** (Auth Service)
```sql
-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY,
  username VARCHAR(255) UNIQUE,
  email VARCHAR(255) UNIQUE,
  password_hash VARCHAR(255),
  enabled BOOLEAN,
  created_at TIMESTAMP
);

-- Roles
CREATE TABLE roles (
  id UUID PRIMARY KEY,
  name VARCHAR(50),
  description TEXT
);

-- User Roles
CREATE TABLE user_roles (
  user_id UUID REFERENCES users(id),
  role_id UUID REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);
```

**hrms_employee** (Employee Service)
```sql
-- Employees (auto-created from user.created event)
CREATE TABLE employees (
  id UUID PRIMARY KEY,
  user_id UUID UNIQUE,  -- References hrms_auth.users.id
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  email VARCHAR(255),
  department_id UUID,
  designation_id UUID,
  created_at TIMESTAMP
);
```

---

## 🔄 Message Broker Configuration

### RabbitMQ Topology

```
Exchange: hrms.exchange (Topic)
  ├─ user.created → employee.user.queue
  ├─ user.deleted → employee.user.queue
  └─ (Ready for future events)

Queue: employee.user.queue
  └─ Consumer: UserEventListener
```

Management UI: http://localhost:15672
- Username: `hrms`
- Password: `hrms_pass`

---

## 📚 Key Classes

### Gateway
- `authMiddleware.js` - JWT verification
- `proxyMiddleware.js` - Service routing
- `auth.routes.js` - Auth endpoints
- `employee.routes.js` - Employee endpoints

### Auth Service
- `AuthService.java` - Registration, login, token refresh
- `UserEventPublisher.java` - Emits user events
- `RabbitMQConfig.java` - Topic exchange setup

### Employee Service
- `GatewayAuthenticationFilter.java` - Trusts gateway headers
- `UserEventListener.java` - Listens to auth events
- `RabbitMQConfig.java` - Queue and binding setup

---

## ✅ Production Checklist

- [ ] Set strong `JWT_SECRET` (64+ chars)
- [ ] Enable HTTPS on API Gateway
- [ ] Configure proper FRONTEND_URL for CORS
- [ ] Set up log aggregation (ELK, CloudWatch, etc.)
- [ ] Configure database backups
- [ ] Set up monitoring/alerts
- [ ] Enable rate limiting with realistic thresholds
- [ ] Use secrets management (Vault, AWS Secrets Manager)
- [ ] Enable audit logging
- [ ] Configure auto-scaling for microservices

---

## 📖 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Express.js Documentation](https://expressjs.com/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

**Last Updated**: May 2026
**Architecture Version**: 2.0 (Event-Driven with Gateway Pattern)
