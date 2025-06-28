output "account_id" {
  description = "AWS Account ID"
  value       = data.aws_caller_identity.current.account_id
}

output "region" {
  description = "AWS Region"
  value       = data.aws_region.current.name
}

output "ecr_repository_backend_url" {
  description = "Backend ECR Repository URL"
  value       = aws_ecr_repository.backend.repository_url
}

output "ecr_repository_frontend_url" {
  description = "Frontend ECR Repository URL"
  value       = aws_ecr_repository.frontend.repository_url
}

output "ecs_cluster_name" {
  description = "ECS Cluster Name"
  value       = aws_ecs_cluster.main.name
}

output "ecs_cluster_arn" {
  description = "ECS Cluster ARN"
  value       = aws_ecs_cluster.main.arn
}

output "backend_service_name" {
  description = "Backend ECS Service Name"
  value       = aws_ecs_service.backend.name
}

output "frontend_service_name" {
  description = "Frontend ECS Service Name"
  value       = aws_ecs_service.frontend.name
}

output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "Public Subnet IDs"
  value       = aws_subnet.public[*].id
}

output "github_secrets_info" {
  description = "Information for GitHub Secrets setup"
  value = {
    AWS_REGION = var.aws_region
    ECR_BACKEND_URI = aws_ecr_repository.backend.repository_url
    ECR_FRONTEND_URI = aws_ecr_repository.frontend.repository_url
    ECS_CLUSTER = aws_ecs_cluster.main.name
    BACKEND_SERVICE = aws_ecs_service.backend.name
    FRONTEND_SERVICE = aws_ecs_service.frontend.name
  }
} 