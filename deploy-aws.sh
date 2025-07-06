#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_step() {
    echo -e "${BLUE}🚀 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

check_dependencies() {
    print_step "Checking dependencies..."
    
    # Check if OpenTofu is installed
    if ! command -v tofu &> /dev/null; then
        print_error "OpenTofu is not installed!"
        echo "Install it from: https://opentofu.org/docs/intro/install/"
        exit 1
    fi
    
    # Check if AWS CLI is installed
    if ! command -v aws &> /dev/null; then
        print_error "AWS CLI is not installed!"
        echo "Install it from: https://aws.amazon.com/cli/"
        exit 1
    fi
    
    # Check AWS credentials
    if ! aws sts get-caller-identity &> /dev/null; then
        print_error "AWS credentials not configured!"
        echo "Run: aws configure"
        exit 1
    fi
    
    print_success "All dependencies are installed"
}

deploy_infrastructure() {
    print_step "Deploying AWS infrastructure with OpenTofu..."
    
    # Get email for billing alerts
    if [[ -z "$ALERT_EMAIL" ]]; then
        echo
        read -p "Enter your email for billing alerts: " ALERT_EMAIL
        if [[ -z "$ALERT_EMAIL" ]]; then
            print_warning "No email provided - billing alerts will be disabled"
            ALERT_EMAIL=""
        fi
    fi

    # Get database password
    if [[ -z "$DB_PASSWORD" ]]; then
        echo
        echo "💡 Tip: You can set DB_PASSWORD as a GitHub secret to avoid this prompt"
        echo "Requirements: 8+ chars, uppercase, lowercase, numbers, special chars"
        echo "Generate one: openssl rand -base64 12"
        echo
        read -s -p "Enter database password: " DB_PASSWORD
        echo
        if [[ -z "$DB_PASSWORD" ]]; then
            print_error "Database password is required!"
            exit 1
        fi
    fi

    # Get custom domain (optional)
    if [[ -z "$DOMAIN_NAME" ]]; then
        echo
        echo "💡 Tip: You can set DOMAIN_NAME as a GitHub variable to avoid this prompt"
        read -p "Enter your custom domain (e.g., example.com) or press Enter to use ALB DNS: " DOMAIN_NAME
        if [[ -n "$DOMAIN_NAME" ]]; then
            echo
            read -p "Create api.${DOMAIN_NAME} subdomain for API? (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                CREATE_API_SUBDOMAIN="true"
            else
                CREATE_API_SUBDOMAIN="false"
            fi
        else
            CREATE_API_SUBDOMAIN="false"
        fi
    fi
    
    cd infrastructure
    
    # Initialize OpenTofu
    print_step "Initializing OpenTofu..."
    tofu init
    
    # Validate configuration
    print_step "Validating configuration..."
    tofu validate
    
    # Plan deployment
    print_step "Planning deployment..."
    PLAN_ARGS="-var-file=tofu.tfvars -var=alert_email=$ALERT_EMAIL -var=db_password=$DB_PASSWORD"
    
    if [[ -n "$DOMAIN_NAME" ]]; then
        PLAN_ARGS="$PLAN_ARGS -var=domain_name=$DOMAIN_NAME -var=create_api_subdomain=$CREATE_API_SUBDOMAIN"
        print_step "Using custom domain: $DOMAIN_NAME"
        if [[ "$CREATE_API_SUBDOMAIN" == "true" ]]; then
            print_step "API subdomain: api.$DOMAIN_NAME"
        fi
    else
        print_step "Using ALB DNS name (no custom domain)"
    fi
    
    tofu plan $PLAN_ARGS
    
    # Ask for confirmation
    echo
    read -p "Do you want to apply these changes? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_warning "Deployment cancelled"
        exit 0
    fi
    
    # Apply changes
    print_step "Applying changes..."
    tofu apply $PLAN_ARGS -auto-approve
    
    # Show outputs
    print_step "Deployment completed! Here are the outputs:"
    tofu output
    
    cd ..
}

setup_github_secrets() {
    print_step "Setting up GitHub secrets..."
    
    echo "Add these secrets to your GitHub repository:"
    echo "Repository → Settings → Secrets and variables → Actions"
    echo
    echo "Required secrets:"
    echo "- AWS_ACCESS_KEY_ID: $(aws configure get aws_access_key_id)"
    echo "- AWS_SECRET_ACCESS_KEY: $(aws configure get aws_secret_access_key)"
    echo
    print_warning "Don't share these credentials!"
}

