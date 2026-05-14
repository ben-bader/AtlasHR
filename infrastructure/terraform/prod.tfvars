# ══════════════════════════════════════════════════════════════════════════════
# TERRAFORM VARIABLES - PRODUCTION ENVIRONMENT
# ══════════════════════════════════════════════════════════════════════════════
# Usage: terraform apply -var-file=prod.tfvars

environment = "prod"

kubeconfig_context = "my-prod-cluster"
namespace          = "hrms-prod"

# Images (use specific versions, never 'latest')
image_registry  = "myrepo.azurecr.io/hrms"
image_tag       = "v1.0.0"

# Replicas (prod: high availability)
auth_service_replicas     = 3
employee_service_replicas = 3
gateway_service_replicas  = 3

# Resources (prod: ample)
auth_service_cpu_request      = "500m"
auth_service_memory_request   = "512Mi"
employee_service_cpu_request  = "500m"
employee_service_memory_request = "512Mi"
gateway_service_cpu_request   = "500m"
gateway_service_memory_request = "512Mi"

gateway_service_type = "LoadBalancer"

# Logging
log_level = "WARN"
hibernate_ddl_auto = "validate"

# Frontend
frontend_url = "https://hrms.example.com"

# Kubernetes context for prod cluster
# Production cluster should have:
# - Proper TLS certificates
# - Node auto-scaling
# - Multi-zone deployment
# - Backup and recovery procedures

# IMPORTANT: Use a remote backend for production state!
# See backend.tf for S3/Azure/Terraform Cloud examples

# Secrets MUST be provided securely:
# Option 1: Use Terraform Cloud/Enterprise with sensitive variables
# Option 2: Use AWS Secrets Manager / Azure Key Vault
# Option 3: Pass via environment variables (CI/CD)
# DO NOT commit secrets to git!
