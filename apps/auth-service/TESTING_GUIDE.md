# Auth Service Testing Guide

## Workspace Validation ✅

**Scan Results:**
- ✅ No duplicate files found
- ✅ 16 Java source files (all unique)
- ✅ No conflicts or contradictions detected
- ✅ All packages correctly organized
- ✅ Build status: SUCCESS

---

## Method 1: Run Tests Using Maven

### Run All Tests
```bash
cd apps/auth-service
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=AuthServiceApplicationTests
```

### Run Specific Test Method
```bash
./mvnw test -Dtest=AuthServiceApplicationTests#testUserRegistration
```

### Run Tests with Coverage
```bash
./mvnw test jacoco:report
# View coverage report at: target/site/jacoco/index.html
```

---

## Method 2: Manual Testing with cURL

### Prerequisites
Start the service locally:
```bash
cd apps/auth-service
./mvnw spring-boot:run
```

Service runs on: `http://localhost:8080`

### 1. Health Check
```bash
curl -X GET http://localhost:8080/api/auth/health
```

**Expected Response (200 OK):**
```
Auth Service is running
```

---

### 2. Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Expected Response (201 Created):**
```json
{
  "userId": 1,
  "username": "john",
  "message": "User registered successfully",
  "token": null,
  "refreshToken": null
}
```

### Test Cases:
- ✅ Register new user → 201 Created
- ✅ Register duplicate username → 400 Bad Request ("Username already exists")
- ✅ Register duplicate email → 400 Bad Request ("Email already exists")

---

### 3. Login User

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjE2MTYzMjU0MjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "john",
  "message": "Login successful"
}
```

### Test Cases:
- ✅ Login with correct credentials → 200 OK (with tokens)
- ✅ Login with wrong password → 401 Unauthorized
- ✅ Login with non-existent user → 401 Unauthorized

**Save the token for next steps:**
```bash
TOKEN="your_token_here"
```

---

### 4. Get Current User (Protected Endpoint)

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "enabled": true,
  "roles": ["ROLE_USER"],
  "createdAt": "2026-04-27T10:00:00",
  "updatedAt": "2026-04-27T10:00:00"
}
```

### Test Cases:
- ✅ Valid token → 200 OK (user info)
- ✅ Invalid token → 401 Unauthorized
- ✅ No token → 401 Unauthorized
- ✅ Expired token → 401 Unauthorized

---

### 5. Get User by ID (Protected Endpoint)

```bash
curl -X GET http://localhost:8080/api/auth/user/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "enabled": true,
  "roles": ["ROLE_USER"],
  "createdAt": "2026-04-27T10:00:00",
  "updatedAt": "2026-04-27T10:00:00"
}
```

### Test Cases:
- ✅ Valid user ID with valid token → 200 OK
- ✅ Invalid user ID → 404 Not Found
- ✅ Without token → 401 Unauthorized

---

### 6. Refresh Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer $REFRESH_TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "token": "new_access_token",
  "refreshToken": "same_refresh_token",
  "userId": 1,
  "username": "john",
  "message": "Token refreshed successfully"
}
```

### Test Cases:
- ✅ Valid refresh token → 200 OK (new access token)
- ✅ Expired refresh token → 401 Unauthorized
- ✅ Invalid token format → 400 Bad Request

---

## Method 3: Testing with Docker Compose

### Start Full Stack
```bash
cd infrastructure/docker
docker compose up -d --build
```

Services started:
- **PostgreSQL**: localhost:5432
- **RabbitMQ**: localhost:5672 (Management UI: localhost:15672)
- **Auth Service**: localhost:8081

### Test Service Running in Docker
```bash
curl -X GET http://localhost:8081/api/auth/health
```

### View Logs
```bash
docker compose logs auth-service -f
```

### Stop Services
```bash
docker compose down
```

---

## Method 4: Testing with Postman

### Import API Collection

Create a Postman collection with these endpoints:

**Environment Variables:**
- `BASE_URL`: http://localhost:8080
- `TOKEN`: (Set after login)
- `REFRESH_TOKEN`: (Set after login)

**Endpoints:**

| Method | URL | Headers | Body |
|--------|-----|---------|------|
| GET | {{BASE_URL}}/api/auth/health | - | - |
| POST | {{BASE_URL}}/api/auth/register | Content-Type: application/json | `{"username":"john","email":"john@example.com","password":"password123"}` |
| POST | {{BASE_URL}}/api/auth/login | Content-Type: application/json | `{"username":"john","password":"password123"}` |
| GET | {{BASE_URL}}/api/auth/me | Authorization: Bearer {{TOKEN}} | - |
| GET | {{BASE_URL}}/api/auth/user/1 | Authorization: Bearer {{TOKEN}} | - |
| POST | {{BASE_URL}}/api/auth/refresh | Authorization: Bearer {{REFRESH_TOKEN}} | - |

---

## Method 5: Integration Testing

### Run Integration Tests
```bash
cd apps/auth-service
./mvnw test -Dtest=*IntegrationTest
```

### Test Coverage Report
```bash
./mvnw clean test jacoco:report
# Opens: target/site/jacoco/index.html
```

---

## Method 6: Database Verification

### Connect to PostgreSQL (Docker)
```bash
docker exec -it hrms-postgres psql -U hrms -d hrms_auth
```

### SQL Queries to Verify

**Check Users Table:**
```sql
SELECT * FROM users;
```

**Check Roles Table:**
```sql
SELECT * FROM roles;
```

**Check User-Roles Mapping:**
```sql
SELECT u.username, r.name FROM user_roles ur
JOIN users u ON ur.user_id = u.id
JOIN roles r ON ur.role_id = r.id;
```

**Check Default Roles Created:**
```sql
SELECT * FROM roles WHERE name IN ('USER', 'ADMIN', 'MANAGER');
```

---

## Method 7: RabbitMQ Verification

### Access RabbitMQ Management UI
```
URL: http://localhost:15672
Username: guest
Password: guest
```

### Check Exchange and Queue
- **Exchange**: hrms.exchange (Topic)
- **Queue**: auth.queue
- **Routing Key**: auth.#

### Verify Queue Bindings
In Management UI → Exchanges → hrms.exchange → Check bindings

---

## Automated Test Script (Bash)

Create `test-auth-service.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api/auth"
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

