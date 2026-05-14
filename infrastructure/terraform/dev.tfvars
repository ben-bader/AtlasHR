# ══════════════════════════════════════════════════════════════════════════════
# TERRAFORM VARIABLES - DEVELOPMENT ENVIRONMENT
# ══════════════════════════════════════════════════════════════════════════════
# Usage: terraform apply -var-file=dev.tfvars

environment = "dev"

kubeconfig_context = "minikube"
namespace          = "hrms"

# Images
image_registry  = "hrms"
image_tag       = "latest"

# Replicas (dev: minimal)
auth_service_replicas     = 1
employee_service_replicas = 1
gateway_service_replicas  = 1

# Resources (dev: minimal)
auth_service_cpu_request      = "100m"
auth_service_memory_request   = "128Mi"
employee_service_cpu_request  = "100m"
employee_service_memory_request = "128Mi"
gateway_service_cpu_request   = "100m"
gateway_service_memory_request = "128Mi"

gateway_service_type = "LoadBalancer"

# Logging
log_level = "DEBUG"
hibernate_ddl_auto = "create-drop"

# Frontend
frontend_url = "http://localhost:3000"

# Note: Secrets must be provided via environment variables or -var flags:
# terraform apply -var-file=dev.tfvars \
#   -var jwt_secret="your_dev_secret_min_32_chars" \
#   -var postgres_password="dev_postgres_pass" \
#   -var rabbitmq_password="dev_rabbitmq_pass"
