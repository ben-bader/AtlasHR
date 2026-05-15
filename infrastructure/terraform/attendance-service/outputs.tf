output "attendance_service_namespace" {
  description = "Kubernetes namespace for Attendance Service"
  value       = kubernetes_namespace.hrms.metadata[0].name
}

output "helm_release_name" {
  description = "Helm release name"
  value       = helm_release.attendance_service.name
}

output "helm_release_status" {
  description = "Helm release status"
  value       = helm_release.attendance_service.status
}

output "service_endpoint" {
  description = "Attendance Service endpoint"
  value       = "http://attendance-service.hrms.svc.cluster.local:8085"
}
