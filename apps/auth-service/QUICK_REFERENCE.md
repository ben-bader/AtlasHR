# Auth Service - Quick Test Reference

## 🚀 Quick Start

### 1. Start the Service
```bash
cd apps/auth-service
./mvnw spring-boot:run
```
Service runs at: `http://localhost:8080`

---

## 📋 Quick Test Commands

### Health Check ✅
```bash
curl http://localhost:8080/api/auth/health
```

### Register User 👤
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login 🔐
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```
Save the `token` from response!

### Get Current User 👤 (Protected)
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Get User by ID 👤 (Protected)
```bash
curl -X GET http://localhost:8080/api/auth/user/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Refresh Token 🔄
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN_HERE"
```

---

## 🧪 Automated Testing

### Run Maven Tests
```bash
./mvnw test
```

### Run Automated Script
```bash
chmod +x test-auth.sh
./test-auth.sh
```

### Docker Compose Full Stack
```bash
cd infrastructure/docker
docker compose up -d --build
# Test at: http://localhost:8081/api/auth/health
```

---

## 🎯 Test Scenarios

| Scenario | Method | Endpoint | Expected |
|----------|--------|----------|----------|
| Health Check | GET | /api/auth/health | 200 OK |
| New User | POST | /api/auth/register | 201 Created |
| Duplicate User | POST | /api/auth/register | 400 Bad Request |
| Valid Login | POST | /api/auth/login | 200 OK + token |
| Invalid Login | POST | /api/auth/login | 401 Unauthorized |
| Get User (auth) | GET | /api/auth/me | 200 OK |
| Get User (no auth) | GET | /api/auth/me | 401 Unauthorized |
| Get by ID (auth) | GET | /api/auth/user/{id} | 200 OK |
| Refresh Token | POST | /api/auth/refresh | 200 OK |
| Invalid Token | GET | /api/auth/me + bad token | 401 Unauthorized |

---

## 🔍 What Gets Tested?

✅ **Core Functionality**
- User registration and login
- JWT token generation and validation
- User data retrieval
- Token refresh mechanism

✅ **Security**
- Password hashing (BCrypt)
- Protected endpoints require authentication
- Invalid tokens rejected
- Authorization header validation

✅ **Database**
- User persistence
- Role assignment
- Data integrity
- Index usage

✅ **Error Handling**
- Duplicate username/email prevention
- Invalid credentials rejection
- Missing field validation
- Token expiration handling

---

## 📊 Workspace Status

```
✅ No Duplicates Found
✅ No Conflicts Found  
✅ 16 Java Source Files
✅ All Packages Organized
✅ Build: SUCCESS
```

---

## 📁 Test Files Created

- `TESTING_GUIDE.md` - Comprehensive testing guide
- `test-auth.sh` - Automated bash test script
- `QUICK_REFERENCE.md` - This file

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8080 in use | `lsof -i :8080` → `kill -9 PID` |
| DB connection failed | Check Docker: `docker ps \| grep postgres` |
| RabbitMQ not running | Check Docker: `docker ps \| grep rabbitmq` |
| Token invalid | Ensure Bearer prefix: `Authorization: Bearer TOKEN` |
| 401 Unauthorized | Token expired or invalid - get new token via login |

---

## 📝 Next Steps

1. Start service: `./mvnw spring-boot:run`
2. Run quick test: `./test-auth.sh`
3. Run Maven tests: `./mvnw test`
4. Use Postman for manual testing
5. Monitor logs: `tail -f target/logs/*.log`

---

**Ready to test!** ✨
