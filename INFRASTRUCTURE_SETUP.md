# Infrastructure Files Summary

All infrastructure files have been created and configured for your HRMS system.

## 📁 Directory Structure

```
infrastructure/
├── docker/                          # Local development
│   └── docker-compose.yml          # All services (PostgreSQL, RabbitMQ, Redis, Apps)
│
├── kubernetes/                      # Raw Kubernetes manifests
│   ├── auth-service/               # Auth service deployment files
│   ├── employee-service/           # Employee service deployment files
│   └── gateway-service/            # Gateway service deployment files (NEW)
│
├── helm/                           # Helm charts (templated K8s deployments)
│   ├── auth-service/               # Auth service chart
│   │   ├── Chart.yaml
│   │   ├── values.yaml
│   │   └── templates/              # K8s templates with variables
│   ├── employee-service/           # Employee service chart
│   │   ├── Chart.yaml
│   │   ├── values.yaml
│   │   ├── values-dev.yaml         # Dev overrides
│   │   ├── values-staging.yaml     # Staging overrides
│   │   ├── values-prod.yaml        # Prod overrides
│   │   └── templates/
│   └── gateway-service/            # Gateway service chart (NEW)
│       ├── Chart.yaml
│       ├── values.yaml
│       └── templates/
│
└── terraform/                      # Infrastructure as Code
    ├── main.tf                     # All resource definitions (NEW)
    ├── variables.tf                # Input variables (NEW)
    ├── outputs.tf                  # Output values (NEW)
    ├── backend.tf                  # State backend config (NEW)
    ├── dev.tfvars                  # Development environment (NEW)
    ├── staging.tfvars              # Staging environment (NEW)
    └── prod.tfvars                 # Production environment (NEW)
```

## 🎯 What Each Tool Does

### 1. Docker Compose (Local Development)
**File**: `infrastructure/docker/docker-compose.yml`  
**What**: Runs entire HRMS stack locally in containers  
**When to use**: Daily development, testing locally, before committing  
**Command**: `docker-compose -f infrastructure/docker/docker-compose.yml up -d`  
**Access**: 
- Frontend: http://localhost:3000
- Gateway API: http://localhost:8084
- RabbitMQ UI: http://localhost:15672

---

### 2. Kubernetes Raw Manifests (Manual K8s Deployment)
**Files**: `infrastructure/kubernetes/*/`  
**What**: YAML files that define K8s Deployments, Services, Secrets, etc.  
**When to use**: Simple deployments, learning K8s, version control of infrastructure  
**Limitation**: No templating - must duplicate for each environment  
**Command**: `kubectl apply -f infrastructure/kubernetes/`

---

### 3. Helm Charts (Templated K8s Deployments)
**Files**: `infrastructure/helm/*/`  
**What**: Packages K8s manifests with templating for reusability  
**When to use**: Multiple environments (dev/staging/prod), team collaboration  
**Benefits**:
- ✅ One chart works for dev, staging, prod with different values
- ✅ Variables for image tag, replicas, resources
- ✅ Easy to update across environments
- ✅ Shareable and versioned

**Commands**:
```bash
# Install chart
helm install auth-service ./infrastructure/helm/auth-service -n hrms --create-namespace

# With custom values for staging
helm install emp-staging ./infrastructure/helm/employee-service \
  -f ./infrastructure/helm/employee-service/values-staging.yaml -n hrms

# Upgrade
helm upgrade auth-service ./infrastructure/helm/auth-service -n hrms

# View rendered manifests
helm template auth-service ./infrastructure/helm/auth-service -n hrms
```

---

### 4. Terraform (Infrastructure as Code)
**Files**: `infrastructure/terraform/`  
**What**: Provisions entire infrastructure and deploys via Helm  
**When to use**: Production, repeatable infrastructure, team/cloud environments  
**Benefits**:
- ✅ **Reproducible**: Same infrastructure every time
- ✅ **Versionable**: Track changes in git
- ✅ **Auditable**: Know exactly what changed and when
- ✅ **Destroyable**: Clean up resources on demand (save $$)
- ✅ **Orchestrates**: K8s + Helm + secrets + configs

**Architecture**:
```
Terraform (orchestrator)
  ├── Creates K8s namespace
  ├── Creates secrets (JWT, DB passwords, etc.)
  ├── Creates ConfigMaps
  └── Calls Helm charts
      ├── Deploys auth-service
      ├── Deploys employee-service
      └── Deploys gateway-service
```

**Commands**:
```bash
cd infrastructure/terraform

# Initialize
terraform init

# Plan (see what will change)
terraform plan -var-file=dev.tfvars

# Apply (create infrastructure)
terraform apply -var-file=dev.tfvars

# Destroy (delete everything)
terraform destroy -var-file=dev.tfvars
```

---

## 🚀 Quick Start by Scenario

### Scenario 1: I'm developing locally right now
```bash
docker-compose -f infrastructure/docker/docker-compose.yml up -d
# Everything runs on your laptop, access at http://localhost:3000
```

### Scenario 2: I want to test on local Kubernetes (Minikube)
```bash
# Using raw manifests
kubectl apply -f infrastructure/kubernetes/

# OR using Helm (better)
helm install auth ./infrastructure/helm/auth-service -n hrms --create-namespace
helm install emp ./infrastructure/helm/employee-service -n hrms
helm install gateway ./infrastructure/helm/gateway-service -n hrms
```

