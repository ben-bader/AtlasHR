# Employee Service - CI/CD & Deployment Guide

## Overview

The Employee Service is containerized and deployable to Kubernetes with complete CI/CD pipeline via GitHub Actions.

## CI/CD Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   GitHub Actions Workflow                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Test       │  │   Build      │  │   Security   │      │
│  │   (Maven)    │  │   (Docker)   │  │   Scan       │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
│         └──────────────────┼──────────────────┘              │
│                            │                                 │
│                     ┌──────▼───────┐                         │
│                     │ Push to       │                        │
│                     │ Docker Hub    │                        │
│                     └──────┬───────┘                         │
│                            │                                 │
│      ┌─────────────────────┼─────────────────────┐           │
│      │                     │                     │           │
│  ┌───▼───┐  ┌──────▼────┐  ┌──────▼────┐  ┌────▼──┐        │
│  │ Dev   │  │ Staging  │  │ Production│  │ Tag   │        │
│  │Deploy │  │ Deploy   │  │ Deploy    │  │Release│        │
│  └───────┘  └──────────┘  └───────────┘  └───────┘        │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## GitHub Secrets Required

Set these secrets in your GitHub repository settings:

```
DOCKER_USERNAME       - Docker Hub username
DOCKER_PASSWORD       - Docker Hub access token
KUBE_CONFIG_DEV       - Base64 encoded kubeconfig for dev cluster
KUBE_CONFIG_STAGING   - Base64 encoded kubeconfig for staging cluster
KUBE_CONFIG_PROD      - Base64 encoded kubeconfig for prod cluster
```

### Encoding kubeconfig:

```bash
cat ~/.kube/config | base64 -w 0
```

## Trigger Pipeline

The CI/CD pipeline triggers automatically on:

- **Push to `develop`**: Deploys to development cluster
- **Push to `main`**: Deploys to staging cluster
- **Tag with `v*`**: Deploys to production cluster

Manual triggers:

```bash
# Create a new tag to trigger production deployment
git tag v1.0.0
git push origin v1.0.0
```

## Deployment Steps

### 1. Build & Test (Automatic)

```bash
# Maven test
mvn clean test

# Maven build
mvn clean package -DskipTests

# Docker build
docker build -t hrms/employee-service:latest .

# Push to registry
docker push hrms/employee-service:latest
```

### 2. Security Scanning

Trivy scans the Docker image for vulnerabilities:

```bash
trivy image hrms/employee-service:latest
```

### 3. Kubernetes Deployment

#### Using kubectl directly:

```bash
# Create namespace
kubectl create namespace hrms

# Apply Kubernetes manifests
kubectl apply -f infrastructure/kubernetes/employee-service/

# Verify deployment
kubectl get deployments -n hrms
kubectl get services -n hrms
kubectl logs -f deployment/employee-service -n hrms
```

#### Using Helm (Recommended):

```bash
# Add repository
helm repo add myrepo https://charts.example.com
helm repo update

# Install/Update release
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values.yaml

# Check status
helm status employee-service -n hrms
helm get values employee-service -n hrms

# Rollback if needed
helm rollback employee-service -n hrms
```

#### Using Terraform:

```bash
cd infrastructure/terraform/employee-service

# Initialize
terraform init

# Plan
terraform plan -var-file=dev.tfvars

# Apply
terraform apply -var-file=dev.tfvars
```

## Environment-Specific Values

### Development

```bash
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values-dev.yaml
```

**Features:**
- 1 replica
- Low resource limits
- No autoscaling
- Always pull latest image

### Staging

```bash
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values-staging.yaml
```

**Features:**
- 2 replicas
- Medium resource limits
- Autoscaling (2-3 replicas)
- Specific image tags

### Production

```bash
helm upgrade --install employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --values ./infrastructure/helm/employee-service/values-prod.yaml
```

**Features:**
- 3 replicas minimum
- High resource limits
- Aggressive autoscaling (3-10 replicas)
- Pod disruption budgets
- Ingress with TLS
- Specific versioned image tags

## Health Checks

The service exposes health endpoints for Kubernetes probes:

```bash
# Liveness probe (is the service alive?)
curl http://localhost:8082/actuator/health/liveness

# Readiness probe (is the service ready to accept traffic?)
curl http://localhost:8082/actuator/health/readiness

# Full health (includes database, messaging, etc.)
curl http://localhost:8082/actuator/health
```

## Monitoring & Logging

### Kubernetes monitoring:

