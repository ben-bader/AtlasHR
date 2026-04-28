# API Gateway - Quick Start Guide

## What is this Gateway?

This is an API Gateway that acts as a single entry point for your HRMS application. It:
- Routes requests from your Next.js frontend to backend microservices
- Handles authentication forwarding
- Manages CORS, rate limiting, and security
- Provides a unified API endpoint for your frontend

## Quick Setup (5 minutes)

### 1. Install Dependencies
```bash
cd gateway
npm install
```

### 2. Configure Environment
```bash
# Copy template
cp .env.example .env

# Edit .env if needed (defaults work for local development)
```

### 3. Start the Gateway
```bash
# Development mode (with auto-reload)
npm run dev

# Or production mode
npm start
```

You should see:
```
🚀 API Gateway running on port 3000 in development mode
```

## Using with Your Frontend (Next.js)

### Configure Next.js API Client

In your frontend, set the base API URL:

```javascript
// utils/apiClient.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add token to requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
```

### Example: Login Request

```javascript
// pages/login.js
import apiClient from '../utils/apiClient';

export default function LoginPage() {
  const handleLogin = async (email, password) => {
    try {
      const response = await apiClient.post('/auth/login', {
        email,
        password
      });
      
      localStorage.setItem('authToken', response.data.token);
      // Redirect to dashboard
    } catch (error) {
      console.error('Login failed:', error.response?.data);
    }
  };

  // ... rest of component
}
```

## Available Endpoints

### Authentication
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/register` - Register new account
- `POST /api/auth/verify` - Verify JWT token
- `POST /api/auth/refresh` - Get new token

### Health & Status
- `GET /health` - Gateway status
- `GET /health/services` - All services status

### Service Proxies
- `/api/auth-service/*` - Auth service endpoints
- `/api/users/*` - User management
- `/api/employees/*` - Employee management
- `/api/payroll/*` - Payroll management

## Running with Docker

Your project already has a centralized Docker Compose setup in `infrastructure/docker/docker-compose.yml` that manages all services including the API Gateway.

### Start Everything Together

```bash
cd infrastructure/docker
docker-compose up -d
```

This automatically starts:
- API Gateway (port 3000)
- Auth Service (port 8081)
- PostgreSQL (port 5432)
- RabbitMQ (ports 5672 & 15672)

View gateway logs:
```bash
docker-compose logs -f api-gateway
```

Stop all services:
```bash
docker-compose down
```

## Testing the Gateway

### Test if Gateway is Running
```bash
curl http://localhost:3000/health
```

Expected response:
```json
{
  "status": "ok",
  "timestamp": "2024-01-15T10:30:00.000Z",
  "uptime": 123.45
}
```

### Test Service Health
```bash
curl http://localhost:3000/health/services
```

### Test Authentication
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

## Common Issues

### "Cannot connect to services"
- Ensure backend services are running on configured ports
- Check `.env` SERVICE_URL variables
- Run: `curl http://localhost:8081/actuator/health` (for auth service)

### "CORS errors in frontend"
- Verify `FRONTEND_URL` in `.env` matches your frontend URL
- Check browser console for exact CORS error
- Ensure credentials: true is set in fetch requests

### "Port 3000 already in use"
```bash
# Change port in .env
PORT=3001

# Or kill the process using it
lsof -ti:3000 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :3000   # Windows
```

## Next Steps

1. **Start your backend services** (auth-service on port 8081, etc.)
2. **Start your Next.js frontend** on port 3000
3. **Update frontend API client** to use gateway endpoints
4. **Test authentication flow** end-to-end
5. **Deploy to production** when ready

## File Structure

```
gateway/
├── src/
│   ├── index.js                 # Main server
│   ├── middleware/              # Express middleware
│   ├── routes/                  # API route handlers
│   └── utils/                   # Helper utilities
├── Dockerfile                   # Container image
├── docker-compose.yml           # Local development stack
├── package.json                 # Dependencies
├── .env.example                 # Environment template
└── README.md                    # Full documentation
```

## Need Help?

- **Full Documentation**: See `README.md`
- **Backend Integration**: Check `docker-compose.yml`
- **Troubleshooting**: Run `npm run dev` for detailed logs

---

**Tip**: Keep the gateway running while developing! It auto-reloads on code changes when using `npm run dev`.
