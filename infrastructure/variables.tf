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
  description = "Enable automatic daily shutdown of ECS services"
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