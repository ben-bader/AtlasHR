# Infrastructure Configuration for HRMS

Complete infrastructure setup for HRMS microservices system with Docker, Kubernetes, Helm, and Terraform.

## 📚 Documentation

Start with one of these guides based on your needs:

1. **[INFRASTRUCTURE_GUIDE.md](../INFRASTRUCTURE_GUIDE.md)** ← **START HERE**
   - Comprehensive overview of all infrastructure tools
   - What each tool does and when to use it
   - Benefits and limitations of each approach

2. **[QUICKSTART.md](../QUICKSTART.md)**
   - Quick reference with commands
   - Copy-paste ready commands for each scenario
   - Troubleshooting guide

3. **[DEPLOYMENT_GUIDE.md](../DEPLOYMENT_GUIDE.md)**
   - Visual decision trees and architecture diagrams
   - Development workflow recommendations
   - Scaling and disaster recovery strategies

4. **[INFRASTRUCTURE_SETUP.md](../INFRASTRUCTURE_SETUP.md)**
   - Files created and modified
   - What changed and why
   - Next steps after setup

## 🚀 Quick Start

### Option 1: Local Development (Fastest)
```bash
cd d:/Hrms-SFE/AtlasHR
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# Access at:
# Frontend: http://localhost:3000
# Gateway API: http://localhost:8084
```

### Option 2: Local Kubernetes (Minikube)
```bash
# Start Minikube
minikube start

# Deploy using Helm
helm install auth ./infrastructure/helm/auth-service -n hrms --create-namespace
helm install emp ./infrastructure/helm/employee-service -n hrms
helm install gateway ./infrastructure/helm/gateway-service -n hrms

# Check status
kubectl get pods -n hrms
```

### Option 3: Production with Terraform
```bash
cd infrastructure/terraform

# Plan
terraform plan -var-file=dev.tfvars

# Apply
terraform apply -var-file=dev.tfvars
```

## 📁 Directory Structure

```
infrastructure/
│
├── docker/
│   ├── docker-compose.yml          # Local development stack
│   └── init-db.sql                 # Database initialization
│
├── kubernetes/                     # Raw K8s manifests (optional)
│   ├── auth-service/
│   ├── employee-service/
│   └── gateway-service/            # NEW: Gateway K8s manifests
│
├── helm/                           # Helm charts (recommended for K8s)
│   ├── auth-service/
│   ├── employee-service/
│   └── gateway-service/            # NEW: Gateway Helm chart
│
└── terraform/                      # Infrastructure as Code (for prod)
    ├── main.tf                     # Resource definitions
    ├── variables.tf                # Input variables
    ├── outputs.tf                  # Output values
    ├── backend.tf                  # State backend config
    ├── dev.tfvars                  # Dev environment
    ├── staging.tfvars              # Staging environment
    └── prod.tfvars                 # Production environment
```

## 🎯 Choose Your Path

| Goal | Tool | Command |
|------|------|---------|
| **Develop locally right now** | Docker Compose | `docker-compose -f infrastructure/docker/docker-compose.yml up` |
| **Test on Kubernetes (local)** | Helm + Minikube | `helm install auth ./infrastructure/helm/auth-service -n hrms` |
| **Deploy to staging** | Terraform + Helm | `terraform apply -var-file=staging.tfvars` |
| **Deploy to production** | Terraform + Helm | `terraform apply -var-file=prod.tfvars` |

## 🔧 Key Files

### Docker Compose
- **File**: `docker/docker-compose.yml`
- **Purpose**: Local development and testing
- **Services**: PostgreSQL, RabbitMQ, Redis, Auth Service, Employee Service, Gateway, Frontend

### Kubernetes Manifests
- **Directory**: `kubernetes/*/`
- **Use When**: Deploying directly to K8s without templating
- **Alternative**: Use Helm instead (recommended)

### Helm Charts
- **Directory**: `helm/*/`
- **Key Files**:
  - `Chart.yaml` - Chart metadata
  - `values.yaml` - Default configuration
  - `templates/` - K8s manifests with variables
- **Use When**: Multiple environments, team collaboration, production

### Terraform
- **Directory**: `terraform/`
- **Key Files**:
  - `main.tf` - All resource definitions
  - `variables.tf` - Input parameters
  - `outputs.tf` - Exported values
  - `*tfvars` - Environment-specific values
- **Use When**: Production infrastructure, IaC practices, automation

## 📊 Services Architecture

