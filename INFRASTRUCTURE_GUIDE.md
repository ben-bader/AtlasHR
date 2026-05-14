# HRMS Infrastructure Guide: Docker, Kubernetes, Helm & Terraform

## Quick Overview

Your HRMS system has three infrastructure layers for different deployment scenarios:

| Layer | Tool | Use Case | Complexity |
|-------|------|----------|-----------|
| **Local Development** | Docker Compose | Development & testing on laptop | ⭐ Low |
| **Kubernetes Native** | Kubectl + YAML manifests | Manual K8s deployment | ⭐⭐ Medium |
| **Kubernetes Automated** | Helm Charts | Repeatable, templated K8s deployments | ⭐⭐ Medium |
| **Infrastructure as Code** | Terraform | Provision K8s + deploy via Helm | ⭐⭐⭐ High (most powerful) |

---

## 1. Docker Compose (For Local Development)

### What It Does
Runs entire HRMS stack locally using containers - perfect for development, testing, and debugging.

### Services
- **PostgreSQL**: Database for auth and employee services
- **RabbitMQ**: Message broker for inter-service communication
- **Redis**: Cache/session store
- **Auth Service**: Spring Boot microservice (port 8081)
- **Employee Service**: Spring Boot microservice (port 8083)
- **Gateway Service**: Spring Cloud Gateway (port 8084)
- **Frontend**: Next.js React app (port 3000)

### How to Run
```bash
# From project root
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# View logs
docker-compose -f infrastructure/docker/docker-compose.yml logs -f

# Stop everything
docker-compose -f infrastructure/docker/docker-compose.yml down
```

### Benefits
✅ Single command to start entire system  
✅ Consistent environment across team  
✅ Instant development/debugging  
✅ No K8s complexity  

---

## 2. Kubernetes Manifests (Raw YAML)

### What They Do
Kubernetes manifests define how to deploy services on Kubernetes clusters. Each service gets:
- **Deployment**: How many replicas, container config, resource limits
- **Service**: Network access (ClusterIP for internal, LoadBalancer/NodePort for external)
- **Namespace**: Logical isolation (hrms namespace contains all services)
- **ConfigMap**: Environment configuration
- **Secret**: Sensitive data (passwords, tokens)
- **HPA**: Auto-scaling rules based on CPU/memory

### Structure
```
kubernetes/
├── auth-service/           # Auth service manifests
│   ├── namespace.yaml      # Create 'hrms' namespace
│   ├── auth-deployment.yaml
│   ├── auth-service.yaml
│   ├── postgres*.yaml      # Database for auth
│   └── rabbitmq*.yaml      # Message broker
├── employee-service/       # Employee service manifests
│   ├── employee-deployment.yaml
│   ├── employee-service.yaml
│   ├── employee-hpa.yaml   # Auto-scale rules
│   └── employee-configmap.yaml
└── gateway-service/        # (TO BE CREATED)
    ├── gateway-deployment.yaml
    └── gateway-service.yaml
```

### How to Deploy
```bash
# Create namespace
kubectl apply -f kubernetes/auth-service/namespace.yaml

# Deploy all auth service resources
kubectl apply -f kubernetes/auth-service/

# Deploy all employee service resources
kubectl apply -f kubernetes/employee-service/

# Deploy gateway
kubectl apply -f kubernetes/gateway-service/

# Verify deployments
kubectl get deployments -n hrms
kubectl get services -n hrms
kubectl get pods -n hrms

# View logs
kubectl logs -n hrms deployment/auth-service
kubectl logs -n hrms deployment/employee-service
```

### Benefits
✅ Version controlled infrastructure  
✅ Works on any Kubernetes cluster (minikube, EKS, AKS, GKE)  
✅ Understands K8s primitives directly  
❌ No templating - must duplicate for dev/staging/prod  
❌ Manual updates to each environment  

---

## 3. Helm Charts (Templated Kubernetes Deployments)

### What They Do
Helm is a **package manager for Kubernetes**. It wraps K8s manifests with templating to:
- Avoid duplicating manifests for dev/staging/prod
- Use `values.yaml` to customize deployments easily
- Share reusable, versioned packages

