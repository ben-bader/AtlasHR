# Employee Service Configuration

Configuration files and environment setup for the Employee Service microservice.

## Directory Structure

```
.
├── .env.example                 # Environment variables template
├── application.properties        # Default Spring Boot configuration
├── application-dev.properties    # Development environment config
├── application-test.properties   # Test environment config
└── application-prod.properties   # Production environment config
```

## Environment Setup

### Local Development

1. **Copy example environment file:**

```bash
cd apps/employee-service
cp .env.example .env
```

2. **Edit .env with your local settings:**

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hrms_employee_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_RABBITMQ_HOST=localhost
```

3. **Load environment variables:**

```bash
# Bash
export $(cat .env | xargs)

# Zsh
export $(cat .env | xargs)

# Or use direnv
direnv allow
```

4. **Run the service:**

```bash
mvn spring-boot:run
```

### Using Docker Compose

```bash
# Start all services (PostgreSQL, RabbitMQ, Employee Service)
docker-compose up -d

# View logs
docker-compose logs -f employee-service

# Stop services
docker-compose down
```

## Profile-Specific Configuration

### Development (default)

```properties
spring.profiles.active=dev
spring.jpa.hibernate.ddl-auto=update
logging.level.com.hrms=DEBUG
server.error.include-binding-errors=always
```

### Test

```properties
spring.profiles.active=test
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
logging.level.com.hrms=DEBUG
```

### Production

```properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
logging.level.com.hrms=WARN
server.error.include-message=never
```

## Database Configuration

### PostgreSQL Setup

```sql
-- Create database
CREATE DATABASE hrms_employee_db;

-- Create user
CREATE USER hrms_user WITH PASSWORD 'secure-password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hrms_employee_db TO hrms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hrms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO hrms_user;
```

### Connection Pool Configuration

```properties
# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

## RabbitMQ Configuration

### Docker Compose RabbitMQ

```yaml
rabbitmq:
  image: rabbitmq:3.13-management-alpine
  environment:
    RABBITMQ_DEFAULT_USER: guest
    RABBITMQ_DEFAULT_PASS: guest
  ports:
    - "5672:5672"      # AMQP
    - "15672:15672"    # Management UI
```

Access management UI: http://localhost:15672 (guest/guest)

### Exchange & Queue Configuration

```properties
# Exchange configuration
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Custom configuration in application.properties
app.rabbitmq.exchange.employee-events=employee.events
app.rabbitmq.queue.employee-created=employee.created
app.rabbitmq.queue.employee-updated=employee.updated
app.rabbitmq.queue.employee-transferred=employee.transferred
app.rabbitmq.queue.employee-terminated=employee.terminated
```

## JWT Configuration

### Generate JWT Secret

```bash
# Generate a 256-bit key (recommended minimum)
openssl rand -base64 32

# Generate a 512-bit key (more secure)
openssl rand -base64 64
```

### Configuration

```properties
# JWT settings
app.jwt.secret=your-generated-secret-here
app.jwt.expiration=86400000      # 24 hours in milliseconds
app.jwt.refresh-token.expiration=604800000  # 7 days
```

### Token Generation Example

```bash
# Using jwt-decode.sh script
./jwt-decode.sh "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Logging Configuration

### Log Level Configuration

```properties
# Root logger
logging.level.root=INFO

# Application loggers
logging.level.com.hrms=DEBUG
logging.level.com.hrms.employee=DEBUG
logging.level.com.hrms.employee.service=DEBUG

# Third-party loggers
logging.level.org.springframework=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=WARN
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE
```

### Log Output Configuration

```properties
# Console logging
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# File logging
logging.file.name=logs/employee-service.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

## Actuator Endpoints

### Health Endpoints

```properties
# Expose specific endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus

# Health details
management.endpoint.health.show-details=when-authorized
management.health.defaults.enabled=true
```

### Available Endpoints

- `GET /actuator/health` - Overall application health
- `GET /actuator/health/liveness` - Pod liveness probe
- `GET /actuator/health/readiness` - Pod readiness probe
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus metrics

## Performance Tuning

### Thread Pool Configuration

```properties
# Thread pool for async tasks
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=20
spring.task.execution.pool.queue-capacity=100
spring.task.execution.thread-name-prefix=employee-svc-

# Thread pool for scheduled tasks
spring.task.scheduling.pool.size=5
spring.task.scheduling.thread-name-prefix=employee-scheduled-
```

### Database Query Optimization

```properties
# Hibernate query optimization
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true

# SQL generation
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```

## Security Configuration

### CORS Configuration

```properties
# CORS allowed origins
app.security.cors.allowed-origins=http://localhost:3000,http://localhost:4200
app.security.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
app.security.cors.allowed-headers=*
app.security.cors.allow-credentials=true
```

### SSL/TLS Configuration

```properties
# Enable HTTPS
server.ssl.enabled=true
server.ssl.key-store=/path/to/keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.port=8443
```

## Kubernetes ConfigMap & Secrets

### ConfigMap Example

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: employee-service-config
data:
  SPRING_PROFILES_ACTIVE: prod
  LOGGING_LEVEL_ROOT: INFO
  MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: health,info,metrics
```

### Secret Example

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: employee-service-secrets
type: Opaque
data:
  SPRING_DATASOURCE_PASSWORD: cG9zdGdyZXM=  # base64 encoded
  JWT_SECRET: eW91ci1zZWNyZXQtaGVyZQ==     # base64 encoded
```

## Quick Reference Commands

```bash
# View current configuration
mvn spring-boot:run -Dspring-boot.run.arguments=--debug

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev

# Check configuration properties
curl http://localhost:8082/actuator/env | jq

# View active profiles
curl http://localhost:8082/actuator/env | jq '.propertySources[] | select(.name | contains("applicationConfig")) | .source.spring.profiles.active'
```

## Troubleshooting Configuration

### Property not being set

1. Check profile is active:
```bash
curl http://localhost:8082/actuator/env | grep "spring.profiles.active"
```

2. Verify file exists:
```bash
ls -la src/main/resources/application*.properties
```

3. Check for typos in property names

4. View all properties:
```bash
curl http://localhost:8082/actuator/configprops
```

### Secrets not loading

1. Verify secret exists in Kubernetes:
```bash
kubectl get secrets employee-service-secrets -n hrms
kubectl get secrets employee-service-secrets -n hrms -o yaml
```

2. Check environment variables are mounted:
```bash
kubectl exec <pod> -n hrms -- env | grep SPRING_DATASOURCE
```

## References

- [Spring Boot Configuration](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [RabbitMQ Configuration](https://www.rabbitmq.com/configure.html)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP)
- [Kubernetes ConfigMaps & Secrets](https://kubernetes.io/docs/concepts/configuration/configmap/)