echo "=== Testing Auth Service ==="

# Test 1: Health Check
echo -n "Test 1: Health Check ... "
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health")
if [ "$RESPONSE" = "200" ]; then
  echo -e "${GREEN}PASS${NC}"
else
  echo -e "${RED}FAIL${NC} (HTTP $RESPONSE)"
fi

# Test 2: Register User
echo -n "Test 2: Register User ... "
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }')

USER_ID=$(echo $REGISTER_RESPONSE | grep -o '"userId":[0-9]*' | grep -o '[0-9]*')
if [ ! -z "$USER_ID" ]; then
  echo -e "${GREEN}PASS${NC} (User ID: $USER_ID)"
else
  echo -e "${RED}FAIL${NC}"
fi

# Test 3: Login User
echo -n "Test 3: Login User ... "
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
if [ ! -z "$TOKEN" ]; then
  echo -e "${GREEN}PASS${NC}"
else
  echo -e "${RED}FAIL${NC}"
fi

# Test 4: Get Current User
echo -n "Test 4: Get Current User ... "
ME_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/me" \
  -H "Authorization: Bearer $TOKEN")
if [ "$ME_RESPONSE" = "200" ]; then
  echo -e "${GREEN}PASS${NC}"
else
  echo -e "${RED}FAIL${NC} (HTTP $ME_RESPONSE)"
fi

echo "=== Tests Complete ==="
```

Run the script:
```bash
chmod +x test-auth-service.sh
./test-auth-service.sh
```

---

## Testing Checklist

### Core Functionality
- [ ] Health check returns 200
- [ ] User registration with valid data → 201
- [ ] User registration with duplicate username → 400
- [ ] User registration with duplicate email → 400
- [ ] Login with correct credentials → 200 + token
- [ ] Login with wrong password → 401
- [ ] Get current user with valid token → 200
- [ ] Get current user without token → 401
- [ ] Refresh token generates new token → 200
- [ ] Expired token rejected → 401

### Database
- [ ] Users table created with correct schema
- [ ] Roles table created with correct schema
- [ ] Default roles (USER, ADMIN, MANAGER) initialized
- [ ] User-roles join table created
- [ ] Username and email indexes created

### Security
- [ ] Passwords are BCrypt hashed (not plain text)
- [ ] JWT tokens are properly signed
- [ ] CORS is properly configured
- [ ] Authorization header required for protected endpoints
- [ ] Invalid tokens rejected

### Performance
- [ ] Registration completes < 500ms
- [ ] Login completes < 500ms
- [ ] Token refresh completes < 200ms
- [ ] Database queries use indexes

### Error Handling
- [ ] Invalid JSON request → 400
- [ ] Missing required fields → 400
- [ ] Non-existent user → 404 or 401
- [ ] Invalid token format → 401
- [ ] Server errors logged properly

---

## Troubleshooting

### Port Already in Use
```bash
# Find process on port 8080
lsof -i :8080
# Kill process
kill -9 <PID>
```

### Database Connection Failed
```bash
# Check PostgreSQL running
docker ps | grep postgres
# Check connection string in application.properties
```

### RabbitMQ Connection Failed
```bash
# Check RabbitMQ running
docker ps | grep rabbitmq
# Check RabbitMQ logs
docker logs hrms-rabbitmq
```

### JWT Token Errors
- Check JWT_SECRET configured properly
- Verify token not expired
- Ensure Bearer prefix in Authorization header

---

## Summary

Your auth-service is ready for testing with multiple methods:
1. ✅ Maven unit tests
2. ✅ cURL manual testing
3. ✅ Docker Compose testing
4. ✅ Postman API testing
5. ✅ Integration tests
6. ✅ Database verification
7. ✅ Automated bash scripts

**Next Steps:**
- Run all tests with `./mvnw test`
- Start service with `./mvnw spring-boot:run`
- Test endpoints with cURL or Postman
- Verify database with SQL queries
- Monitor with docker logs

**Status**: ✅ All systems ready for testing!