### Benefits Over Raw Kubernetes
✅ **Templating**: One Chart works for dev/staging/prod with different `values.yaml`  
✅ **Reusability**: Share charts across projects  
✅ **Variables**: Change image tag, replicas, resources without editing YAML  
✅ **Hooks**: Run scripts before/after deployment (migrations, backups)  

### Structure
```
helm/
├── auth-service/
│   ├── Chart.yaml          # Metadata (name, version)
│   ├── values.yaml         # Default config
│   └── templates/          # K8s manifest templates
│       ├── deployment.yaml
│       ├── service.yaml
│       └── configmap.yaml
├── employee-service/
│   ├── Chart.yaml
│   ├── values.yaml
│   ├── values-dev.yaml     # Dev-specific overrides
│   ├── values-staging.yaml # Staging overrides
│   ├── values-prod.yaml    # Prod overrides
│   └── templates/
└── gateway-service/        # (TO BE CREATED)
```

### How to Deploy
```bash
# Install chart from local directory
helm install auth-service ./helm/auth-service -n hrms --create-namespace

# Install with custom values for staging
helm install emp-staging ./helm/employee-service \
  -f ./helm/employee-service/values-staging.yaml \
  -n hrms

# Upgrade an existing release
helm upgrade auth-service ./helm/auth-service -n hrms

# Uninstall
helm uninstall auth-service -n hrms

# List installed releases
helm list -n hrms

# View rendered manifests (what kubectl will apply)
helm template auth-service ./helm/auth-service -n hrms
```

### When to Use Helm
✅ Multiple environments (dev/staging/prod)  
✅ Team sharing charts  
✅ Complex deployments with many config variations  
✅ GitOps workflows  

---

## 4. Terraform (Infrastructure as Code)

### What It Does
Terraform provisions **entire infrastructure** as code:
1. Creates Kubernetes cluster (or assumes it exists)
2. Creates namespaces, secrets, ConfigMaps
3. **Deploys Helm charts** (Terraform orchestrates Helm)
4. Manages infrastructure state (avoids manual changes)
5. Makes environment setup repeatable and auditable

### Workflow
```
terraform/
├── backend.tf          # Where Terraform state is stored (S3, local, etc.)
├── main.tf            # Primary config (providers, resources)
├── variables.tf       # Input variables (image tag, cluster name, etc.)
├── outputs.tf         # What Terraform exports
└── terraform.tfvars   # Actual values for variables (sensitive, not in git)
```

### How Terraform + Helm Work Together
```
Terraform (orchestrator)
  ↓
  Deploys: namespace, secrets, persistent volumes
  ↓
  Calls: Helm (via terraform helm provider)
    ↓
    Helm renders: deployment.yaml from templates
    ↓
    kubectl apply: deployment to cluster
```

### How to Use
```bash
# Navigate to terraform directory
cd infrastructure/terraform/employee-service

# Initialize Terraform (download providers)
terraform init

# Validate syntax
terraform validate

# See what changes will be made
terraform plan

# Apply changes to infrastructure
terraform apply

# View current state
terraform show

# Destroy infrastructure (CAREFUL!)
terraform destroy

# Output values (IPs, endpoints, etc.)
terraform output
```

### Benefits
✅ **Reproducible**: Same infrastructure every time  
✅ **Versionable**: Track all infrastructure changes in git  
✅ **Auditable**: See exactly what changed and when  
✅ **Idempotent**: Run multiple times, same result  
✅ **Destroy on demand**: Clean up resources (save $$)  

---

## 5. Which Should I Use?

### Local Development
```bash
docker-compose -f infrastructure/docker/docker-compose.yml up
```
**→ Fast, simple, no K8s needed**

### Testing K8s Locally (Minikube)
```bash
# Option A: Raw manifests
kubectl apply -f kubernetes/

# Option B: Helm (better if testing multiple configs)
helm install auth ./helm/auth-service -n hrms --create-namespace
```

### Staging Environment
```bash
# Use Helm with staging values
helm install emp ./helm/employee-service \
  -f ./helm/employee-service/values-staging.yaml \
  -n hrms

# Or use Terraform to deploy with Helm
cd infrastructure/terraform
terraform apply -var-file=staging.tfvars
```