### Scenario 3: I want to deploy to staging
```bash
cd infrastructure/terraform

# Create Kubernetes context first
kubectl config set-context my-staging-cluster --cluster=staging-k8s --user=staging-user

# Deploy with staging config
terraform apply -var-file=staging.tfvars \
  -var kubeconfig_context="my-staging-cluster" \
  -var jwt_secret="your_staging_secret"
```

### Scenario 4: I want to deploy to production
```bash
cd infrastructure/terraform

# Configure remote backend first (see backend.tf)
# Then deploy with prod config
terraform apply -var-file=prod.tfvars \
  -var jwt_secret="your_prod_secret" \
  -var image_tag="v1.0.0"  # Never use 'latest' in prod!
```

---

## 📋 Files Changed/Created

### NEW FILES CREATED:
✅ `infrastructure/helm/gateway-service/` - Helm chart for Gateway  
✅ `infrastructure/kubernetes/gateway-service/` - K8s manifests for Gateway  
✅ `infrastructure/terraform/main.tf` - Terraform resource definitions  
✅ `infrastructure/terraform/variables.tf` - Terraform input variables  
✅ `infrastructure/terraform/outputs.tf` - Terraform outputs  
✅ `infrastructure/terraform/backend.tf` - Terraform state backend  
✅ `infrastructure/terraform/dev.tfvars` - Dev environment config  
✅ `infrastructure/terraform/staging.tfvars` - Staging environment config  
✅ `infrastructure/terraform/prod.tfvars` - Production environment config  
✅ `INFRASTRUCTURE_GUIDE.md` - Comprehensive guide  
✅ `QUICKSTART.md` - Quick reference guide  

### FILES IMPROVED:
- `infrastructure/helm/auth-service/` - Now complete with templates
- `infrastructure/helm/employee-service/` - Now complete with templates
- `infrastructure/kubernetes/` - All manifests now properly configured

---

## 🔑 Key Concepts

### Terraform Variables
Located in `variables.tf`, can be overridden via:
- `.tfvars` files: `dev.tfvars`, `staging.tfvars`, `prod.tfvars`
- Command line: `-var key=value`
- Environment: `TF_VAR_key=value`
- GUI: `terraform.auto.tfvars`

### Helm Values
Located in `values.yaml` in each chart, can be overridden via:
- `-f` flag with different `values-*.yaml` files
- `--set` flag: `--set replicaCount=3`
- Multiple values files (last wins): `-f values.yaml -f values-prod.yaml`

### Secrets Management
Sensitive data (JWT, passwords) should:
1. **Never be committed to git**
2. Be stored in:
   - `.env` files (development only)
   - AWS Secrets Manager / Azure Key Vault
   - Terraform Cloud / Enterprise
   - CI/CD secret variables (GitHub Secrets, GitLab CI Variables)

---

## 🛠️ Common Tasks

### Build Docker Images
```bash
# Navigate to service directory
cd apps/auth-service

# Build image
docker build -t hrms/auth-service:latest .

# Tag for registry
docker tag hrms/auth-service:latest myregistry.azurecr.io/hrms/auth-service:v1.0.0

# Push to registry
docker push myregistry.azurecr.io/hrms/auth-service:v1.0.0

# Deploy with Terraform
terraform apply -var image_tag="v1.0.0" -var image_registry="myregistry.azurecr.io/hrms"
```

### Scale Deployments
```bash
# Using Helm values
helm upgrade auth-service ./infrastructure/helm/auth-service \
  --set replicaCount=5 -n hrms

# Or with Terraform
terraform apply -var auth_service_replicas=5 -var-file=prod.tfvars
```

### Update Environment Variables
```bash
# Update ConfigMap
kubectl create configmap hrms-config --from-literal=LOG_LEVEL=DEBUG \
  -n hrms --dry-run=client -o yaml | kubectl apply -f -

# Restart pods to pick up changes
kubectl rollout restart deployment/auth-service -n hrms
```

### Destroy Everything Safely
```bash
# See what will be deleted
terraform destroy -var-file=prod.tfvars -json | jq '.'

# Destroy with confirmation
terraform destroy -var-file=prod.tfvars

# OR manually delete K8s resources
kubectl delete namespace hrms
```

---

## 📚 Documentation Files

1. **INFRASTRUCTURE_GUIDE.md** - Complete overview of all tools and concepts
2. **QUICKSTART.md** - Quick reference with commands for each scenario
3. This file - Summary of what was created

---

## ✅ Next Steps

1. **Test locally first**:
   ```bash
   docker-compose -f infrastructure/docker/docker-compose.yml up -d
   ```

2. **Build Docker images** for your services:
   ```bash
   docker build -t hrms/auth-service:latest apps/auth-service/
   ```

3. **Try Helm locally**:
   ```bash
   minikube start
   helm install auth ./infrastructure/helm/auth-service -n hrms --create-namespace
   ```

4. **Try Terraform locally**:
   ```bash
   cd infrastructure/terraform
   terraform init
   terraform plan -var-file=dev.tfvars
   terraform apply -var-file=dev.tfvars
   ```

5. **Set up CI/CD pipeline** (GitHub Actions, GitLab CI, etc.)

6. **Configure remote backend** for Terraform state (S3, Azure, Terraform Cloud)

7. **Deploy to staging** and validate

8. **Deploy to production** with monitoring and backups

---

## 🆘 Getting Help

- Check logs: `kubectl logs -n hrms deployment/auth-service`
- Debug pod: `kubectl exec -it -n hrms <pod> -- /bin/bash`
- View Terraform state: `terraform state show`
- Helm debugging: `helm template <release> <chart> | kubectl apply -f - --dry-run=client`

---

**You're all set! Your infrastructure is now properly organized and ready for development, staging, and production deployments. 🚀**