```
Frontend (React/Next.js)
        ↓ HTTP
    API Gateway (Spring Cloud Gateway) - Port 8084
        ↓
    ┌───┴────────────────┐
    ↓                    ↓
Auth Service      Employee Service
Port 8081         Port 8083
    │                  │
    └────┬─────────────┘
         ↓ AMQP
    RabbitMQ (Message Broker)
         ↓
    ┌────┼────┐
    ↓    ↓    ↓
  PostgreSQL Redis RabbitMQ
```

## 🔐 Security Configuration

### Secrets Management
All sensitive data is managed via Kubernetes Secrets:
- **JWT_SECRET**: For token signing
- **Database passwords**: PostgreSQL credentials
- **RabbitMQ credentials**: Message broker access
- **Redis password**: (Optional) Cache authentication

### Environment-Specific Configuration
- **Development**: Minimal security (quick setup)
- **Staging**: Production-like (validation)
- **Production**: Strict security (encrypted, audited)

## 🚀 Deployment Workflow

### Development
```
1. Make code changes
2. docker-compose up
3. Test locally
4. git commit
```

### Staging
```
1. Build Docker images
2. Push to registry
3. Terraform apply -var-file=staging.tfvars
4. Run integration tests
```

### Production
```
1. Code review + approved
2. Tag release (v1.0.0)
3. Build & push images
4. Terraform apply -var-file=prod.tfvars
5. Monitor health checks
6. Verify all systems operational
```

## 📋 Prerequisites

### For Docker Compose
- Docker Desktop installed
- 4GB+ RAM available

### For Kubernetes
- kubectl installed
- Kubernetes cluster (minikube for local, EKS/AKS for cloud)
- kubeconfig configured

### For Helm
- Helm 3+ installed
- kubectl configured

### For Terraform
- Terraform 1.0+ installed
- kubectl configured
- Cloud CLI (AWS, Azure) - optional for cloud deployments

## 🔍 Common Commands

### Docker Compose
```bash
# Start
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# Logs
docker-compose -f infrastructure/docker/docker-compose.yml logs -f

# Stop
docker-compose -f infrastructure/docker/docker-compose.yml down
```

### Kubernetes / Helm
```bash
# Create namespace
kubectl create namespace hrms

# Deploy with Helm
helm install auth ./infrastructure/helm/auth-service -n hrms
helm install emp ./infrastructure/helm/employee-service -n hrms

# View deployments
kubectl get deployments -n hrms
kubectl get pods -n hrms
kubectl get services -n hrms

# View logs
kubectl logs -n hrms deployment/auth-service

# Port forward
kubectl port-forward -n hrms svc/gateway-service 8084:8084
```

### Terraform
```bash
# Initialize
cd infrastructure/terraform
terraform init

# Plan
terraform plan -var-file=dev.tfvars

# Apply
terraform apply -var-file=dev.tfvars

# Destroy
terraform destroy -var-file=dev.tfvars
```

## 📖 Learn More

- [INFRASTRUCTURE_GUIDE.md](../INFRASTRUCTURE_GUIDE.md) - Complete guide with explanations
- [QUICKSTART.md](../QUICKSTART.md) - Quick reference with commands
- [DEPLOYMENT_GUIDE.md](../DEPLOYMENT_GUIDE.md) - Decision trees and workflows
- [INFRASTRUCTURE_SETUP.md](../INFRASTRUCTURE_SETUP.md) - Summary of changes

## ✅ Checklist Before Deployment

- [ ] Docker images built and tagged
- [ ] Kubernetes cluster available and configured
- [ ] kubectl properly set up
- [ ] Helm installed (version 3+)
- [ ] Terraform installed (version 1.0+)
- [ ] Secrets prepared (JWT_SECRET, DB password, etc.)
- [ ] Kubeconfig context configured for target cluster
- [ ] All services tested locally with docker-compose
- [ ] Helm templates validated: `helm template <chart>`
- [ ] Terraform plan reviewed: `terraform plan`

## 🆘 Troubleshooting

### Pods not starting
```bash
kubectl describe pod <pod-name> -n hrms
kubectl logs <pod-name> -n hrms
```

### Services not communicating
```bash
kubectl exec -it -n hrms <pod> -- curl http://other-service:8080/health
```

### Terraform errors
```bash
terraform validate
terraform plan -var-file=dev.tfvars
TF_LOG=DEBUG terraform apply
```

## 📞 Support

See documentation files for detailed troubleshooting:
- [INFRASTRUCTURE_GUIDE.md](../INFRASTRUCTURE_GUIDE.md#8-troubleshooting)
- [QUICKSTART.md](../QUICKSTART.md#-troubleshooting)

---

**For detailed information, start with [INFRASTRUCTURE_GUIDE.md](../INFRASTRUCTURE_GUIDE.md)**