### Production Environment
```bash
# Use Terraform for full infrastructure control
cd infrastructure/terraform
terraform apply -var-file=prod.tfvars

# Or use Helm directly for production
helm install emp ./helm/employee-service \
  -f ./helm/employee-service/values-prod.yaml \
  -n hrms-prod
```

---

## 6. Complete Deployment Checklist

### Step 1: Prerequisites
- [ ] Docker & Docker Compose installed
- [ ] Kubernetes cluster (minikube for local, EKS/AKS for cloud)
- [ ] kubectl configured
- [ ] Helm 3 installed
- [ ] Terraform installed (optional, for IaC)

### Step 2: Prepare Environment
```bash
# Build Docker images
docker-compose -f infrastructure/docker/docker-compose.yml build

# Or build individually:
docker build -t hrms/auth-service:latest apps/auth-service/
docker build -t hrms/employee-service:latest apps/employee-service/
docker build -t hrms/gateway-service:latest apps/gateway-service/
```

### Step 3: Deploy to Local K8s (Minikube)
```bash
# Start minikube
minikube start

# Create namespace
kubectl apply -f kubernetes/auth-service/namespace.yaml

# Deploy using Helm
helm install auth ./helm/auth-service -n hrms --create-namespace
helm install emp ./helm/employee-service -n hrms
helm install gateway ./helm/gateway-service -n hrms
```

### Step 4: Verify & Access
```bash
# Check all pods are running
kubectl get pods -n hrms

# Forward ports for local access
kubectl port-forward -n hrms svc/auth-service 8081:8080
kubectl port-forward -n hrms svc/employee-service 8083:8080
kubectl port-forward -n hrms svc/gateway-service 8084:8080

# Access services
curl http://localhost:8084/api/auth/login
```

---

## 7. Key Configuration Files You Need

### For Each Service
- **Docker**: Already have `Dockerfile` in each service
- **Kubernetes**: Raw YAML manifests in `kubernetes/`
- **Helm**: Templates in `helm/*/templates/`
- **Environment**: Set via `values.yaml` (Helm) or `ConfigMap` (K8s)

### Environment Variables Needed
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/hrms_auth
SPRING_DATASOURCE_USERNAME=hrms
SPRING_DATASOURCE_PASSWORD=hrms_pass

# RabbitMQ
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=hrms
SPRING_RABBITMQ_PASSWORD=hrms_pass

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379

# JWT
JWT_SECRET=your_secret_key_min_32_chars
JWT_EXPIRATION=86400000

# Frontend
FRONTEND_URL=http://localhost:3000
```

---

## 8. Troubleshooting

### Pods won't start
```bash
# Check pod status
kubectl describe pod <pod-name> -n hrms

# View logs
kubectl logs <pod-name> -n hrms

# Check events
kubectl get events -n hrms --sort-by='.lastTimestamp'
```

### Services not communicating
```bash
# Check service DNS
kubectl exec <pod-name> -n hrms -- nslookup auth-service

# Test connectivity
kubectl exec <pod-name> -n hrms -- curl http://auth-service:8080/health
```

### Terraform state issues
```bash
# Reset state (CAREFUL!)
terraform destroy -force
rm -rf .terraform terraform.tfstate*

# Reinitialize
terraform init
```

---

## 9. Development Workflow Recommendation

### Daily Development (Without K8s)
```bash
# Use Docker Compose
docker-compose -f infrastructure/docker/docker-compose.yml up -d
# Make code changes
# Services auto-reload
```

### Before Commit
```bash
# Test with K8s locally
minikube start
helm install auth ./helm/auth-service -n hrms --create-namespace
# Verify all services work
# Fix any issues
```

### Deployment to Staging
```bash
# Push code to repo
git push

# Deploy via CI/CD or manually
cd infrastructure/terraform
terraform apply -var-file=staging.tfvars
```

### Deployment to Production
```bash
# Code review + tests pass
# Tag release
git tag v1.0.0

# Deploy
cd infrastructure/terraform
terraform apply -var-file=prod.tfvars
```

---

## Next Steps

1. **Complete Helm charts** for all services (gateway is missing)
2. **Create Terraform state backend** (S3 or Terraform Cloud)
3. **Set up CI/CD pipeline** (GitHub Actions, GitLab CI)
4. **Add database migrations** to Helm hooks
5. **Implement monitoring** (Prometheus, Grafana)

