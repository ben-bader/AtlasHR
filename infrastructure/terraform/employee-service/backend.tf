terraform {
  backend "kubernetes" {
    secret_suffix    = "employee-service"
    config_path      = "~/.kube/config"
    namespace        = "terraform-state"
  }
}
