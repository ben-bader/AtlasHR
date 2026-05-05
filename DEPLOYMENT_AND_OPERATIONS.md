# HRMS Microservices Deployment & Operations Guide

## 🚀 Quick Start

### 1. Clone & Navigate
```bash
cd d:/Hrms-SFE/AtlasHR
```

### 2. Build & Start (One Command)
```bash
# Build all images and start containers
docker-compose up -d --build

# Wait for services to be ready (~30 seconds)
docker-compose logs -f

# When you see "UP" status for all services, proceed to testing
```

### 3. Verify All Services Are Running
```bash
docker-compose ps

# Expected output:
# NAME                    STATUS              PORTS
# hrms-postgres          Up (healthy)         5432:5432
# hrms-rabbitmq          Up (healthy)         5672:5672, 15672:15672
# hrms-redis             Up (healthy)         6379:6379
# hrms-auth-service      Up (healthy)         8081:8081
# hrms-employee-service  Up (healthy)         8083:8083
# api-gateway            Up (healthy)         3000:3000
```

### 4. Test Gateway Health
```bash
curl http://localhost:3000/health

# Expected: 200 OK with status JSON
```

---

## 📝 Testing the System

### Test 1: User Registration

```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'

# Expected Response:
# {
#   "userId": "550e8400-e29b-41d4-a716-446655440000",
#   "username": "john.doe",
#   "message": "User registered successfully"
# }
```

### Test 2: User Login

```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePass123!"
  }'

# Expected Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "refreshToken": "...",
#   "userId": "550e8400-e29b-41d4-a716-446655440000",
#   "username": "john.doe",
#   "message": "Login successful"
# }

# SAVE the token for next steps:
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Test 3: Access Employee Service (Protected Route)

```bash
# With valid token
curl -X GET http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN"

# Expected Response:
# []  (empty list initially, or existing employees)

# Without token (should fail with 401)
curl -X GET http://localhost:3000/api/employees

# Expected Response:
# {
#   "error": "Unauthorized",
#   "message": "Missing Authorization header"
# }
```

### Test 4: Verify Event Flow

After registering a user, check if employee profile was auto-created:

```bash
# Get the JWT token from login (from Test 2)
export TOKEN="your_jwt_token"

# Query employees
curl -X GET http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN"

# Should see employee record created from user.created event
```

---

## 🔍 Monitoring & Logs

### View All Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api-gateway
docker-compose logs -f auth-service
docker-compose logs -f employee-service

# Last 50 lines
docker-compose logs --tail=50 auth-service

# Follow specific pattern
docker-compose logs -f | grep "ERROR"
```

### Check Service Health

```bash
# Auth Service Health
curl http://localhost:8081/actuator/health

# Employee Service Health
curl http://localhost:8083/actuator/health

# Full details (development only)
curl http://localhost:8081/actuator/health/detailed
```

### RabbitMQ Management UI
- URL: http://localhost:15672
- Username: `hrms`
- Password: `hrms_pass`

Check:
1. **Queues** → `employee.user.queue` (should show messages if events published)
2. **Exchanges** → `hrms.exchange` (Topic exchange)
3. **Connections** → Services connected

### Redis CLI
```bash
# Connect to Redis
docker exec -it hrms-redis redis-cli

# Check cached data
KEYS *
GET some_key
FLUSHALL  # Clear all (use with caution!)
```

### PostgreSQL Admin
```bash
# Connect to PostgreSQL
docker exec -it hrms-postgres psql -U hrms -d hrms_auth

# List databases
\l

# Connect to employee database
\c hrms_employee

# List tables
\dt

# Query users
SELECT id, username, email FROM users;

# Query employees
SELECT id, user_id, first_name, last_name FROM employees;
```

---

## 🔧 Common Operations

### Restart Specific Service
```bash
# Restart auth-service only
docker-compose restart auth-service

# Restart gateway
docker-compose restart api-gateway
```

### View Container Logs with Timestamps
```bash
docker-compose logs --timestamps auth-service
```

### Rebuild Specific Service
```bash
# Rebuild auth-service
docker-compose build auth-service
docker-compose up -d auth-service
```

### Stop All Services (Keep Data)
```bash
docker-compose stop
```

### Stop & Remove Everything (Delete Data)
```bash
docker-compose down -v
```

### Update Environment Variables

1. Edit `.env` file or service docker-compose
2. Rebuild: `docker-compose build --no-cache service_name`
3. Restart: `docker-compose up -d service_name`

---

## 🐛 Troubleshooting

### Issue: Services Can't Connect to PostgreSQL

**Symptoms**: Auth/Employee service keeps restarting

**Debug**:
```bash
docker-compose logs auth-service | tail -20
```

**Solution**:
```bash
# Ensure network exists
docker network ls
docker network inspect hrms-network

# Ensure PostgreSQL is running
docker-compose logs postgres
docker-compose restart postgres

# Rebuild services
docker-compose up -d --build
```

### Issue: JWT Token Validation Fails

**Symptoms**: `Invalid or expired token` even with new token

**Debug**:
```bash
# Check JWT_SECRET consistency
docker-compose exec api-gateway env | grep JWT_SECRET
docker-compose exec auth-service env | grep JWT_SECRET
```

**Solution**:
```bash
# Ensure same JWT_SECRET in:
# 1. gateway/.env
# 2. auth-service docker-compose.yml
# 3. gateway docker-compose.yml

# Restart both services
docker-compose restart api-gateway auth-service
```

