# AWS Cognito Configuration

# Cognito User Pool
resource "aws_cognito_user_pool" "main" {
  name = "${var.project_name}-user-pool"

  # Password policy
  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }

  # Username configuration
  username_configuration {
    case_sensitive = false
  }

  # Auto-verified attributes
  auto_verified_attributes = ["email"]

  # Verification message templates
  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Bau Platform - Verify your email"
    email_message        = "Your verification code is {####}. Please enter this code to verify your email address."
  }

  # Account recovery settings
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  # User attributes
  schema {
    name                = "email"
    attribute_data_type = "String"
    mutable             = true
    required            = true
  }

  schema {
    name                = "given_name"
    attribute_data_type = "String"
    mutable             = true
    required            = true
  }

  schema {
    name                = "family_name"
    attribute_data_type = "String"
    mutable             = true
    required            = true
  }

  schema {
    name                = "custom:betrieb_id"
    attribute_data_type = "String"
    mutable             = true
    required            = false
  }

  schema {
    name                = "custom:betrieb_name"
    attribute_data_type = "String"
    mutable             = true
    required            = false
  }

  # Admin create user config
  admin_create_user_config {
    allow_admin_create_user_only = false
  }

  # User pool add-ons
  user_pool_add_ons {
    advanced_security_mode = "ENFORCED"
  }

  tags = {
    Name = "${var.project_name}-user-pool"
  }
}

# Cognito User Pool Client
resource "aws_cognito_user_pool_client" "main" {
  name         = "${var.project_name}-client"
  user_pool_id = aws_cognito_user_pool.main.id

  generate_secret = false

  # OAuth settings
  allowed_oauth_flows = ["code", "implicit"]
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_scopes = ["email", "openid", "profile", "aws.cognito.signin.user.admin"]

  # Callback URLs - use custom domain if available, otherwise ALB DNS
  callback_urls = concat(
    ["http://localhost:4200/auth/callback"],
    var.domain_name != "" ? [
      "https://${var.domain_name}/auth/callback",
      "https://www.${var.domain_name}/auth/callback"
    ] : [
      "http://${aws_lb.main.dns_name}/auth/callback"
    ]
  )

  logout_urls = concat(
    ["http://localhost:4200/logout"],
    var.domain_name != "" ? [
      "https://${var.domain_name}/logout",
      "https://www.${var.domain_name}/logout"
    ] : [
      "http://${aws_lb.main.dns_name}/logout"
    ]
  )

  # Supported identity providers
  supported_identity_providers = ["COGNITO"]

  # Token validity
  access_token_validity  = 1  # 1 hour
  id_token_validity      = 1  # 1 hour
  refresh_token_validity = 24 # 24 hours

  # Read/write attributes
  read_attributes = [
    "email",
    "email_verified",
    "given_name",
    "family_name",
    "custom:betrieb_id",
    "custom:betrieb_name"
  ]

  write_attributes = [
    "email",
    "given_name",
    "family_name",
    "custom:betrieb_id",
    "custom:betrieb_name"
  ]

  # Explicit auth flows
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]

  # Prevent user existence errors
  prevent_user_existence_errors = "ENABLED"

  depends_on = [aws_lb.main]
}

# Cognito User Pool Domain
resource "aws_cognito_user_pool_domain" "main" {
  domain       = "${var.project_name}-auth-${random_string.domain_suffix.result}"
  user_pool_id = aws_cognito_user_pool.main.id
}

# Random string for unique domain
resource "random_string" "domain_suffix" {
  length  = 8
  special = false
  upper   = false
}

# Cognito User Pool Groups
resource "aws_cognito_user_pool_group" "admin" {
  name         = "ADMIN"
  user_pool_id = aws_cognito_user_pool.main.id
  description  = "Admin group for platform administrators"
  precedence   = 1
}

resource "aws_cognito_user_pool_group" "betrieb" {
  name         = "BETRIEB"
  user_pool_id = aws_cognito_user_pool.main.id
  description  = "Betrieb group for company users"
  precedence   = 2
}

# Identity Pool for AWS services access (optional)
resource "aws_cognito_identity_pool" "main" {
  identity_pool_name               = "${var.project_name}-identity-pool"
  allow_unauthenticated_identities = false

  cognito_identity_providers {
    client_id               = aws_cognito_user_pool_client.main.id
    provider_name           = aws_cognito_user_pool.main.endpoint
    server_side_token_check = true
  }

  tags = {
    Name = "${var.project_name}-identity-pool"
  }
} 