push_initial_images() {
    print_step "Pushing initial Docker images to ECR..."
    
    # Get ECR URLs from Terraform output
    cd infrastructure
    BACKEND_ECR=$(tofu output -raw ecr_repository_backend_url)
    FRONTEND_ECR=$(tofu output -raw ecr_repository_frontend_url)
    AWS_REGION=$(tofu output -raw region)
    cd ..
    
    # Login to ECR
    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $BACKEND_ECR
    
    # Build and push backend
    print_step "Building and pushing backend image..."
    docker build -t $BACKEND_ECR:latest ./backend
    docker push $BACKEND_ECR:latest
    
    # Build and push frontend
    print_step "Building and pushing frontend image..."
    docker build -t $FRONTEND_ECR:latest ./frontend
    docker push $FRONTEND_ECR:latest
    
    print_success "Images pushed successfully!"
}

update_ecs_services() {
    print_step "Updating ECS services with new images..."
    
    cd infrastructure
    CLUSTER_NAME=$(tofu output -raw ecs_cluster_name)
    BACKEND_SERVICE=$(tofu output -raw backend_service_name)
    FRONTEND_SERVICE=$(tofu output -raw frontend_service_name)
    AWS_REGION=$(tofu output -raw region)
    cd ..
    
    # Force new deployment
    aws ecs update-service --cluster $CLUSTER_NAME --service $BACKEND_SERVICE --force-new-deployment --region $AWS_REGION
    aws ecs update-service --cluster $CLUSTER_NAME --service $FRONTEND_SERVICE --force-new-deployment --region $AWS_REGION
    
    print_success "ECS services updated!"
}

show_completion_info() {
    print_success "🎉 AWS infrastructure deployment completed!"
    echo
    
    # Show outputs
    print_step "Getting deployment information..."
    tofu output
    
    echo
    echo "📋 Next steps:"
    echo "1. Configure GitHub repository (see GITHUB_SETUP.md)"
    echo "   Required secrets: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, DB_PASSWORD"
    echo "   Required variables: BILLING_ALERT_EMAIL"
    
    # Show domain-specific information
    DOMAIN_OUTPUT=$(tofu output -raw domain_name 2>/dev/null || echo "")
    if [[ -n "$DOMAIN_OUTPUT" && "$DOMAIN_OUTPUT" != "null" ]]; then
        echo
        print_step "🌐 Custom Domain Configuration:"
        echo "Domain: $DOMAIN_OUTPUT"
        echo "Certificate: Created and validated automatically"
        echo
        echo "📍 Point your domain to these name servers:"
        tofu output -json route53_name_servers 2>/dev/null | jq -r '.[]' 2>/dev/null || echo "Check Route 53 console for name servers"
        echo
        print_warning "Important: Update your domain's name servers with your registrar!"
    else
        echo
        print_step "🌐 Using ALB DNS name (no custom domain configured)"
        ALB_DNS=$(tofu output -raw load_balancer_dns_name 2>/dev/null || echo "")
        if [[ -n "$ALB_DNS" ]]; then
            echo "Access your application at: http://$ALB_DNS"
        fi
    fi
    
    echo
    echo "2. Deploy applications:"
    echo "   git push origin main  # Triggers automatic deployment"
    echo
    echo "3. Verify deployment:"
    APP_URL=$(tofu output -raw application_url 2>/dev/null || echo "")
    API_URL=$(tofu output -raw api_url 2>/dev/null || echo "")
    if [[ -n "$APP_URL" ]]; then
        echo "   Frontend: $APP_URL"
    fi
    if [[ -n "$API_URL" ]]; then
        echo "   API: $API_URL"
        echo "   Health: $API_URL/../actuator/health"
    fi
    
    echo
    echo "🛠️  Management commands:"
    echo "- Check services: aws ecs list-services --cluster bau-cluster"
    echo "- View logs: aws logs tail /ecs/bau-backend --follow"
    echo "- Update domain: Edit infrastructure/tofu.tfvars and run tofu apply"
    echo "- Destroy: cd infrastructure && tofu destroy"
}

# Main execution
main() {
    echo "🏗️  Bau Platform - AWS Infrastructure Deployment"
    echo "=============================================="
    echo
    
    check_dependencies
    deploy_infrastructure
    setup_github_secrets
    
    # Ask if user wants to push initial images
    echo
    read -p "Do you want to build and push initial Docker images? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        push_initial_images
        update_ecs_services
    fi
    
    show_completion_info
}

# Run main function
main 