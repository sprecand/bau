variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "bau"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-central-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "main"
}

# Cost Control Variables
variable "monthly_budget_limit" {
  description = "Monthly budget limit in USD"
  type        = number
  default     = 10
}

variable "alert_email" {
  description = "Email address for billing alerts (leave empty to disable)"
  type        = string
  default     = ""
}

variable "enable_auto_shutdown" {
  description = "Enable automatic daily schedule: startup at 9 AM CET/CEST, shutdown at 9 PM CET/CEST"
  type        = bool
  default     = true
}

# Resource Sizing (optimized for cost)
variable "backend_cpu" {
  description = "CPU units for backend (256 = 0.25 vCPU)"
  type        = number
  default     = 256
}

variable "backend_memory" {
  description = "Memory for backend in MB"
  type        = number
  default     = 512
}

variable "frontend_cpu" {
  description = "CPU units for frontend (256 = 0.25 vCPU)"
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Memory for frontend in MB"
  type        = number
  default     = 512
}

# Database Variables
variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Allocated storage for RDS instance in GB"
  type        = number
  default     = 20
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "bau_platform"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "bau_user"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

# Domain Variables
variable "domain_name" {
  description = "Custom domain name for the application (e.g., example.com). Leave empty to use ALB DNS name"
  type        = string
  default     = ""
}

variable "create_api_subdomain" {
  description = "Create api.domain.com subdomain for API endpoints"
  type        = bool
  default     = false
} 