### Issue: Employee Service Returns 401 Unauthorized

**Symptoms**: `Missing X-User-Id header`

**Cause**: Request went directly to port 8083, bypassing gateway

**Solution**: Always use gateway at port 3000:
```bash
# WRONG (direct to service)
curl http://localhost:8083/api/employees

# CORRECT (through gateway)
curl http://localhost:3000/api/employees -H "Authorization: Bearer $TOKEN"
```

### Issue: RabbitMQ Events Not Processed

**Symptoms**: User registers but employee profile not auto-created

**Debug**:
```bash
# Check queue
docker exec hrms-rabbitmq rabbitmqctl list_queues name messages

# Check bindings
docker exec hrms-rabbitmq rabbitmqctl list_bindings

# Check logs
docker-compose logs employee-service | grep UserEventListener
```

**Solution**:
```bash
# Ensure queues/exchanges exist (they auto-create from Spring config)
docker-compose restart employee-service

# Check RabbitMQ Management UI
# Navigate to Queues tab, verify employee.user.queue exists
```

### Issue: Out of Memory

**Symptoms**: Containers randomly crash

**Solution**:
```bash
# Check memory usage
docker stats

# Increase Docker resources in settings
# Restart Docker daemon
```

---

## 📊 Performance Monitoring

### Container Resource Usage
```bash
# Real-time stats
docker stats

# CPU/Memory per service
docker stats --no-stream

# Check specific container
docker stats hrms-postgres
```

### Database Query Performance
```bash
# Connect to PostgreSQL
docker exec -it hrms-postgres psql -U hrms -d hrms_auth

# List slow queries
SELECT query, calls, mean_time FROM pg_stat_statements 
ORDER BY mean_time DESC LIMIT 5;

# Check table size
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) 
FROM pg_tables 
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### Check API Response Times
```bash
# Gateway response time
time curl http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN"

# Direct service response time
time curl http://localhost:8083/employees \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000"
```

---

## 🔐 Security Checklist

### Before Production

- [ ] Change `JWT_SECRET` to a strong, unique 64+ character string
- [ ] Change PostgreSQL credentials (`hrms` / `hrms_pass`)
- [ ] Change RabbitMQ credentials
- [ ] Enable HTTPS on API Gateway
- [ ] Configure firewall rules
- [ ] Set up proper logging & monitoring
- [ ] Enable database encryption
- [ ] Configure backups
- [ ] Use secrets management system
- [ ] Run security audit

### Setting Strong JWT_SECRET
```bash
# Generate on Linux/Mac
openssl rand -base64 48

# Generate on Windows (Git Bash)
openssl rand -base64 48

# Example output:
# IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=
```

### Rotate JWT_SECRET (Without Downtime)

1. Add new secret to `.env` temporarily
2. Gateway keeps accepting old & new JWT
3. Remove old secret from config
4. Redeploy services

---

## 📈 Scaling Considerations

### Horizontal Scaling
```bash
# Scale employee-service to 2 instances (not recommended with Docker Compose)
# Use Docker Swarm or Kubernetes for production scaling

# Alternative: Use docker-compose with multiple services
# (See docker-compose.scale.yml for example)
```

### Load Balancing
```bash
# Add Nginx reverse proxy for multiple instances
# Configure sticky sessions for WebSocket support
```

### Database Connection Pooling
- Auth Service: 10 connections (default)
- Employee Service: 10 connections (default)
- Adjust: `spring.datasource.hikari.maximum-pool-size`

---

## 📋 Maintenance

### Regular Tasks

**Daily**:
- Check logs for errors
- Monitor resource usage
- Verify health endpoints

**Weekly**:
- Check database size
- Review slow queries
- Update dependencies

**Monthly**:
- Database maintenance (VACUUM, ANALYZE)
- Review security logs
- Check backup integrity
- Update OS/Docker

### Backup Strategy
```bash
# Backup PostgreSQL
docker exec hrms-postgres pg_dump -U hrms hrms_auth > backup_auth.sql
docker exec hrms-postgres pg_dump -U hrms hrms_employee > backup_employee.sql

# Backup Docker volumes
docker run --rm -v hrms-postgres_postgres_data:/data -v $(pwd):/backup \
  alpine tar czf /backup/postgres_data.tar.gz -C /data .

# Restore from backup
docker exec -i hrms-postgres psql -U hrms hrms_auth < backup_auth.sql
```

### Disaster Recovery
```bash
# Complete system reset (CAUTION: DELETES ALL DATA)
docker-compose down -v

# Start fresh
docker-compose up -d --build
```

---

## 🎯 Key Metrics to Track

| Metric | Target | Tool |
|--------|--------|------|
| API Response Time | < 200ms | `curl` / APM |
| Error Rate | < 1% | Log aggregation |
| Uptime | > 99.9% | Monitoring tool |
| Database Query Time | < 100ms | PostgreSQL logs |
| Message Processing Latency | < 5s | RabbitMQ UI |
| Memory Usage per Service | < 512MB | `docker stats` |
| Disk Usage | < 80% | `df -h` |

---

## 📞 Support

### Error Documentation
- Check `ARCHITECTURE.md` for design patterns
- Review service README files for specifics
- Check logs with timestamps: `docker-compose logs --timestamps`

### Getting Help
1. Check logs for error messages
2. Verify all services are healthy
3. Test connectivity between services
4. Review configuration files
5. Check network connectivity

---

**Last Updated**: May 2026
**Version**: 1.0
