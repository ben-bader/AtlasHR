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

# ==========================
# Namespace
# ==========================
resource "kubernetes_namespace" "hrms" {
  metadata {
    name = "hrms"
  }
}

# ==========================
# Attendance Service (Helm)
# ==========================
resource "helm_release" "attendance_service" {
  name             = "attendance-service"
  repository       = "file://${path.module}/../helm"
  chart            = "attendance-service"
  namespace        = kubernetes_namespace.hrms.metadata[0].name
  create_namespace = false

  values = [
    file("${path.module}/../helm/attendance-service/values.yaml")
  ]

  set {
    name  = "image.repository"
    value = var.image_repository
  }

  set {
    name  = "image.tag"
    value = var.image_tag
  }

  depends_on = [kubernetes_namespace.hrms]
}

# ==========================
# PostgreSQL Secret
# ==========================
resource "kubernetes_secret" "postgres_credentials" {
  metadata {
    name      = "postgres-credentials"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    username = base64encode(var.postgres_username)
    password = base64encode(var.postgres_password)
  }

  depends_on = [kubernetes_namespace.hrms]
}

# ==========================
# RabbitMQ Secret
# ==========================
resource "kubernetes_secret" "rabbitmq_credentials" {
  metadata {
    name      = "rabbitmq-credentials"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  data = {
    username = base64encode(var.rabbitmq_username)
    password = base64encode(var.rabbitmq_password)
  }

  depends_on = [kubernetes_namespace.hrms]
}

# ==========================
# Network Policy
# ==========================
resource "kubernetes_network_policy" "attendance_service_netpol" {
  metadata {
    name      = "attendance-service-netpol"
    namespace = kubernetes_namespace.hrms.metadata[0].name
  }

  spec {
    pod_selector {
      match_labels = {
        app = "attendance-service"
      }
    }

    policy_types = ["Ingress", "Egress"]

    ingress {
      from {
        namespace_selector {
          match_labels = {
            name = "hrms"
          }
        }
      }

      from {
        pod_selector {
          match_labels = {
            app = "api-gateway"
          }
        }
      }

      ports {
        protocol = "TCP"
        port     = "8082"
      }
    }

    egress {
      to {
        namespace_selector {
          match_labels = {
            name = "hrms"
          }
        }
      }

      ports {
        protocol = "TCP"
        port     = "5432"
      }

      ports {
        protocol = "TCP"
        port     = "5672"
      }
    }

    egress {
      to {
        pod_selector {}
      }

      ports {
        protocol = "TCP"
        port     = "53"
      }

      ports {
        protocol = "UDP"
        port     = "53"
      }
    }
  }

  depends_on = [kubernetes_namespace.hrms]
}
