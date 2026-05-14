# ══════════════════════════════════════════════════════════════════════════════
# KUBERNETES CONFIGURATION
# ══════════════════════════════════════════════════════════════════════════════

variable "kubeconfig_path" {
  description = "Path to kubeconfig file"
  type        = string
  default     = "~/.kube/config"
}

variable "kubeconfig_context" {
  description = "Kubernetes context to use (e.g., 'minikube', 'docker-desktop', 'my-eks-cluster')"
  type        = string
  default     = "minikube"
}

variable "namespace" {
  description = "Kubernetes namespace for HRMS"
  type        = string
  default     = "hrms"
}

variable "environment" {
  description = "Deployment environment (dev, staging, prod)"
  type        = string
  default     = "dev"
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod."
  }
}

# ══════════════════════════════════════════════════════════════════════════════
# DOCKER REGISTRY & IMAGES
# ══════════════════════════════════════════════════════════════════════════════

variable "image_registry" {
  description = "Docker registry for images (e.g., 'hrms', 'myrepo.azurecr.io/hrms')"
  type        = string
  default     = "hrms"
}

variable "image_tag" {
  description = "Docker image tag (e.g., 'latest', 'v1.0.0')"
  type        = string
  default     = "latest"
}

variable "auth_service_image" {
  description = "Auth service image name"
  type        = string
  default     = "auth-service"
}

variable "employee_service_image" {
  description = "Employee service image name"
  type        = string
  default     = "employee-service"
}

variable "gateway_service_image" {
  description = "Gateway service image name"
  type        = string
  default     = "gateway-service"
}

# ══════════════════════════════════════════════════════════════════════════════
# SERVICE REPLICAS & RESOURCES
# ══════════════════════════════════════════════════════════════════════════════

variable "auth_service_replicas" {
  description = "Number of auth-service replicas"
  type        = number
  default     = 2
}

variable "auth_service_cpu_request" {
  description = "CPU request for auth-service"
  type        = string
  default     = "250m"
}

variable "auth_service_memory_request" {
  description = "Memory request for auth-service"
  type        = string
  default     = "256Mi"
}

variable "employee_service_replicas" {
  description = "Number of employee-service replicas"
  type        = number
  default     = 2
}

variable "employee_service_cpu_request" {
  description = "CPU request for employee-service"
  type        = string
  default     = "250m"
}

variable "employee_service_memory_request" {
  description = "Memory request for employee-service"
  type        = string
  default     = "256Mi"
}

variable "gateway_service_replicas" {
  description = "Number of gateway-service replicas"
  type        = number
  default     = 2
}

variable "gateway_service_cpu_request" {
  description = "CPU request for gateway-service"
  type        = string
  default     = "250m"
}

variable "gateway_service_memory_request" {
  description = "Memory request for gateway-service"
  type        = string
  default     = "256Mi"
}

variable "gateway_service_type" {
  description = "Service type for gateway (LoadBalancer, NodePort, ClusterIP)"
  type        = string
  default     = "LoadBalancer"
}

# ══════════════════════════════════════════════════════════════════════════════
# SECURITY & CREDENTIALS
# ══════════════════════════════════════════════════════════════════════════════

variable "jwt_secret" {
  description = "JWT secret key for token signing (min 32 characters)"
  type        = string
  sensitive   = true
  validation {
    condition     = length(var.jwt_secret) >= 32
    error_message = "JWT secret must be at least 32 characters long."
  }
}

variable "postgres_username" {
  description = "PostgreSQL username"
  type        = string
  default     = "hrms"
  sensitive   = true
}

variable "postgres_password" {
  description = "PostgreSQL password"
  type        = string
  sensitive   = true
}

variable "postgres_host" {
  description = "PostgreSQL host"
  type        = string
  default     = "postgres"
}

variable "postgres_port" {
  description = "PostgreSQL port"
  type        = number
  default     = 5432
}

variable "rabbitmq_username" {
  description = "RabbitMQ username"
  type        = string
  default     = "hrms"
  sensitive   = true
}

variable "rabbitmq_password" {
  description = "RabbitMQ password"
  type        = string
  sensitive   = true
}

variable "rabbitmq_host" {
  description = "RabbitMQ host"
  type        = string
  default     = "rabbitmq"
}

variable "rabbitmq_port" {
  description = "RabbitMQ port"
  type        = number
  default     = 5672
}

variable "redis_host" {
  description = "Redis host"
  type        = string
  default     = "redis"
}

variable "redis_port" {
  description = "Redis port"
  type        = number
  default     = 6379
}

variable "redis_password" {
  description = "Redis password (leave empty for no auth)"
  type        = string
  default     = ""
  sensitive   = true
}

# ══════════════════════════════════════════════════════════════════════════════
# APPLICATION CONFIGURATION
# ══════════════════════════════════════════════════════════════════════════════

variable "jwt_expiration_ms" {
  description = "JWT token expiration in milliseconds"
  type        = number
  default     = 86400000  # 24 hours
}

variable "jwt_refresh_expiration_ms" {
  description = "JWT refresh token expiration in milliseconds"
  type        = number
  default     = 604800000  # 7 days
}

variable "hibernate_ddl_auto" {
  description = "Hibernate DDL auto strategy (create, create-drop, update, validate)"
  type        = string
  default     = "update"
}

variable "log_level" {
  description = "Logging level"
  type        = string
  default     = "INFO"
}

variable "frontend_url" {
  description = "Frontend URL for CORS"
  type        = string
  default     = "http://localhost:3000"
}
