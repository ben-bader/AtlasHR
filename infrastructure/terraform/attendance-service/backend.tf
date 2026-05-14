terraform {
  backend "kubernetes" {
    secret_suffix = "attendance-service"
    config_path   = "~/.kube/config"
    namespace     = "terraform-state"
  }
}
