# HRMS Microservices - Quick Reference Card

## 🚀 Start System (One Command)
```bash
cd d:/Hrms-SFE/AtlasHR
docker-compose up -d --build
```

## 🛑 Stop System
```bash
docker-compose down
```

## 🔄 Restart Services
```bash
docker-compose restart                    # All
docker-compose restart auth-service       # Auth only
docker-compose restart employee-service   # Employee only
docker-compose restart api-gateway        # Gateway only
```

---

## 📊 Check Status
```bash
docker-compose ps                         # List all services
docker-compose logs -f                    # Follow all logs
docker-compose logs -f api-gateway        # Gateway logs
docker-compose logs --tail=50 auth-service # Last 50 lines
```

---

## 🧪 Test API Endpoints

### Register User
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"Pass123!"}'
```

### Login
```bash
TOKEN=$(curl -s -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"Pass123!"}' \
  | jq -r '.token')
echo $TOKEN
```

### Get Employees (Protected)
```bash
curl -X GET http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN"
```

### Create Employee
```bash
curl -X POST http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe"}'
```

---

## 🐳 Docker Quick Commands

```bash
# View resource usage
docker stats

# Execute command in container
docker exec hrms-postgres psql -U hrms -d hrms_auth

# View container environment
docker exec api-gateway env

# Rebuild specific service
docker-compose build --no-cache auth-service

# Restart with log follow
docker-compose restart api-gateway && docker-compose logs -f api-gateway
```

---

## 📍 Service Endpoints

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| Gateway | 3000 | http://localhost:3000 | Main entry point |
| Auth Service | 8081 | http://localhost:8081 | Direct access (dev only) |
| Employee Service | 8083 | http://localhost:8083 | Direct access (dev only) |
| PostgreSQL | 5432 | localhost:5432 | Database |
| RabbitMQ | 5672 | localhost:5672 | AMQP |
| RabbitMQ UI | 15672 | http://localhost:15672 | Management console |
| Redis | 6379 | localhost:6379 | Cache |

---

## 🔑 Container Hostnames (Internal)

Use these **inside Docker** (services):
- `hrms-postgres` - Database
- `hrms-rabbitmq` - Message broker
- `hrms-redis` - Cache
- `hrms-auth-service` - Auth service
- `hrms-employee-service` - Employee service
- `api-gateway` - Gateway

---

## 🔐 Important Configuration

### JWT Secret (Must Match)
```
IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
```
Locations:
- `gateway/.env` → `JWT_SECRET`
- `apps/auth-service/docker-compose.yml` → `JWT_SECRET`

### Database Credentials
```
User: hrms
Pass: hrms_pass
```

---

## 📋 Environment Variables

**Gateway** (.env):
```env
JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
NODE_ENV=production
PORT=3000
FRONTEND_URL=http://localhost:3000
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=100
```

**Auth Service** (docker-compose.yml):
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://hrms-postgres:5432/hrms_auth
SPRING_RABBITMQ_HOST=hrms-rabbitmq
SPRING_DATA_REDIS_HOST=hrms-redis
JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
```

**Employee Service** (docker-compose.yml):
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://hrms-postgres:5432/hrms_employee
SPRING_RABBITMQ_HOST=hrms-rabbitmq
SPRING_DATA_REDIS_HOST=hrms-redis
```

---

## 🔗 API Routes Summary

### Public (No Auth)
- `POST /api/auth/register` - Register
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token
- `GET /api/auth/validate` - Validate token

### Protected (Requires JWT)
- `GET /api/employees` - List
- `POST /api/employees` - Create
- `GET /api/employees/:id` - Get by ID
- `PUT /api/employees/:id` - Update
- `DELETE /api/employees/:id` - Delete
- `POST /api/employees/:id/skills` - Add skill
- `POST /api/employees/:id/transfer` - Transfer

---

## 🐛 Quick Debugging

### Check service connectivity
```bash
# From gateway to auth service
docker exec api-gateway curl http://hrms-auth-service:8081/actuator/health

