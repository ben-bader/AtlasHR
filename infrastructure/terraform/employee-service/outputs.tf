output "employee_service_namespace" {
  description = "Kubernetes namespace for Employee Service"
  value       = kubernetes_namespace.hrms.metadata[0].name
}

output "helm_release_name" {
  description = "Helm release name"
  value       = helm_release.employee_service.name
}

output "helm_release_status" {
  description = "Helm release status"
  value       = helm_release.employee_service.status
}

output "service_endpoint" {
  description = "Employee Service endpoint"
  value       = "http://employee-service.hrms.svc.cluster.local:8082"
}
