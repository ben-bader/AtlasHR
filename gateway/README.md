# HRMS API Gateway

A high-performance API Gateway for the HRMS microservices architecture, built with Node.js Express. Routes requests from the Next.js frontend to multiple backend services with built-in features like rate limiting, CORS, security headers, and request logging.

## 🎯 Features

- **Service Routing**: Routes requests to Auth, User, Employee, and Payroll services
- **CORS Support**: Pre-configured for Next.js frontend integration
- **Rate Limiting**: Prevents abuse with configurable request throttling
- **Security**: Helmet.js for HTTP headers security
- **Logging**: Morgan + custom request logger for monitoring
- **Health Checks**: Endpoint to monitor gateway and service health
- **Error Handling**: Centralized error handling with proper status codes
- **Docker Ready**: Includes Dockerfile and docker-compose for containerization

## 📋 Prerequisites

- Node.js 18+ or 20+
- npm or yarn
- Running backend microservices (auth-service, etc.)

## 🚀 Getting Started

### Local Development

1. **Install dependencies**:
   ```bash
   cd gateway
   npm install
   ```

2. **Configure environment**:
   ```bash
   cp .env.example .env
   ```
   
   Update `.env` with your service URLs:
   ```env
   PORT=3000
   NODE_ENV=development
   AUTH_SERVICE_URL=http://localhost:8081
   USER_SERVICE_URL=http://localhost:8082
   FRONTEND_URL=http://localhost:3000
   ```

3. **Start development server**:
   ```bash
   npm run dev
   ```
   
   The gateway will be available at `http://localhost:3000`

4. **Start production server**:
   ```bash
   npm start
   ```

## � Docker Setup

### Using Infrastructure Docker Compose (Recommended)

The project uses a centralized Docker setup in `infrastructure/docker/docker-compose.yml` that includes the API Gateway, all services, and databases.

```bash
# Start all services from the infrastructure directory
cd infrastructure/docker
docker-compose up -d

# View logs
docker-compose logs -f api-gateway

# Stop services
docker-compose down
```

This starts:
- **API Gateway** on port 3000
- **Auth Service** on port 8081
- **PostgreSQL** on port 5432
- **RabbitMQ** on ports 5672 & 15672

### Build Docker Image Only

To build just the gateway image:

```bash
docker build -t hrms-api-gateway:latest .
```

## 🔌 API Routes

### Health Checks
- `GET /health` - Gateway health status
- `GET /health/services` - All services health status

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/verify` - Verify JWT token
- `POST /api/auth/refresh` - Refresh JWT token

### Service Proxies
- `/api/auth-service/*` → Auth Service
- `/api/users/*` → User Service
- `/api/employees/*` → Employee Service
- `/api/payroll/*` → Payroll Service

## ⚙️ Configuration

### Environment Variables

```env
# Server
PORT=3000
NODE_ENV=development|production

# Service URLs
AUTH_SERVICE_URL=http://localhost:8081
USER_SERVICE_URL=http://localhost:8082
EMPLOYEE_SERVICE_URL=http://localhost:8083
PAYROLL_SERVICE_URL=http://localhost:8084

# Frontend
FRONTEND_URL=http://localhost:3000

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRY=7d

# Rate Limiting
RATE_LIMIT_WINDOW_MS=15000
RATE_LIMIT_MAX_REQUESTS=100

# Logging
LOG_LEVEL=debug|info|warn|error
```

## 📡 Service Health Check

Check if all services are healthy:

```bash
curl http://localhost:3000/health/services
```

Response:
```json
{
  "status": "healthy",
  "services": [
    {
      "name": "auth-service",
      "status": "up",
      "url": "http://localhost:8081"
    }
  ],
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

## 🔒 Security Features

- **Helmet.js**: Sets security HTTP headers
- **CORS**: Restricted to frontend URL
- **Rate Limiting**: 100 requests per 15 seconds (configurable)
- **Request Validation**: JSON body size limit of 10MB
- **Error Handling**: No sensitive information leaked in responses

## 🧪 Testing with cURL

### Login
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Register
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123","name":"John Doe"}'
```

### Health Check
```bash
curl http://localhost:3000/health
```

## 📊 Monitoring

The gateway logs:
- All incoming requests with method, path, and response status
- Response times for performance monitoring
- Errors with full stack traces (dev mode only)
- Service availability warnings

View logs:
```bash
# Local development
npm run dev  # Real-time logs

# Docker
docker-compose logs -f api-gateway
```

## 🔧 Development

### Project Structure

```
gateway/
├── src/
│   ├── index.js                 # Main application entry
│   ├── middleware/
│   │   ├── errorHandler.js      # Global error handling
│   │   └── requestLogger.js     # Request logging
│   ├── routes/
│   │   ├── auth.js              # Auth endpoints
│   │   └── health.js            # Health check endpoints
│   └── utils/
│       └── serviceValidator.js  # Service health validation
├── Dockerfile                    # Container image
├── docker-compose.yml            # Docker composition
├── package.json                  # Dependencies
├── .env.example                  # Environment template
└── README.md                     # This file
```

### Adding New Service Routes

1. Create a new proxy middleware in `src/index.js`:
```javascript
app.use('/api/new-service', createProxyMiddleware({
  target: process.env.NEW_SERVICE_URL || 'http://localhost:8085',
  changeOrigin: true,
  pathRewrite: {
    '^/api/new-service': ''
  },
  onError: (err, req, res) => {
    console.error('New Service Error:', err);
    res.status(503).json({ error: 'New Service unavailable' });
  }
}));
```

2. Update `.env` and `.env.example`:
```env
NEW_SERVICE_URL=http://localhost:8085
```

## 🚨 Troubleshooting

### Services Unreachable
- Verify services are running and accessible at configured URLs
- Check firewall and network connectivity
- Review logs: `docker-compose logs`

### CORS Errors
- Ensure `FRONTEND_URL` matches your frontend's actual URL
- Check that credentials are properly handled in requests

### Rate Limiting Issues
- Increase `RATE_LIMIT_MAX_REQUESTS` for development
- Reset by restarting the gateway

## 📚 Next Steps

1. **Integrate with Next.js Frontend**:
   - Set API base URL to `http://localhost:3000/api`
   - Configure authentication with JWT tokens

2. **Add Service-Specific Logic**:
   - Create custom middleware for authentication
   - Implement request/response transformation

3. **Production Deployment**:
   - Use environment-specific `.env` files
   - Enable HTTPS with reverse proxy (nginx/Caddy)
   - Set up monitoring and alerting

## 📝 License

MIT