# From gateway to employee service
docker exec api-gateway curl http://hrms-employee-service:8083/actuator/health
```

### View database
```bash
docker exec -it hrms-postgres psql -U hrms -d hrms_auth -c "SELECT * FROM users;"
```

### View RabbitMQ queue
```bash
docker exec hrms-rabbitmq rabbitmqctl list_queues
```

### Decode JWT token
```bash
node -e "console.log(require('jsonwebtoken').decode('TOKEN_HERE'))"
```

---

## 📝 Common Fixes

### Services won't start
```bash
# Rebuild everything
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### JWT validation fails
```bash
# Check JWT_SECRET matches
docker exec api-gateway env | grep JWT_SECRET
docker exec auth-service env | grep JWT_SECRET

# Restart both
docker-compose restart api-gateway auth-service
```

### Can't connect to PostgreSQL
```bash
# Ensure network exists
docker network inspect hrms-network

# Check PostgreSQL is running
docker-compose logs postgres | grep -i "ready"

# Restart
docker-compose restart postgres
```

### RabbitMQ not processing events
```bash
# Check queues exist
docker exec hrms-rabbitmq rabbitmqctl list_queues

# View management UI
# Open http://localhost:15672 (user: hrms, pass: hrms_pass)

# Check event listener logs
docker-compose logs employee-service | grep "UserEventListener"
```

---

## 🏥 Health Checks

All services expose health endpoints:

```bash
# Gateway
curl http://localhost:3000/health

# Auth Service
curl http://localhost:8081/actuator/health

# Employee Service
curl http://localhost:8083/actuator/health

# Expected: {"status":"UP",...}
```

---

## 📊 Key Metrics

```bash
# Check container stats
docker stats

# Database size
docker exec hrms-postgres psql -U hrms -c "SELECT pg_size_pretty(pg_database_size(datname)) AS size FROM pg_database WHERE datname='hrms_auth';"

# RabbitMQ status
docker exec hrms-rabbitmq rabbitmqctl status

# Redis info
docker exec hrms-redis redis-cli INFO
```

---

## 🔑 Example JWT Token Flow

```bash
# 1. Register
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"user1@test.com","password":"Pass123!"}'

# Response: {"userId":"...", "message":"User registered successfully"}

# 2. Login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"Pass123!"}'

# Response: {"token":"eyJ...", "refreshToken":"...", "userId":"..."}

# 3. Use Token
TOKEN="eyJ..."
curl -X GET http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN"

# Response: [{"id":"...", "firstName":"..."}, ...]
```

---

## 🌐 Docker Network

```bash
# List networks
docker network ls

# Inspect hrms-network
docker network inspect hrms-network

# Check container connectivity
docker exec api-gateway ping hrms-auth-service
docker exec api-gateway ping hrms-postgres
```

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `ARCHITECTURE.md` | System design & concepts |
| `DEPLOYMENT_AND_OPERATIONS.md` | Operational procedures |
| `IMPLEMENTATION_SUMMARY.md` | Changes made |
| `gateway/GATEWAY_SETUP_GUIDE.md` | Gateway details |
| `README.md` | Project overview |

---

## 🚨 Emergency Commands

```bash
# Complete reset (WARNING: Deletes all data)
docker-compose down -v
docker-compose up -d --build

# View ALL logs
docker-compose logs

# Stop all containers
docker-compose stop

# Kill all containers
docker-compose kill

# Remove all containers
docker-compose rm -f

# Prune everything (WARNING: Deletes volumes)
docker system prune -a --volumes
```

---

## ✅ Pre-Deployment Checklist

- [ ] JWT_SECRET is strong (64+ chars)
- [ ] JWT_SECRET matches gateway and auth-service
- [ ] Database credentials are set
- [ ] RabbitMQ credentials configured
- [ ] All services build without errors
- [ ] All services start without errors
- [ ] Health endpoints return UP status
- [ ] Gateway routes to services correctly
- [ ] JWT validation works
- [ ] Event publishing/listening works
- [ ] Database migrations completed
- [ ] Logs show no errors

---

## 📞 Need Help?

1. Check `ARCHITECTURE.md` for concepts
2. Review `DEPLOYMENT_AND_OPERATIONS.md` for procedures
3. Check service logs: `docker-compose logs service-name`
4. Run health checks: `curl http://localhost:PORT/health`
5. Verify network: `docker network inspect hrms-network`
6. Check RabbitMQ UI: http://localhost:15672

---

**Version**: 2.0
**Last Updated**: May 2026
**Status**: Production Ready ✅
