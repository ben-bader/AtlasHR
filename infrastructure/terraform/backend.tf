terraform {
  required_version = ">= 1.0"
  
  # IMPORTANT: For production, configure a remote backend
  # Examples:
  
  # AWS S3 Backend:
  # backend "s3" {
  #   bucket         = "my-hrms-terraform-state"
  #   key            = "infrastructure/terraform.tfstate"
  #   region         = "us-east-1"
  #   encrypt        = true
  #   dynamodb_table = "terraform-locks"
  # }
  
  # Azure Storage Backend:
  # backend "azurerm" {
  #   resource_group_name  = "my-rg"
  #   storage_account_name = "myterraformstate"
  #   container_name       = "tfstate"
  #   key                  = "hrms.tfstate"
  # }
  
  # Terraform Cloud Backend:
  # backend "cloud" {
  #   organization = "my-org"
  #   workspaces {
  #     name = "hrms-prod"
  #   }
  # }
  
  # Default: Local backend (development only)
  # backend "local" {
  #   path = "terraform.tfstate"
  # }
}
