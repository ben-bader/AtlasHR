# HRMS Infrastructure Setup Guide

Quick reference for deploying HRMS using Docker, Kubernetes, Helm, and Terraform.

## Prerequisites

```bash
# Install Docker & Docker Compose
https://docs.docker.com/get-docker/

# Install Kubernetes tools
brew install kubectl helm terraform  # macOS
# OR
choco install kubernetes-cli helm terraform  # Windows

# For local K8s (Minikube)
brew install minikube
minikube start
```

---

## 🐳 Option 1: Local Development (Docker Compose)

### Quick Start
```bash
cd d:/Hrms-SFE/AtlasHR

# Start all services
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# View logs
docker-compose -f infrastructure/docker/docker-compose.yml logs -f

# Stop everything
docker-compose -f infrastructure/docker/docker-compose.yml down
```

### Access Points
- **Frontend**: http://localhost:3000
- **Gateway API**: http://localhost:8084
- **Auth Service**: http://localhost:8081 (internal)
- **Employee Service**: http://localhost:8083 (internal)
- **RabbitMQ Management**: http://localhost:15672 (user: hrms / pass: hrms_pass)

### Benefits
✅ One command to start everything  
✅ No K8s knowledge needed  
✅ Perfect for daily development  

---

## ☸️ Option 2: Local Kubernetes (Minikube)

### Prerequisites
```bash
# Start Minikube
minikube start --cpus=4 --memory=8192

# Enable ingress addon (optional)
minikube addons enable ingress
```

### Deploy Using Raw Manifests
```bash
# Create namespace
kubectl apply -f infrastructure/kubernetes/auth-service/namespace.yaml

# Deploy auth service
kubectl apply -f infrastructure/kubernetes/auth-service/

# Deploy employee service
kubectl apply -f infrastructure/kubernetes/employee-service/

# Deploy gateway service
kubectl apply -f infrastructure/kubernetes/gateway-service/

# Verify deployments
kubectl get pods -n hrms
kubectl get services -n hrms

# View logs
kubectl logs -n hrms deployment/gateway-service
```

### Deploy Using Helm
```bash
# Install releases
helm install auth-service ./infrastructure/helm/auth-service \
  -n hrms --create-namespace

helm install employee-service ./infrastructure/helm/employee-service \
  -n hrms

helm install gateway-service ./infrastructure/helm/gateway-service \
  -n hrms

# Verify
helm list -n hrms

# View rendered manifests (before applying)
helm template auth-service ./infrastructure/helm/auth-service -n hrms
```

### Access Services (Port Forwarding)
```bash
# Forward ports in separate terminals
kubectl port-forward -n hrms svc/gateway-service 8084:8084

# Now access at http://localhost:8084
```

---

## 🔨 Option 3: Kubernetes + Terraform (Recommended for Teams)

### Setup

1. **Prepare Secrets**
   ```bash
   cd infrastructure/terraform
   
   # Create terraform.tfvars with sensitive values
   cat > terraform.auto.tfvars << EOF
   jwt_secret             = "your_jwt_secret_min_32_chars_here"
   postgres_password      = "your_postgres_password"
   rabbitmq_password      = "your_rabbitmq_password"
   EOF
   
   # IMPORTANT: Add to .gitignore to avoid committing secrets
   echo "terraform.auto.tfvars" >> .gitignore
   ```

2. **Initialize Terraform**
   ```bash
   terraform init
   
   # Verify syntax
   terraform validate
   ```

3. **Plan Deployment (DEV)**
   ```bash
   # See what will be created
   terraform plan -var-file=dev.tfvars
   ```

4. **Apply Deployment (DEV)**
   ```bash
   terraform apply -var-file=dev.tfvars
   
   # Review and confirm with 'yes'
   ```

5. **Verify Deployment**
   ```bash
   # Get deployment info
   terraform output
   
   # Check pods
   kubectl get pods -n hrms
   
   # View service IPs
   kubectl get services -n hrms
   ```

### Common Terraform Commands

```bash
# Check current state
terraform show

# See what changed (drift detection)
terraform refresh
terraform plan

# Destroy everything (CAREFUL!)
terraform destroy -var-file=dev.tfvars

# List Helm releases
helm list -n hrms

# Upgrade an Helm release
helm upgrade auth-service ./infrastructure/helm/auth-service -n hrms

# Rollback Helm release
helm rollback auth-service -n hrms
```

---

## 🌍 Environments

### Development
```bash
terraform apply -var-file=dev.tfvars \
  -var jwt_secret="dev_secret_min_32_chars" \
  -var postgres_password="dev_pass" \
  -var rabbitmq_password="dev_pass"
```

### Staging
```bash
# First, create kubeconfig context for staging cluster
kubectl config set-context my-staging-cluster --cluster=staging-k8s --user=staging-user

# Then deploy
terraform apply -var-file=staging.tfvars \
  -var kubeconfig_context="my-staging-cluster" \
  -var jwt_secret="staging_secret" \
  -var postgres_password="staging_pass" \
  -var rabbitmq_password="staging_pass"
```

### Production
```bash
# Use a remote backend (S3, Azure, Terraform Cloud)
# See infrastructure/terraform/backend.tf for examples

terraform apply -var-file=prod.tfvars \
  -var jwt_secret="prod_secret" \
  -var postgres_password="prod_pass" \
  -var rabbitmq_password="prod_pass"
```

---

