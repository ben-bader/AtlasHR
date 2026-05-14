terraform {
  required_version = ">= 1.0"
  
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
  }

  # IMPORTANT: Configure this for your environment
  # For local development: Use local backend (default)
  # For team/cloud: Use S3, Azure Storage, Terraform Cloud, etc.
  # backend "local" {
  #   path = "terraform.tfstate"
  # }
}

provider "kubernetes" {
  config_path    = var.kubeconfig_path
  config_context = var.kubeconfig_context
}

provider "helm" {
  kubernetes {
    config_path    = var.kubeconfig_path
    config_context = var.kubeconfig_context
  }
}

# ══════════════════════════════════════════════════════════════════════════════
# NAMESPACE
# ══════════════════════════════════════════════════════════════════════════════
resource "kubernetes_namespace" "hrms" {
  metadata {
    name = var.namespace
    labels = {
      "name"                           = var.namespace
      "pod-security.kubernetes.io/enforce" = "baseline"
    }
  }
}

# ══════════════════════════════════════════════════════════════════════════════
# SECRETS
# ══════════════════════════════════════════════════════════════════════════════

# JWT Secret (used by auth-service to sign tokens)
resource "kubernetes_secret" "jwt_secret" {
  metadata {
    name      = "jwt-secret"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    jwt-secret = var.jwt_secret
  }

  depends_on = [kubernetes_namespace.hrms]
}

# PostgreSQL credentials
resource "kubernetes_secret" "postgres_credentials" {
  metadata {
    name      = "postgres-credentials"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    username = var.postgres_username
    password = var.postgres_password
  }

  depends_on = [kubernetes_namespace.hrms]
}

# RabbitMQ credentials
resource "kubernetes_secret" "rabbitmq_credentials" {
  metadata {
    name      = "rabbitmq-credentials"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    username = var.rabbitmq_username
    password = var.rabbitmq_password
  }

  depends_on = [kubernetes_namespace.hrms]
}

# Redis credentials (optional, if using authentication)
resource "kubernetes_secret" "redis_credentials" {
  metadata {
    name      = "redis-credentials"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    password = var.redis_password != "" ? var.redis_password : "nopass"
  }

  depends_on = [kubernetes_namespace.hrms]
}

# ══════════════════════════════════════════════════════════════════════════════
# CONFIGMAPS
# ══════════════════════════════════════════════════════════════════════════════

resource "kubernetes_config_map" "hrms_config" {
  metadata {
    name      = "hrms-config"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    # Database
    SPRING_DATASOURCE_URL      = "jdbc:postgresql://${var.postgres_host}:${var.postgres_port}/hrms_auth"
    SPRING_JPA_DATABASE_PLATFORM = "org.hibernate.dialect.PostgreSQLDialect"
    SPRING_JPA_HIBERNATE_DDL_AUTO = var.hibernate_ddl_auto
    
    # RabbitMQ
    SPRING_RABBITMQ_HOST       = var.rabbitmq_host
    SPRING_RABBITMQ_PORT       = tostring(var.rabbitmq_port)
    
    # Redis
    SPRING_DATA_REDIS_HOST     = var.redis_host
    SPRING_DATA_REDIS_PORT     = tostring(var.redis_port)
    
    # JWT
    JWT_EXPIRATION             = tostring(var.jwt_expiration_ms)
    JWT_REFRESH_EXPIRATION     = tostring(var.jwt_refresh_expiration_ms)
    
    # Application
    LOG_LEVEL                  = var.log_level
    FRONTEND_URL               = var.frontend_url
  }

  depends_on = [kubernetes_namespace.hrms]
}

# ══════════════════════════════════════════════════════════════════════════════
# AUTH SERVICE HELM DEPLOYMENT
# ══════════════════════════════════════════════════════════════════════════════

resource "helm_release" "auth_service" {
  name             = "auth-service"
  chart            = "${path.module}/../helm/auth-service"
  namespace        = kubernetes_namespace.hrms.metadata[0].name
  create_namespace = false

  # Base values from chart
  values = [
    file("${path.module}/../helm/auth-service/values.yaml")
  ]

  # Override image
  set {
    name  = "image.repository"
    value = "${var.image_registry}/${var.auth_service_image}"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }

  # Override replicas based on environment
  set {
    name  = "replicaCount"
    value = var.auth_service_replicas
  }

  # Resource limits
  set {
    name  = "resources.requests.cpu"
    value = var.auth_service_cpu_request
  }
  set {
    name  = "resources.requests.memory"
    value = var.auth_service_memory_request
  }

  depends_on = [
    kubernetes_namespace.hrms,
    kubernetes_secret.jwt_secret,
    kubernetes_secret.postgres_credentials,
    kubernetes_secret.rabbitmq_credentials,
    kubernetes_config_map.hrms_config
  ]
}

