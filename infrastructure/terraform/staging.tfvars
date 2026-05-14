# ══════════════════════════════════════════════════════════════════════════════
# TERRAFORM VARIABLES - STAGING ENVIRONMENT
# ══════════════════════════════════════════════════════════════════════════════
# Usage: terraform apply -var-file=staging.tfvars

environment = "staging"

kubeconfig_context = "my-staging-cluster"
namespace          = "hrms-staging"

# Images
image_registry  = "myrepo.azurecr.io/hrms"
image_tag       = "staging-v1.0.0"

# Replicas (staging: medium)
auth_service_replicas     = 2
employee_service_replicas = 2
gateway_service_replicas  = 2

# Resources (staging: medium)
auth_service_cpu_request      = "250m"
auth_service_memory_request   = "256Mi"
employee_service_cpu_request  = "250m"
employee_service_memory_request = "256Mi"
gateway_service_cpu_request   = "250m"
gateway_service_memory_request = "256Mi"

gateway_service_type = "LoadBalancer"

# Logging
log_level = "INFO"
hibernate_ddl_auto = "validate"

# Frontend
frontend_url = "https://staging.hrms.example.com"

# Kubernetes context for staging cluster
# Make sure you have configured this in your kubeconfig:
# kubectl config set-context my-staging-cluster --cluster=my-staging-k8s --user=my-staging-user

# Secrets provided via environment variables or -var flags
