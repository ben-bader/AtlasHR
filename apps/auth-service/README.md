# HRMS Auth Service

A comprehensive authentication and authorization service for the HRMS (Human Resource Management System) built with Spring Boot, PostgreSQL, and RabbitMQ.

## Overview

The Auth Service provides:
- User registration and login
- JWT token-based authentication
- Role-based access control (RBAC)
- Token refresh functionality
- RabbitMQ integration for async messaging
- PostgreSQL database for persistence
- Docker and Kubernetes deployment support

## Technology Stack

- **Framework**: Spring Boot 4.0.6
- **Language**: Java 21
- **Database**: PostgreSQL 16
- **Messaging**: RabbitMQ 3
- **Authentication**: JWT (JSON Web Tokens)
- **Container**: Docker
- **Orchestration**: Kubernetes, Helm
- **Build Tool**: Maven

## Project Structure

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/hrms/auth/
│   │   │   ├── AuthServiceApplication.java
│   │   │   ├── application/
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   └── service/          # Business logic
│   │   │   ├── domain/
│   │   │   │   ├── model/            # Entity models
│   │   │   │   └── repository/       # JPA repositories
│   │   │   ├── infrastructure/
│   │   │   │   ├── config/           # Spring configs
│   │   │   │   ├── messaging/        # RabbitMQ handlers
│   │   │   │   └── security/         # JWT & security
│   │   │   └── presentation/
│   │   │       └── controller/       # REST endpoints
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/hrms/auth/       # Unit & integration tests
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16 (for local development)
- RabbitMQ 3 (for local development)

## Installation & Setup

### 1. Local Development

#### Using Docker Compose

```bash
cd infrastructure/docker
docker compose up -d --build
```

This starts:
- PostgreSQL database (port 5432)
- RabbitMQ (ports 5672, 15672)
- Auth Service (port 8081)

#### Manual Setup

1. **Clone and navigate to auth-service:**
   ```bash
   cd apps/auth-service
   ```

2. **Build the project:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Run the service:**
   ```bash
   ./mvnw spring-boot:run
   ```

   The service will start on `http://localhost:8080`

### 2. Configuration

Update `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_auth
spring.datasource.username=hrms
spring.datasource.password=hrms

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# JWT
jwt.secret=your-secret-key-here
jwt.expiration=86400000
jwt.refresh.expiration=604800000
```

Or use environment variables:
```bash
export DB_HOST=localhost
export RABBITMQ_HOST=localhost
export JWT_SECRET=your-secret-key
```

## API Endpoints

### Authentication

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}

Response (201 Created):
{
  "userId": 1,
  "username": "john",
  "message": "User registered successfully"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "john",
  "message": "Login successful"
}
```

#### 3. Refresh Token
```http
POST /api/auth/refresh
Authorization: Bearer <refresh_token>

Response (200 OK):
{
  "token": "new-access-token",
  "refreshToken": "refresh-token",
  "userId": 1,
  "username": "john",
  "message": "Token refreshed successfully"
}
```

#### 4. Get Current User
```http
GET /api/auth/me
Authorization: Bearer <access_token>

Response (200 OK):
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "enabled": true,
  "roles": ["ROLE_USER"],
  "createdAt": "2025-04-27T10:00:00",
  "updatedAt": "2025-04-27T10:00:00"
}
```

#### 5. Get User by ID
```http
GET /api/auth/user/{userId}
Authorization: Bearer <access_token>

Response (200 OK):
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "enabled": true,
  "roles": ["ROLE_USER"],
  "createdAt": "2025-04-27T10:00:00",
  "updatedAt": "2025-04-27T10:00:00"
}
```

#### 6. Health Check
```http
GET /api/auth/health

Response (200 OK):
"Auth Service is running"
```

## Default Roles

The service initializes with three default roles:
- **USER** - Default user role
- **ADMIN** - Administrator with full access
- **MANAGER** - Manager for team management

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  enabled BOOLEAN DEFAULT true,
  account_non_expired BOOLEAN DEFAULT true,
  account_non_locked BOOLEAN DEFAULT true,
  credentials_non_expired BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_username ON users(username);
```

### Roles Table
```sql
CREATE TABLE roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL,
  description TEXT
);
```

### User-Roles Join Table
```sql
CREATE TABLE user_roles (
  user_id BIGINT REFERENCES users(id),
  role_id BIGINT REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);
```

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=AuthServiceApplicationTests
```

### Run with Coverage
```bash
./mvnw test jacoco:report
```

### Test Coverage Report
```bash
open target/site/jacoco/index.html
```

## Build & Deployment

### Docker Build
```bash
docker build -t hrms/auth-service:latest .
```

### Docker Run
```bash
docker run -p 8080:8080 \
  -e DB_HOST=postgres \
  -e RABBITMQ_HOST=rabbitmq \
  -e JWT_SECRET=your-secret \
  hrms/auth-service:latest
```

### Kubernetes Deployment
```bash
kubectl apply -f infrastructure/kubernetes/auth-service/namespace.yaml
kubectl apply -f infrastructure/kubernetes/auth-service/
```

### Helm Deployment
```bash
helm install auth-service infrastructure/helm/auth-service/
```

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/auth-service.yaml`) automatically:
1. Builds the Maven project
2. Runs tests
3. Builds Docker image
4. Pushes to Docker Hub
5. Deploys to Kubernetes (if configured)

### Setup Docker Hub Secrets

Add to GitHub Repository Secrets:
- `DOCKER_USERNAME` - Your Docker Hub username
- `DOCKER_PASSWORD` - Your Docker Hub access token

## Monitoring & Logging

### Actuator Endpoints
- Health: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Info: `GET /actuator/info`

### Log Levels
```properties
logging.level.com.hrms.auth=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Security Considerations

1. **JWT Secret**: Use strong, random secret in production
2. **Password Encoding**: BCrypt hashing with configurable strength
3. **CORS**: Configure for specific origins in production
4. **HTTPS**: Always use HTTPS in production
5. **Token Expiration**: Configure appropriate token lifespans
6. **Database**: Use parameterized queries (JPA prevents SQL injection)

## Environment Variables

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hrms_auth
DB_USER=hrms
DB_PASSWORD=hrms

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

## Troubleshooting

### Database Connection Issues
```bash
# Check PostgreSQL connection
psql -h localhost -U hrms -d hrms_auth

# Verify environment variables
echo $DB_HOST $DB_PORT
```

### RabbitMQ Connection Issues
```bash
# Check RabbitMQ Management UI
http://localhost:15672 (guest/guest)
```

### JWT Token Issues
- Verify JWT_SECRET matches across services
- Check token expiration: `jwt.expiration`
- Ensure Authorization header format: `Bearer <token>`

## Performance Optimization

- Database connection pooling configured
- JPA batch operations enabled
- Query optimization with indexes
- Caching strategies for role lookups

## Contributing

1. Create a feature branch
2. Make your changes
3. Run tests: `./mvnw test`
4. Submit a pull request

## License

This project is part of the HRMS suite and follows the organization's license agreement.

## Support

For issues, questions, or contributions, please contact the development team or create an issue in the repository.

---

**Last Updated**: April 27, 2026
**Version**: 0.0.1-SNAPSHOT
**Status**: Production Ready