## 🔍 Troubleshooting

### Pods not starting
```bash
# Check pod status and events
kubectl describe pod <pod-name> -n hrms

# View logs
kubectl logs -n hrms deployment/auth-service --tail=100

# Get events
kubectl get events -n hrms --sort-by='.lastTimestamp'
```

### Services not communicating
```bash
# Test DNS resolution
kubectl exec -it -n hrms <pod-name> -- nslookup auth-service

# Test connectivity
kubectl exec -it -n hrms <pod-name> -- curl http://auth-service:8081/actuator/health
```

### Helm issues
```bash
# Debug Helm template rendering
helm template auth-service ./infrastructure/helm/auth-service -n hrms

# Check values
helm get values auth-service -n hrms

# Get release history
helm history auth-service -n hrms

# Rollback to previous release
helm rollback auth-service 1 -n hrms
```

### Terraform issues
```bash
# Check what Terraform will do (dry run)
terraform plan -var-file=dev.tfvars

# Enable debug logging
TF_LOG=DEBUG terraform apply -var-file=dev.tfvars

# Import existing K8s resources into Terraform state
terraform import kubernetes_namespace.hrms hrms

# Refresh state without applying changes
terraform refresh
```

---

## 📊 Monitoring & Logs

### View Logs
```bash
# Real-time logs from all services
kubectl logs -n hrms -f -l app=auth-service

# View previous pod logs (if crashed)
kubectl logs -n hrms <pod-name> --previous

# Stream logs from multiple pods
kubectl logs -n hrms -f deployment/auth-service deployment/employee-service
```

### Check Resource Usage
```bash
kubectl top pods -n hrms
kubectl top nodes
```

### Shell into Pod
```bash
kubectl exec -it -n hrms <pod-name> -- /bin/bash
```

---

## 🔐 Secrets Management

### Best Practices

**Development**: Store secrets in `.env` files (git ignored)
```bash
# .env (git ignored)
JWT_SECRET=dev_secret_here
POSTGRES_PASSWORD=dev_pass_here
```

**Staging/Production**: Use external secrets manager
```bash
# AWS Secrets Manager
aws secretsmanager get-secret-value --secret-id hrms/jwt-secret

# Or Azure Key Vault
az keyvault secret show --vault-name my-vault --name jwt-secret
```

### Update Secrets in Kubernetes
```bash
# Update secret
kubectl create secret generic jwt-secret \
  --from-literal=jwt-secret="new_secret" \
  -n hrms --dry-run=client -o yaml | kubectl apply -f -

# Restart pods to pick up new secret
kubectl rollout restart deployment/auth-service -n hrms
```

---

## 🚀 CI/CD Integration

### GitHub Actions Example
```yaml
name: Deploy to Kubernetes

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Terraform
        uses: hashicorp/setup-terraform@v1
      
      - name: Terraform Init
        run: cd infrastructure/terraform && terraform init
      
      - name: Terraform Plan
        run: |
          cd infrastructure/terraform
          terraform plan \
            -var-file=prod.tfvars \
            -var jwt_secret="${{ secrets.JWT_SECRET }}" \
            -var postgres_password="${{ secrets.POSTGRES_PASSWORD }}" \
            -var rabbitmq_password="${{ secrets.RABBITMQ_PASSWORD }}"
      
      - name: Terraform Apply
        if: github.ref == 'refs/heads/main'
        run: cd infrastructure/terraform && terraform apply -auto-approve
```

---

## 📚 Additional Resources

- [Kubernetes Docs](https://kubernetes.io/docs/)
- [Helm Charts](https://helm.sh/docs/)
- [Terraform Kubernetes Provider](https://registry.terraform.io/providers/hashicorp/kubernetes/latest)
- [Spring Boot on Kubernetes](https://spring.io/guides/kubernetes/spring-boot-kubernetes/)

---

## ✅ Deployment Checklist

### Before Deploying
- [ ] All services compiled and tested locally
- [ ] Docker images built and tagged
- [ ] Kubernetes cluster available and configured
- [ ] kubeconfig properly set up
- [ ] Helm installed (version 3+)
- [ ] Terraform installed (version 1.0+)
- [ ] Secrets prepared (JWT, DB passwords, etc.)

### Development
- [ ] `docker-compose up` works
- [ ] Services communicate via network
- [ ] Frontend accesses API Gateway

### Staging
- [ ] Terraform plan shows expected resources
- [ ] All pods reach "Running" state
- [ ] Services pass readiness checks
- [ ] Frontend can reach staging API

### Production
- [ ] Use remote Terraform backend
- [ ] All secrets in external vault
- [ ] Proper TLS certificates configured
- [ ] Database backups enabled
- [ ] Monitoring and alerts set up
- [ ] Disaster recovery plan ready

---

## Quick Commands Reference

```bash
# Docker Compose
docker-compose -f infrastructure/docker/docker-compose.yml up -d

# Kubectl
kubectl apply -f infrastructure/kubernetes/
kubectl get pods -n hrms
kubectl logs -n hrms deployment/auth-service

# Helm
helm install auth ./infrastructure/helm/auth-service -n hrms --create-namespace
helm upgrade auth ./infrastructure/helm/auth-service -n hrms
helm uninstall auth -n hrms

# Terraform
terraform init
terraform plan -var-file=dev.tfvars
terraform apply -var-file=dev.tfvars
terraform destroy -var-file=dev.tfvars
```

