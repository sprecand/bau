# Custom Domain Infrastructure

# Route 53 Hosted Zone (conditional - only if domain_name is provided)
resource "aws_route53_zone" "main" {
  count = var.domain_name != "" ? 1 : 0
  name  = var.domain_name

  tags = {
    Name = "${var.project_name}-hosted-zone"
  }
}

# ACM Certificate for custom domain
resource "aws_acm_certificate" "main" {
  count           = var.domain_name != "" ? 1 : 0
  domain_name     = var.domain_name
  subject_alternative_names = [
    "*.${var.domain_name}",
    "www.${var.domain_name}"
  ]
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }

  tags = {
    Name = "${var.project_name}-certificate"
  }
}

# Route 53 Certificate Validation Records
resource "aws_route53_record" "cert_validation" {
  for_each = var.domain_name != "" ? {
    for dvo in aws_acm_certificate.main[0].domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  } : {}

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = aws_route53_zone.main[0].zone_id
}

# ACM Certificate Validation
resource "aws_acm_certificate_validation" "main" {
  count           = var.domain_name != "" ? 1 : 0
  certificate_arn = aws_acm_certificate.main[0].arn
  validation_record_fqdns = [for record in aws_route53_record.cert_validation : record.fqdn]

  timeouts {
    create = "5m"
  }
}

# Route 53 A Record for ALB
resource "aws_route53_record" "main" {
  count   = var.domain_name != "" ? 1 : 0
  zone_id = aws_route53_zone.main[0].zone_id
  name    = var.domain_name
  type    = "A"

  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = true
  }
}

# Route 53 A Record for www subdomain
resource "aws_route53_record" "www" {
  count   = var.domain_name != "" ? 1 : 0
  zone_id = aws_route53_zone.main[0].zone_id
  name    = "www.${var.domain_name}"
  type    = "A"

  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = true
  }
}

# Route 53 A Record for API subdomain
resource "aws_route53_record" "api" {
  count   = var.domain_name != "" && var.create_api_subdomain ? 1 : 0
  zone_id = aws_route53_zone.main[0].zone_id
  name    = "api.${var.domain_name}"
  type    = "A"

  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = true
  }
}

# Local values for domain configuration
locals {
  # Use custom domain if provided, otherwise use ALB DNS name
  primary_domain = var.domain_name != "" ? var.domain_name : aws_lb.main.dns_name
  api_domain     = var.domain_name != "" && var.create_api_subdomain ? "api.${var.domain_name}" : local.primary_domain
  
  # Protocol based on whether we have SSL certificate
  protocol = var.domain_name != "" ? "https" : "http"
  
  # Frontend URL
  frontend_url = "${local.protocol}://${local.primary_domain}"
  
  # Backend API URL
  api_url = "${local.protocol}://${local.api_domain}/api/v1"
  
  # Full URLs for various services
  swagger_url = "${local.protocol}://${local.api_domain}/swagger-ui/index.html"
  health_url  = "${local.protocol}://${local.api_domain}/actuator/health"
} 