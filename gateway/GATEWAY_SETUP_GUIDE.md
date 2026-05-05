# API Gateway - Complete Implementation Guide

## 📋 Overview

The **API Gateway** is the single entry point for all HRMS microservices. It handles:

- ✅ JWT Token Verification
- ✅ Route Orchestration to microservices
- ✅ User Identity Injection via Headers
- ✅ CORS & Security Headers
- ✅ Rate Limiting
- ✅ Request/Response Logging

---

## 🏗️ Architecture

```
┌─────────────────────────────────┐
│  Client/Frontend (port 3000)    │
└────────────────┬────────────────┘
                 │ HTTP/HTTPS
                 ▼
    ┌────────────────────────────┐
    │   API GATEWAY (port 3000)  │
    ├────────────────────────────┤
    │ 1. CORS Handler            │
    │ 2. Rate Limiter            │
    │ 3. JWT Validator           │
    │ 4. Header Injector         │
    │ 5. Request Router          │
    └────────────────────────────┘
           │              │
           │              │
     ┌─────▼────┐   ┌─────▼──────────┐
     │   Auth   │   │   Employee     │
     │ Service  │   │    Service     │
     │ (8081)   │   │    (8083)      │
     └──────────┘   └────────────────┘
```

---

## 🛠️ Setup Instructions

### 1. Install Dependencies
```bash
cd apps/gateway
npm install
```

This installs:
- `express` - Web framework
- `http-proxy-middleware` - Service proxying
- `jsonwebtoken` - JWT verification
- `helmet` - Security headers
- `cors` - CORS handling
- `morgan` - HTTP logging
- `express-rate-limit` - Rate limiting
- `dotenv` - Environment variables

### 2. Configure Environment

Create/update `.env`:

```env
# Server
PORT=3000
NODE_ENV=production

# Security
JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=

# Frontend Access
FRONTEND_URL=http://hrms-frontend:3000

# Rate Limiting
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=100

# Logging
LOG_LEVEL=info
```

**⚠️ CRITICAL**: `JWT_SECRET` must match the auth-service value!

### 3. Start Gateway

**Development**:
```bash
npm run dev
# Watches for file changes, auto-restarts
```

**Production**:
```bash
npm start
```

---

## 📁 File Structure

```
gateway/
├── src/
│   ├── index.js                    # Main application
│   ├── middleware/
│   │   ├── authMiddleware.js       # JWT verification
│   │   ├── proxyMiddleware.js      # Service routing
│   │   ├── errorHandler.js         # Error handling
│   │   └── requestLogger.js        # HTTP logging
│   └── routes/
│       ├── auth.routes.js          # Auth endpoints
│       └── employee.routes.js      # Employee endpoints
├── .env                            # Environment config
├── .env.example                    # Template
├── Dockerfile                      # Container image
├── docker-compose.yml              # Docker orchestration
├── package.json                    # Dependencies
└── README.md                       # This file
```

---

## 🔐 JWT Authentication Flow

### 1. User Logs In

```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "SecurePass123!"
}
```

**Auth Service Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john.doe"
}
```

### 2. Client Stores Token

Browser stores JWT in:
- LocalStorage: `localStorage.setItem('token', token)`
- Or SessionStorage for session-only access

### 3. Client Makes Protected Request

```bash
GET /api/employees
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4. Gateway Validates JWT

**Gateway receives**:
```
Authorization: Bearer <token>
```

**Gateway does**:
1. Extract token from `Authorization` header
2. Verify signature using `JWT_SECRET`
3. Check expiration
4. Extract claims: `userId`, `username`, `roles`

**If valid**:
- Adds headers:
  - `X-User-Id: 550e8400-e29b-41d4-a716-446655440000`
  - `X-User-Roles: ["ROLE_USER"]`
  - `X-Username: john.doe`
- Proxies to Employee Service

**If invalid**:
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 5. Downstream Service Receives Headers

Employee Service receives:
```
GET /employees
X-User-Id: 550e8400-e29b-41d4-a716-446655440000
X-User-Roles: ["ROLE_USER"]
X-Username: john.doe
```

Service uses these headers for business logic (no re-validation).

---

## 🛣️ Route Mapping

### Public Routes (No Auth Required)