```bash
# Watch deployment
kubectl get deployment employee-service -n hrms -w

# Stream logs
kubectl logs -f deployment/employee-service -n hrms --tail=100

# Describe pod for events
kubectl describe pod <pod-name> -n hrms

# CPU/Memory usage
kubectl top pod -n hrms
```

### Prometheus metrics:

Available at `/actuator/prometheus` on port 8082

### Helm values for monitoring:

```yaml
podAnnotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "8082"
  prometheus.io/path: "/actuator/prometheus"
```

## Scaling

### Manual scaling:

```bash
kubectl scale deployment employee-service --replicas=5 -n hrms
```

### Automatic scaling (configured in Helm):

```bash
# View HPA status
kubectl get hpa -n hrms

# Watch scaling
kubectl get hpa employee-service -n hrms -w
```

## Updates & Rollouts

### Rolling update:

```bash
# Automatic with Helm
helm upgrade employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --set image.tag=v1.0.1

# Watch rollout
kubectl rollout status deployment/employee-service -n hrms
```

### Blue-Green deployment:

```bash
# Deploy new version as blue
helm install employee-service-blue ./infrastructure/helm/employee-service \
  --namespace hrms \
  --set image.tag=v1.0.1

# Switch traffic (update service selector)
kubectl patch service employee-service -n hrms \
  -p '{"spec":{"selector":{"app":"employee-service-blue"}}}'

# Verify, then remove old version
helm uninstall employee-service -n hrms
```

## Troubleshooting

### Pod not starting:

```bash
# Check pod status
kubectl get pods -n hrms -o wide

# View logs
kubectl logs <pod-name> -n hrms --previous

# Describe pod
kubectl describe pod <pod-name> -n hrms
```

### Database connection issues:

```bash
# Test database connectivity from pod
kubectl exec -it <pod-name> -n hrms -- \
  curl postgresql://postgres:5432

# Check configmap
kubectl get configmap employee-service-config -n hrms -o yaml

# Check secret
kubectl get secret employee-service-secrets -n hrms -o yaml
```

### High CPU/Memory:

```bash
# Check resource requests/limits
kubectl describe deployment employee-service -n hrms

# Adjust in values.yaml
helm upgrade employee-service ./infrastructure/helm/employee-service \
  --namespace hrms \
  --set resources.requests.memory=512Mi \
  --set resources.limits.memory=1Gi
```

## Docker Images

### Build locally:

```bash
cd apps/employee-service
docker build -t hrms/employee-service:latest .
docker build -t hrms/employee-service:$(git rev-parse --short HEAD) .
```

### Run Docker container:

```bash
docker run -d \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hrms_employee_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_RABBITMQ_HOST=localhost \
  --name employee-service \
  hrms/employee-service:latest
```

### Using Docker Compose:

```bash
cd apps/employee-service
docker-compose up -d
docker-compose logs -f
docker-compose down
```

## Network Policies

Network policies restrict traffic to/from the Employee Service:

```yaml
# Ingress: Allow from API Gateway and services in hrms namespace
# Egress: Allow to PostgreSQL (5432), RabbitMQ (5672), DNS (53)
```

Configure in `infrastructure/kubernetes/employee-service/employee-netpol.yaml`

## Backup & Recovery

### Database backup:

```bash
kubectl exec -it <postgres-pod> -n hrms -- \
  pg_dump -U hrms hrms_employee_db > backup.sql
```

### Restore:

```bash
kubectl exec -it <postgres-pod> -n hrms -- \
  psql -U hrms hrms_employee_db < backup.sql
```

## Security Considerations

✅ **Implemented:**
- Non-root container user
- Read-only root filesystem
- Resource limits
- Network policies
- Security context
- Secret management
- Image scanning

⚠️ **Recommended:**
- Enable Pod Security Policy
- Use private Docker registry
- Encrypt secrets at rest
- Enable RBAC
- Implement service mesh (Istio)

## CI/CD Workflow Files

- `.github/workflows/employee-service.yaml` - Main workflow
- `infrastructure/kubernetes/` - K8s manifests
- `infrastructure/helm/` - Helm charts
- `infrastructure/terraform/` - Terraform IaC
- `apps/employee-service/Dockerfile` - Container image

## References

- [Kubernetes Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)
- [Helm Documentation](https://helm.sh/docs/)
- [Terraform Kubernetes Provider](https://registry.terraform.io/providers/hashicorp/kubernetes/latest/docs)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
