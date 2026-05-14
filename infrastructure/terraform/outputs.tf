output "namespace" {
  description = "Kubernetes namespace where HRMS is deployed"
  value       = kubernetes_namespace.hrms.metadata[0].name
}

output "auth_service_endpoint" {
  description = "Auth Service endpoint"
  value       = "http://auth-service:8081"
}

output "employee_service_endpoint" {
  description = "Employee Service endpoint"
  value       = "http://employee-service:8083"
}

output "gateway_service_endpoint" {
  description = "Gateway Service endpoint (external access)"
  value       = "http://gateway-service:8084"
}

output "helm_releases" {
  description = "Helm releases deployed"
  value = {
    auth_service     = helm_release.auth_service.id
    employee_service = helm_release.employee_service.id
    gateway_service  = helm_release.gateway_service.id
  }
}

output "deployment_commands" {
  description = "Useful kubectl commands"
  value = {
    get_pods        = "kubectl get pods -n ${kubernetes_namespace.hrms.metadata[0].name}"
    get_services    = "kubectl get services -n ${kubernetes_namespace.hrms.metadata[0].name}"
    get_deployments = "kubectl get deployments -n ${kubernetes_namespace.hrms.metadata[0].name}"
    logs_auth       = "kubectl logs -n ${kubernetes_namespace.hrms.metadata[0].name} deployment/auth-service"
    logs_employee   = "kubectl logs -n ${kubernetes_namespace.hrms.metadata[0].name} deployment/employee-service"
    logs_gateway    = "kubectl logs -n ${kubernetes_namespace.hrms.metadata[0].name} deployment/gateway-service"
  }
}
