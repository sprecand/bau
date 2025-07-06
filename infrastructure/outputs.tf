output "account_id" {
  description = "AWS Account ID"
  value       = data.aws_caller_identity.current.account_id
}

output "region" {
  description = "AWS Region"
  value       = data.aws_region.current.id
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

# Database Outputs
output "database_endpoint" {
  description = "Database endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "database_name" {
  description = "Database name"
  value       = aws_db_instance.postgres.db_name
}

# Load Balancer Outputs
output "load_balancer_dns_name" {
  description = "Load balancer DNS name"
  value       = aws_lb.main.dns_name
}

output "load_balancer_zone_id" {
  description = "Load balancer zone ID"
  value       = aws_lb.main.zone_id
}

# Cognito Outputs
output "cognito_user_pool_id" {
  description = "Cognito User Pool ID"
  value       = aws_cognito_user_pool.main.id
}

output "cognito_user_pool_client_id" {
  description = "Cognito User Pool Client ID"
  value       = aws_cognito_user_pool_client.main.id
}

output "cognito_domain" {
  description = "Cognito Domain"
  value       = aws_cognito_user_pool_domain.main.domain
}

output "cognito_issuer_uri" {
  description = "Cognito Issuer URI"
  value       = "https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.main.id}"
}

output "cognito_jwks_uri" {
  description = "Cognito JWKS URI"
  value       = "https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.main.id}/.well-known/jwks.json"
}

# Domain Outputs
output "domain_name" {
  description = "Custom domain name (if configured)"
  value       = var.domain_name != "" ? var.domain_name : null
}

output "route53_zone_id" {
  description = "Route 53 Hosted Zone ID (if custom domain is used)"
  value       = var.domain_name != "" ? aws_route53_zone.main[0].zone_id : null
}

output "route53_name_servers" {
  description = "Route 53 Name Servers (point your domain to these)"
  value       = var.domain_name != "" ? aws_route53_zone.main[0].name_servers : null
}

output "certificate_arn" {
  description = "ACM Certificate ARN (if custom domain is used)"
  value       = var.domain_name != "" ? aws_acm_certificate_validation.main[0].certificate_arn : null
}

# Application URLs (dynamic based on custom domain)
output "application_url" {
  description = "Application URL"
  value       = local.frontend_url
}

output "api_url" {
  description = "API URL"
  value       = local.api_url
}

output "swagger_url" {
  description = "Swagger UI URL"
  value       = local.swagger_url
}

output "health_check_url" {
  description = "Health Check URL"
  value       = local.health_url
} 