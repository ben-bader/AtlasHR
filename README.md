# HRMS - AtlasHR Microservices Platform

Complete microservices-based Human Resource Management System built with Spring Boot, React, and modern cloud-native technologies.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Services](#services)
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [Development](#development)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🎯 Project Overview

AtlasHR is a comprehensive HRMS platform built using microservices architecture, enabling scalable and maintainable human resource management across organizations. The platform provides complete employee lifecycle management, from onboarding to offboarding, with integrated payroll, leave, and talent management.

### Key Features

✅ **Employee Management**

- Complete employee lifecycle (onboarding, promotions, transfers, termination)
- Organizational hierarchy and reporting structure
- Department and designation management
- Employee skills and competency tracking

✅ **Authentication & Authorization**

- JWT-based authentication
- Role-based access control (RBAC)
- Secure token management

✅ **Integration & Communication**

- Event-driven architecture with RabbitMQ
- RESTful APIs with OpenAPI/Swagger documentation
- Async event publishing for cross-service communication

✅ **Cloud-Native & Production-Ready**

- Docker containerization
- Kubernetes orchestration
- Helm charts for templated deployments
- Terraform infrastructure-as-code
- Comprehensive CI/CD pipeline with GitHub Actions

## 🏗️ Architecture

### System Architecture

```
 ┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (React)                          │
│                   (apps/frontend)                                │
└──────────────────────┬──────────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│                     API Gateway                                  │
│                   (apps/gateway)                                 │
└──────────────────────┬──────────────────────────────────────────┘
                       │
     ┌─────────────────┼─────────────────┐
     │                 │                 │
┌────▼──────┐   ┌──────▼──────┐   ┌─────▼─────┐
│   Auth     │   │  Employee   │   │  Services │
│  Service   │   │  Service    │   │  (Future) │
│   :8080    │   │   :8082     │   │           │
└────┬──────┘   └──────┬──────┘   └─────┬─────┘
     │                 │                │
     └─────────────────┼────────────────┘
                       │
            ┌──────────▼──────────┐
            │    RabbitMQ         │
            │  (Event Bus)        │
            └─────────────────────┘
                       │
          ┌────────────┼────────────┐
          │            │            │
    ┌─────▼──┐  ┌──────▼────┐  ┌───▼──────┐
    │ Database│ │ Cache/Auth│ │ Services │
    │PostgreSQL│ │  (Redis)  │ │  (Future)│
    └─────────┘  └───────────┘  └──────────┘
```

### Deployment Architecture

```
┌──────────────────────────────────────────────────┐
│          GitHub Repository                       │
│  (with GitHub Actions CI/CD Workflow)            │
└──────────────┬───────────────────────────────────┘
               │
     ┌─────────▼──────────┐
     │  CI/CD Pipeline    │
     │ - Build            │
     │ - Test             │
     │ - Security Scan    │
     │ - Push Image       │
     └─────────┬──────────┘
               │
   ┌───────────┼────────────┐
   │           │            │
┌──▼────┐ ┌────▼──┐  ┌─────▼──┐
│ Dev   │ │Staging│  │ Prod   │
│Cluster│ │Cluster│  │Cluster │
└───────┘ └───────┘  └────────┘
   │         │          │
 Helm      Helm       Helm
 Deploy    Deploy     Deploy
```

## 🔧 Services

### 1. Auth Service (`apps/auth-service`)

**Port:** 8080

**Responsibilities:**

- User authentication and authorization
- JWT token generation and validation
- Role-based access control (RBAC)
- Service-to-service authentication

**Technology Stack:**

- Spring Boot 4.0.6
- Spring Security
- JWT (jjwt 0.12.3)
- PostgreSQL

**Key Endpoints:**

- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh token
- `GET /api/v1/auth/validate` - Validate token

### 2. Employee Service (`apps/employee-service`)

**Port:** 8082

**Responsibilities:**

- Employee lifecycle management (onboarding, promotion, transfer, termination)
- Department and designation management
- Organizational hierarchy and reporting structure
- Employee skills and competency tracking
- Employment history audit trail

**Technology Stack:**

- Spring Boot 4.0.6
- Spring Data JPA
- PostgreSQL
- RabbitMQ

**Key Endpoints:**

- `POST /api/v1/employees/onboard` - Onboard new employee
- `GET /api/v1/employees/{employeeId}` - Get employee profile
- `POST /api/v1/employees/{employeeId}/promote` - Promote employee
- `POST /api/v1/employees/{employeeId}/transfer` - Transfer employee
- `POST /api/v1/employees/{employeeId}/terminate` - Terminate employee

**Domain Events Published:**

- `EmployeeCreatedEvent` - When new employee is onboarded
- `EmployeeUpdatedEvent` - When employee profile is updated
- `EmployeeTransferredEvent` - When employee is promoted/transferred
- `EmployeeTerminatedEvent` - When employee is terminated

### 3. API Gateway (`apps/gateway`)

**Port:** 3000

**Responsibilities:**

- API request routing to microservices
- Request/response logging and monitoring
- Error handling and transformation
- Health checks and service discovery

**Technology Stack:**

- Node.js
- Express.js
- Axios

### 4. Frontend (`apps/frontend`)

**Port:** 3000

**Responsibilities:**

- User interface for HRMS
- Dashboard and analytics
- Employee management forms
- Responsive design

**Technology Stack:**

- React 18+
- Next.js
- TypeScript
- Tailwind CSS
- Shadcn UI

## 🚀 Quick Start

### Prerequisites

- Java 21 (Temurin Eclipse)
- Maven 3.9.6+
- Docker & Docker Compose
- PostgreSQL 15+
- Node.js 18+
- Git

### Local Development Setup

#### 1. Clone Repository

```bash
git clone <repository-url>
cd AtlasHR
```

#### 2. Set Up Environment Variables

```bash
cd apps/employee-service
cp .env.example .env
# Edit .env with your settings
```

#### 3. Start Dependencies with Docker Compose

```bash
cd apps/employee-service
docker-compose up -d

# Verify services are running
docker-compose ps
```

#### 4. Run Auth Service

```bash
cd apps/auth-service
mvn spring-boot:run
```

#### 5. Run Employee Service

```bash
cd apps/employee-service
mvn spring-boot:run
```

#### 6. Run API Gateway

```bash
cd apps/gateway
npm install
npm start
```

#### 7. Run Frontend

```bash
cd apps/frontend
npm install
npm run dev
```

#### 8. Access Services

- **Frontend:** http://localhost:3000
- **Auth Service:** http://localhost:8080
- **Employee Service:** http://localhost:8082
- **API Gateway:** http://localhost:3000/api
- **Health Checks:**
  - Auth: http://localhost:8080/actuator/health
  - Employee: http://localhost:8082/actuator/health
- **PostgreSQL:** localhost:5432
- **RabbitMQ Management:** http://localhost:15672 (guest/guest)

### Docker Compose Only

```bash
cd apps/employee-service

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 📚 Documentation

### Root Level Guides

| Document                                          | Purpose                                                              |
| ------------------------------------------------- | -------------------------------------------------------------------- |
| [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)         | Complete deployment instructions for Kubernetes, Helm, and Terraform |
| [GITHUB_SECRETS_SETUP.md](./GITHUB_SECRETS_SETUP.md) | GitHub Secrets configuration for CI/CD pipeline                      |
| [CONFIG_GUIDE.md](./CONFIG_GUIDE.md)                 | Environment variables, configuration, and security settings          |

### Service-Specific Documentation

**Employee Service:**

- [apps/employee-service/README.md](./apps/employee-service/README.md) - Service overview and architecture
- [apps/employee-service/QUICK_REFERENCE.md](./apps/employee-service/QUICK_REFERENCE.md) - cURL examples
- [apps/employee-service/TESTING_GUIDE.md](./apps/employee-service/TESTING_GUIDE.md) - Unit and integration testing
- [apps/employee-service/COMPLETION_SUMMARY.md](./apps/employee-service/COMPLETION_SUMMARY.md) - Implementation checklist

**Auth Service:**

- [apps/auth-service/README.md](./apps/auth-service/README.md) - Authentication service documentation

### Infrastructure & DevOps

- [infrastructure/kubernetes/](./infrastructure/kubernetes/) - Kubernetes manifests
- [infrastructure/helm/](./infrastructure/helm/) - Helm charts for templated deployments
- [infrastructure/terraform/](./infrastructure/terraform/) - Infrastructure-as-code
- [.github/workflows/](./github/workflows/) - CI/CD pipeline definitions

## 💻 Development

### Project Structure

```
AtlasHR/
├── apps/
│   ├── auth-service/          # Authentication microservice
│   ├── employee-service/      # Employee management microservice
│   ├── frontend/              # React/Next.js frontend
│   └── gateway/               # API Gateway (Node.js)
├── infrastructure/
│   ├── kubernetes/            # K8s manifests
│   ├── helm/                  # Helm charts
│   ├── terraform/             # Infrastructure-as-code
│   └── docker/                # Docker Compose
├── libs/
│   ├── auth-lib/              # Authentication library (shared)
│   ├── common-lib/            # Common utilities
│   ├── config/                # Configuration library
│   ├── logger/                # Logging library
│   └── shared-types/          # Shared types/DTOs
└── docs/                      # General documentation
```

### Running Tests

#### Employee Service Tests

```bash
cd apps/employee-service

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=EmployeeServiceTest

# Run with coverage
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

#### Frontend Tests

```bash
cd apps/frontend

# Run tests
npm test

# Run with coverage
npm test -- --coverage

# Run E2E tests
npm run e2e
```

### Code Style & Quality

#### Java (Maven Checkstyle)

```bash
# Check style
mvn checkstyle:check

# View violations
mvn checkstyle:checkstyle
open target/site/checkstyle.html
```

#### TypeScript/JavaScript (ESLint)

```bash
cd apps/frontend

# Check style
npm run lint

# Fix issues
npm run lint:fix
```

#### Code Formatting

```bash
# Java - Format code
mvn spotless:apply

# JavaScript - Format code
npm run format
```

### Git Hooks

Pre-commit hooks are configured to:

- Validate Maven pom.xml
- Check code style
- Prevent debug statements (console.log, System.out.println)
- Validate Kubernetes YAML
- Validate Helm charts

```bash
# Install pre-commit hooks
chmod +x .git/hooks/pre-commit
chmod +x .git/hooks/pre-push
```

## 📦 Deployment

### Development Deployment

```bash
# Using Helm (recommended)
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values-dev.yaml

# Or using kubectl
kubectl apply -f infrastructure/kubernetes/employee-service/
```

### Staging Deployment

```bash
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values-staging.yaml
```

### Production Deployment

```bash
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values-prod.yaml
```

### Using GitHub Actions

1. **Configure GitHub Secrets** (see [GITHUB_SECRETS_SETUP.md](./GITHUB_SECRETS_SETUP.md))
2. **Push to repository:** `git push origin main`
3. **Monitor workflow:** GitHub Actions → Workflows → Employee Service
4. **Manual triggers:** Staging and Production deployments require manual approval

## 🔐 Security

### Implemented Security Measures

✅ JWT token-based authentication
✅ Role-based access control (RBAC)
✅ Database secrets management
✅ Network policies in Kubernetes
✅ Container image scanning (Trivy)
✅ Secure HTTPS/TLS configuration
✅ Non-root container users
✅ Resource limits and quotas

### Security Best Practices

1. **Secrets Management**

   - Never commit secrets to repository
   - Use GitHub Secrets or Kubernetes Secrets
   - Rotate credentials regularly
2. **Network Security**

   - Configure network policies
   - Use service mesh (optional: Istio)
   - Enable mutual TLS
3. **Image Security**

   - Scan images regularly (Trivy)
   - Use private Docker registry
   - Sign container images
4. **Compliance**

   - Enable audit logging
   - Implement data retention policies
   - Regular security audits

## 📊 Monitoring & Observability

### Available Metrics

- Application metrics: `/actuator/metrics`
- Prometheus metrics: `/actuator/prometheus`
- Health status: `/actuator/health`
- Application info: `/actuator/info`

### Logging

Logs are collected from:

- Application logs: `logs/employee-service.log`
- Kubernetes logs: `kubectl logs <pod>`
- Docker logs: `docker logs <container>`

### Alerting

Configure alerts for:

- Pod restart failures
- High CPU/memory usage
- Database connection failures
- API response time degradation

## 🤝 Contributing

### Branch Strategy

- `main` - Production-ready code
- `develop` - Development branch
- `feature/*` - Feature branches
- `bugfix/*` - Bug fix branches

### Pull Request Process

1. Create feature branch: `git checkout -b feature/description`
2. Make changes and commit: `git commit -m "feat: description"`
3. Push to branch: `git push origin feature/description`
4. Create Pull Request
5. Code review and CI/CD checks pass
6. Merge to develop or main

### Conventional Commits

Use conventional commit format:

```
feat: add new feature
fix: fix bug
docs: documentation updates
test: add tests
chore: maintenance tasks
```

## 📞 Support

### Common Issues

| Issue                      | Solution                                                     |
| -------------------------- | ------------------------------------------------------------ |
| Database connection failed | Check PostgreSQL is running, verify credentials in `.env`  |
| Port already in use        | Kill process or change port in `application.properties`    |
| Maven build fails          | Run `mvn clean install`, check Java version (need Java 21) |
| Docker build fails         | Check Docker daemon is running, verify Dockerfile syntax     |

### Getting Help

1. Check the documentation
2. Review existing GitHub Issues
3. Create new GitHub Issue with details
4. Contact the development team

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎯 Roadmap

- [ ] Payment integration for payroll
- [ ] Leave management system
- [ ] Performance appraisal module
- [ ] Recruitment and onboarding portal
- [ ] Mobile application (iOS/Android)
- [ ] Advanced analytics and reporting
- [ ] AI-based talent recommendations

## 👥 Team

AtlasHR is maintained by the HRMS development team. For questions or contributions, please reach out to the development team.

---

**Last Updated:** 2024
**Version:** 1.0.0
