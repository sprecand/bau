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
    
    cd infrastructure
    
    # Initialize OpenTofu
    print_step "Initializing OpenTofu..."
    tofu init
    
    # Validate configuration
    print_step "Validating configuration..."
    tofu validate
    
    # Plan deployment
    print_step "Planning deployment..."
    tofu plan -var-file=tofu.tfvars -var="alert_email=$ALERT_EMAIL"
    
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
    tofu apply -var-file=tofu.tfvars -var="alert_email=$ALERT_EMAIL" -auto-approve
    
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
    echo "Next steps:"
    echo "1. Add GitHub secrets:"
    echo "   - AWS_ACCESS_KEY_ID"
    echo "   - AWS_SECRET_ACCESS_KEY" 
    echo "   - BILLING_ALERT_EMAIL (your email for cost alerts)"
    echo "2. Push to main branch to trigger automatic deployment"
    echo "3. Monitor deployment in GitHub Actions"
    echo
    echo "Alternative deployment methods:"
    echo "- With email env var: ALERT_EMAIL=you@example.com ./deploy-aws.sh"
    echo "- Manual: cd infrastructure && make deploy"
    echo
    echo "Useful commands:"
    echo "- Check ECS services: aws ecs list-services --cluster bau-cluster"
    echo "- View logs: aws logs tail /ecs/bau-backend --follow"
    echo "- Destroy infrastructure: cd infrastructure && make destroy"
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