| Method | Gateway Path | Service | Endpoint | Purpose |
|--------|--------------|---------|----------|---------|
| POST | `/api/auth/register` | Auth (8081) | `/register` | Register new user |
| POST | `/api/auth/login` | Auth (8081) | `/login` | User login |
| POST | `/api/auth/refresh` | Auth (8081) | `/refresh` | Refresh expired token |
| GET | `/api/auth/validate` | Auth (8081) | `/validate` | Validate token format |

### Protected Routes (Auth Required)

| Method | Gateway Path | Service | Endpoint | Purpose |
|--------|--------------|---------|----------|---------|
| GET | `/api/employees` | Employee (8083) | `/` | List employees |
| GET | `/api/employees/:id` | Employee (8083) | `/:id` | Get employee |
| POST | `/api/employees` | Employee (8083) | `/` | Create employee |
| PUT | `/api/employees/:id` | Employee (8083) | `/:id` | Update employee |
| DELETE | `/api/employees/:id` | Employee (8083) | `/:id` | Delete employee |
| POST | `/api/employees/:id/skills` | Employee (8083) | `/:id/skills` | Add skill |
| POST | `/api/employees/:id/transfer` | Employee (8083) | `/:id/transfer` | Transfer employee |

---

## 📊 Middleware Pipeline

Each request goes through:

```
1. CORS Middleware
   └─ Allow cross-origin requests from FRONTEND_URL

2. Helmet Middleware
   └─ Set security headers

3. Body Parser Middleware
   └─ Parse JSON/URL-encoded request bodies

4. Morgan Logger Middleware
   └─ Log HTTP requests

5. Rate Limiter Middleware
   └─ Limit requests (15 requests per 15s)

6. JWT Auth Middleware (if protected route)
   └─ Verify token, inject headers

7. Proxy Middleware
   └─ Forward to microservice
```

---

## 🔄 Error Handling

### Missing Authorization Header
```json
{
  "error": "Unauthorized",
  "message": "Missing Authorization header"
}
```
**HTTP Status**: 401

### Invalid Token Format
```json
{
  "error": "Unauthorized",
  "message": "Invalid Authorization header format. Expected: Bearer <token>"
}
```
**HTTP Status**: 401

### Token Expired/Invalid
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "details": "jwt expired"
}
```
**HTTP Status**: 401

### Service Unavailable
```json
{
  "error": "Service Unavailable",
  "message": "Auth service is not responding"
}
```
**HTTP Status**: 503

### Rate Limit Exceeded
```json
{
  "error": "Too many requests",
  "message": "Too many requests from this IP, please try again later."
}
```
**HTTP Status**: 429

---

## 🐳 Docker Deployment

### Build Image
```bash
docker build -t hrms-api-gateway .
```

### Run Container
```bash
docker run \
  -e JWT_SECRET=IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg= \
  -e NODE_ENV=production \
  -p 3000:3000 \
  hrms-api-gateway
```

### Using Docker Compose
```bash
cd .. && docker-compose up -d api-gateway
```

---

## 🧪 Testing

### Test 1: Check Gateway Health
```bash
curl http://localhost:3000/health

# Expected 200 OK
```

### Test 2: Register User
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "TestPass123!"
  }'
```

### Test 3: Login
```bash
TOKEN=$(curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPass123!"
  }' | jq -r '.token')

echo $TOKEN
```

### Test 4: Access Protected Route
```bash
curl -X GET http://localhost:3000/api/employees \
  -H "Authorization: Bearer $TOKEN"
```

### Test 5: Missing Auth Header (Should Fail)
```bash
curl -X GET http://localhost:3000/api/employees

# Expected 401 Unauthorized
```

### Test 6: Invalid Token (Should Fail)
```bash
curl -X GET http://localhost:3000/api/employees \
  -H "Authorization: Bearer invalid_token"

# Expected 401 Unauthorized
```

---

## 📝 Logging

### Request Logging

All HTTP requests are logged with Morgan:

```
GET /api/employees 200 125.34 ms - 2541
POST /api/auth/login 200 234.12 ms - 1024
GET /api/employees/123 401 10.45 ms - 85
```

### JWT Validation Logs

```
[AUTH] ✓ User authenticated: john.doe (550e8400-e29b-41d4-a716-446655440000)
[AUTH] ✗ Token verification failed: jwt expired
```

### Proxy Logs

```
[PROXY→AUTH] POST /login
[PROXY←AUTH] Status 200
[PROXY→EMPLOYEE] GET /employees
[PROXY←EMPLOYEE] Status 200
```