# ══════════════════════════════════════════════════════════════════════════════
# EMPLOYEE SERVICE HELM DEPLOYMENT
# ══════════════════════════════════════════════════════════════════════════════

resource "helm_release" "employee_service" {
  name             = "employee-service"
  chart            = "${path.module}/../helm/employee-service"
  namespace        = kubernetes_namespace.hrms.metadata[0].name
  create_namespace = false

  values = [
    file("${path.module}/../helm/employee-service/values.yaml")
  ]

  # Select environment-specific values
  values = var.environment != "prod" ? [
    file("${path.module}/../helm/employee-service/values.yaml"),
    file("${path.module}/../helm/employee-service/values-${var.environment}.yaml")
  ] : [
    file("${path.module}/../helm/employee-service/values.yaml"),
    file("${path.module}/../helm/employee-service/values-prod.yaml")
  ]

  set {
    name  = "image.repository"
    value = "${var.image_registry}/${var.employee_service_image}"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }

  set {
    name  = "replicaCount"
    value = var.employee_service_replicas
  }

  set {
    name  = "resources.requests.cpu"
    value = var.employee_service_cpu_request
  }
  set {
    name  = "resources.requests.memory"
    value = var.employee_service_memory_request
  }

  depends_on = [
    kubernetes_namespace.hrms,
    kubernetes_secret.jwt_secret,
    kubernetes_secret.postgres_credentials,
    kubernetes_secret.rabbitmq_credentials,
    kubernetes_config_map.hrms_config
  ]
}

# ══════════════════════════════════════════════════════════════════════════════
# GATEWAY SERVICE HELM DEPLOYMENT
# ══════════════════════════════════════════════════════════════════════════════

resource "helm_release" "gateway_service" {
  name             = "gateway-service"
  chart            = "${path.module}/../helm/gateway-service"
  namespace        = kubernetes_namespace.hrms.metadata[0].name
  create_namespace = false

  values = [
    file("${path.module}/../helm/gateway-service/values.yaml")
  ]

  set {
    name  = "image.repository"
    value = "${var.image_registry}/${var.gateway_service_image}"
  }
  set {
    name  = "image.tag"
    value = var.image_tag
  }

  set {
    name  = "replicaCount"
    value = var.gateway_service_replicas
  }

  set {
    name  = "service.type"
    value = var.gateway_service_type
  }

  set {
    name  = "resources.requests.cpu"
    value = var.gateway_service_cpu_request
  }
  set {
    name  = "resources.requests.memory"
    value = var.gateway_service_memory_request
  }

  depends_on = [
    kubernetes_namespace.hrms,
    kubernetes_secret.jwt_secret,
    kubernetes_secret.redis_credentials,
    kubernetes_config_map.hrms_config
  ]
}

# ══════════════════════════════════════════════════════════════════════════════
# NETWORK POLICIES
# ══════════════════════════════════════════════════════════════════════════════

resource "kubernetes_network_policy" "gateway_to_services" {
  metadata {
    name      = "gateway-to-services"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  spec {
    pod_selector {
      match_labels = {
        app = "gateway-service"
      }
    }

    policy_types = ["Egress"]

    egress {
      # Allow to auth-service
      to {
        pod_selector {
          match_labels = {
            app = "auth-service"
          }
        }
      }
      ports {
        protocol = "TCP"
        port     = "8081"
      }
    }

    egress {
      # Allow to employee-service
      to {
        pod_selector {
          match_labels = {
            app = "employee-service"
          }
        }
      }
      ports {
        protocol = "TCP"
        port     = "8083"
      }
    }

    egress {
      # Allow DNS
      to {
        namespace_selector {
          match_labels = {
            name = "kube-system"
          }
        }
      }
      ports {
        protocol = "UDP"
        port     = "53"
      }
    }
  }

  depends_on = [kubernetes_namespace.hrms]
}