### Access Logs

```bash
# View logs
docker logs api-gateway

# Follow logs
docker logs -f api-gateway

# Last 50 lines
docker logs --tail=50 api-gateway
```

---

## 🔍 Debugging

### Check JWT Token Contents

```bash
# Decode JWT (online: jwt.io)
# Or use Node.js:
node -e "console.log(require('jsonwebtoken').decode('YOUR_TOKEN_HERE'))"
```

### Test JWT Verification

```bash
# Create test script (test-jwt.js)
const jwt = require('jsonwebtoken');
const token = 'YOUR_TOKEN_HERE';
const secret = 'IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=';

try {
  const decoded = jwt.verify(token, secret);
  console.log('Valid token:', decoded);
} catch (error) {
  console.error('Invalid token:', error.message);
}

# Run
node test-jwt.js
```

### Monitor Proxy Requests

```bash
# Enable verbose logging
export DEBUG=http-proxy-middleware:*
npm start
```

### Check Environment Variables

```bash
# Inside container
docker exec api-gateway env | grep -E "JWT|NODE_ENV"

# Locally
echo $JWT_SECRET
echo $NODE_ENV
```

---

## 🚨 Troubleshooting

### Gateway can't connect to services

**Problem**: `Service Unavailable`

**Check**:
1. Are services running?
   ```bash
   docker-compose ps
   ```

2. Can gateway reach services?
   ```bash
   docker exec api-gateway curl http://hrms-auth-service:8081/actuator/health
   docker exec api-gateway curl http://hrms-employee-service:8083/actuator/health
   ```

3. Are services on same network?
   ```bash
   docker network inspect hrms-network
   ```

### JWT validation always fails

**Problem**: `Invalid or expired token`

**Check**:
1. JWT_SECRET matches auth-service
2. Token not expired (check expiration time)
3. Token from correct auth-service instance

**Fix**:
```bash
# Restart both gateway and auth-service
docker-compose restart api-gateway auth-service
```

### Rate limiting too strict

**Problem**: Getting 429 errors

**Solution**:
```env
# Update .env
RATE_LIMIT_WINDOW_MS=60000      # 60 seconds
RATE_LIMIT_MAX_REQUESTS=1000    # 1000 requests
```

### CORS errors in frontend

**Problem**: Blocked cross-origin request

**Solution**:
```env
# Update .env to frontend URL
FRONTEND_URL=http://your-frontend-url:3000
```

---

## 📈 Performance Tuning

### Timeout Configuration

Update `src/index.js`:
```javascript
const authServiceProxy = createProxyMiddleware({
  target: 'http://hrms-auth-service:8081',
  timeout: 30000,  // 30 seconds
  proxyTimeout: 30000,
  // ...
});
```

### Rate Limiting Adjustment

```env
# Conservative (strict rate limiting)
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=50

# Balanced (recommended)
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=100

# Aggressive (high throughput)
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=1000
```

### Connection Pooling

Node.js handles this automatically, but for tuning:

```javascript
// In index.js
const http = require('http');
const https = require('https');

http.globalAgent.maxSockets = 100;
https.globalAgent.maxSockets = 100;
```

---

## 🔐 Security Considerations

### JWT Secret Management
- ✅ Store in `.env` (never commit)
- ✅ Use strong secret (64+ characters)
- ✅ Rotate periodically
- ✅ Same secret across all services

### CORS Configuration
- ✅ Restrict to FRONTEND_URL
- ✅ Avoid `*` in production
- ✅ Specify allowed methods

### Rate Limiting
- ✅ Prevent brute force attacks
- ✅ Adjust thresholds per use case
- ✅ Monitor for DDoS patterns

### Security Headers (Helmet)
- ✅ X-Frame-Options: DENY
- ✅ X-Content-Type-Options: nosniff
- ✅ Strict-Transport-Security (HSTS)

---

## 📚 Additional Resources

- [Express.js Proxy Middleware](https://github.com/chimurai/http-proxy-middleware)
- [JWT.io - Introduction](https://jwt.io/introduction)
- [Helmet Security](https://helmetjs.github.io/)
- [CORS in Express](https://expressjs.com/en/resources/middleware/cors.html)

---

**Last Updated**: May 2026
**Version**: 2.0
**Status**: Production Ready ✅
