#!/bin/bash

# Bau Application Management Script
# Controls AWS ECS services for cost optimization and deployment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
CLUSTER_NAME="bau-cluster"
BACKEND_SERVICE="bau-backend"
FRONTEND_SERVICE="bau-frontend"
REGION="eu-central-1"

# Helper functions
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

check_aws_cli() {
    if ! command -v aws &> /dev/null; then
        log_error "AWS CLI not found. Please install and configure AWS CLI."
        exit 1
    fi
}

check_cluster_exists() {
    if ! aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$REGION" &> /dev/null; then
        log_error "ECS cluster '$CLUSTER_NAME' not found. Deploy infrastructure first."
        exit 1
    fi
}

get_service_status() {
    local service_name=$1
    aws ecs describe-services \
        --cluster "$CLUSTER_NAME" \
        --services "$service_name" \
        --region "$REGION" \
        --query 'services[0].desiredCount' \
        --output text 2>/dev/null || echo "0"
}

get_current_version() {
    if [[ -f "deploy/production.yaml" ]]; then
        grep "version:" deploy/production.yaml | head -1 | cut -d '"' -f 2
    else
        echo "unknown"
    fi
}

# Functions
print_header() {
    echo -e "${BLUE}🏗️  Bau Platform - AWS App Manager${NC}"
    echo -e "${BLUE}====================================${NC}"
    echo
}

print_status() {
    echo -e "${YELLOW}📊 Current Status:${NC}"
    
    # Get service info
    BACKEND_INFO=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $BACKEND_SERVICE --region $REGION --query 'services[0].[desiredCount,runningCount,status]' --output text 2>/dev/null || echo "0 0 INACTIVE")
    FRONTEND_INFO=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $FRONTEND_SERVICE --region $REGION --query 'services[0].[desiredCount,runningCount,status]' --output text 2>/dev/null || echo "0 0 INACTIVE")
    
    read -r BACKEND_DESIRED BACKEND_RUNNING BACKEND_STATUS <<< "$BACKEND_INFO"
    read -r FRONTEND_DESIRED FRONTEND_RUNNING FRONTEND_STATUS <<< "$FRONTEND_INFO"
    
    # Backend status
    if [[ "$BACKEND_DESIRED" -gt 0 ]]; then
        echo -e "   Backend:  ${GREEN}🟢 ON${NC}  (${BACKEND_RUNNING}/${BACKEND_DESIRED} running)"
    else
        echo -e "   Backend:  ${RED}🔴 OFF${NC} (scaled to 0)"
    fi
    
    # Frontend status
    if [[ "$FRONTEND_DESIRED" -gt 0 ]]; then
        echo -e "   Frontend: ${GREEN}🟢 ON${NC}  (${FRONTEND_RUNNING}/${FRONTEND_DESIRED} running)"
    else
        echo -e "   Frontend: ${RED}🔴 OFF${NC} (scaled to 0)"
    fi
    
    # Cost estimate
    TOTAL_SERVICES=$((BACKEND_DESIRED + FRONTEND_DESIRED))
    if [[ $TOTAL_SERVICES -gt 0 ]]; then
        HOURLY_COST=$(echo "scale=3; $TOTAL_SERVICES * 0.012" | bc)
        DAILY_COST_24H=$(echo "scale=2; $HOURLY_COST * 24" | bc)
        DAILY_COST_12H=$(echo "scale=2; $HOURLY_COST * 12" | bc)
        MONTHLY_COST_24H=$(echo "scale=2; $DAILY_COST_24H * 30" | bc)
        MONTHLY_COST_12H=$(echo "scale=2; $DAILY_COST_12H * 30 + 30" | bc)  # +30 for infrastructure
        MONTHLY_COST_24H_TOTAL=$(echo "scale=2; $MONTHLY_COST_24H + 30" | bc)  # +30 for infrastructure
        echo -e "   💰 Running cost: \$${HOURLY_COST}/hour (compute only)"
        echo -e "   💰 With auto-schedule (9AM-9PM): ~\$${MONTHLY_COST_12H}/month total"
        echo -e "   💰 If running 24/7: ~\$${MONTHLY_COST_24H_TOTAL}/month total"
    else
        echo -e "   ${GREEN}💰 Current cost: ~\$30/month (infrastructure only)${NC}"
    fi
    echo
}

start_app() {
    log_info "Starting Bau application..."
    check_aws_cli
    check_cluster_exists
    
    # Scale up services
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$BACKEND_SERVICE" \
        --desired-count 1 \
        --region "$REGION" > /dev/null
    
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$FRONTEND_SERVICE" \
        --desired-count 1 \
        --region "$REGION" > /dev/null
    
    log_success "Application is starting up..."
    log_info "It may take 2-3 minutes for services to be fully available."
}

stop_app() {
    log_info "Stopping Bau application..."
    check_aws_cli
    check_cluster_exists
    
    # Scale down services
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$BACKEND_SERVICE" \
        --desired-count 0 \
        --region "$REGION" > /dev/null
    
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$FRONTEND_SERVICE" \
        --desired-count 0 \
        --region "$REGION" > /dev/null
    
    log_success "Application stopped. You're now saving money! 💰"
}

show_status() {
    log_info "Checking Bau application status..."
    check_aws_cli
    
    if ! check_cluster_exists &> /dev/null; then
        log_warning "Infrastructure not deployed"
        return
    fi
    
    local backend_count=$(get_service_status "$BACKEND_SERVICE")
    local frontend_count=$(get_service_status "$FRONTEND_SERVICE")
    local current_version=$(get_current_version)
    
    echo ""
    echo "=== BAU APPLICATION STATUS ==="
    echo "Current Version: $current_version"
    echo "Backend Service: $backend_count replica(s)"
    echo "Frontend Service: $frontend_count replica(s)"
    echo ""
    
    if [[ "$backend_count" == "0" && "$frontend_count" == "0" ]]; then
        echo "Status: 🔴 STOPPED"
        echo "Monthly cost: ~$30 (infrastructure only)"
    elif [[ "$backend_count" == "1" && "$frontend_count" == "1" ]]; then
        echo "Status: 🟢 RUNNING"
        echo "Monthly cost: ~$38 (with auto-schedule 9AM-9PM)"
        echo "             ~$47 (if running 24/7)"
    else
        echo "Status: 🟡 PARTIAL (mixed state)"
        echo "Monthly cost: ~$34-42"
    fi
    echo ""
}

deploy_version() {
    local version=$1
    
    if [[ -z "$version" ]]; then
        log_error "Version required. Usage: $0 deploy <version>"
        echo "Example: $0 deploy v1.2.3"
        echo ""
        log_info "🎯 GitOps Way: Edit deploy/production.yaml and commit the change"
        exit 1
    fi
    
    log_info "GitOps deployment for version $version..."
    
    # Check if version exists in ECR
    log_info "Checking if version $version exists..."
    
    # Remove 'v' prefix if present
    VERSION=${version#v}
    
    # Get ECR registry (you'll need to set AWS_ACCOUNT_ID)
    ECR_REGISTRY="${AWS_ACCOUNT_ID:-123456789}.dkr.ecr.eu-central-1.amazonaws.com"
    BACKEND_IMAGE="$ECR_REGISTRY/bau-backend:$VERSION"
    FRONTEND_IMAGE="$ECR_REGISTRY/bau-frontend:$VERSION"
    
    # Update production.yaml file
    log_info "Updating deploy/production.yaml..."
    
    cat > deploy/production.yaml << EOF
apiVersion: v1
kind: Config
metadata:
  name: bau-production
  version: "$VERSION"
  updated: "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  description: "Production deployment configuration for Bau platform"
spec:
  backend:
    image: "$BACKEND_IMAGE"
    version: "$VERSION"
    replicas: 1
    resources:
      cpu: "256"
      memory: "512"
  frontend:
    image: "$FRONTEND_IMAGE"
    version: "$VERSION"
    replicas: 1
    resources:
      cpu: "256"
      memory: "512"
environment:
  name: "production"
  region: "eu-central-1"
  cluster: "bau-cluster"
EOF
    
    log_success "Updated deploy/production.yaml to version $VERSION"
    echo ""
    log_info "🔄 Next steps:"
    echo "  1. Review the changes: git diff deploy/production.yaml"
    echo "  2. Commit and push: git add deploy/production.yaml && git commit -m 'deploy: update to $version' && git push"
    echo "  3. GitHub Actions will automatically deploy this version"
    echo ""
    log_warning "💡 Pure GitOps: The deployment happens when you push the config change to Git!"
}

list_versions() {
    log_info "Available versions..."
    
    if command -v gh &> /dev/null; then
        echo ""
        echo "=== RECENT RELEASES ==="
        gh release list --limit 10
        echo ""
        echo "Use: $0 deploy <version> to deploy a specific version"
    else
        log_warning "GitHub CLI (gh) not found."
        log_info "Check available releases at:"
        log_info "https://github.com/YOUR_REPO/releases"
    fi
}

create_release() {
    local version_type=${1:-patch}
    
    log_info "Creating new release ($version_type)..."
    
    if command -v gh &> /dev/null; then
        gh workflow run release.yml \
            --ref main \
            -f version_type="$version_type"
        
        log_success "Release workflow triggered ($version_type increment)"
        log_info "Check progress at: https://github.com/$(gh repo view --json owner,name -q '.owner.login + "/" + .name')/actions"
    else
        log_warning "GitHub CLI (gh) not found."
        log_info "Please manually trigger the 'Release' workflow at:"
        log_info "https://github.com/YOUR_REPO/actions/workflows/release.yml"
    fi
}

destroy_infrastructure() {
    log_warning "This will COMPLETELY DESTROY all AWS infrastructure!"
    log_warning "All data will be lost permanently."
    echo ""
    read -p "Are you absolutely sure? Type 'destroy' to confirm: " -r
    echo ""
    
    if [[ $REPLY == "destroy" ]]; then
        log_info "Destroying infrastructure..."
        
        cd infrastructure
        if command -v tofu &> /dev/null; then
            tofu destroy -auto-approve
        else
            log_error "OpenTofu not found. Please destroy manually via AWS console."
            exit 1
        fi
        
        log_success "Infrastructure destroyed. Monthly cost: $0"
    else
        log_info "Destruction cancelled."
    fi
}

show_help() {
    echo "Bau Application Management Script"
    echo ""
    echo "USAGE:"
    echo "  $0 <command> [options]"
    echo ""
    echo "COMMANDS:"
    echo "  start                Start the application (scale up)"
    echo "  stop                 Stop the application (scale down, save money)"
    echo "  status               Show current application status"
    echo "  deploy <version>     Prepare deployment config (GitOps)"
    echo "  versions             List available versions"
    echo "  release [type]       Create new release (patch/minor/major)"
    echo "  destroy              Completely destroy all infrastructure"
    echo ""
    echo "EXAMPLES:"
    echo "  $0 start             # Start the app"
    echo "  $0 stop              # Stop the app to save money"
    echo "  $0 status            # Check current status"
    echo "  $0 deploy v1.2.3     # Prepare GitOps deployment config"
    echo "  $0 versions          # List available versions"
    echo "  $0 release minor     # Create minor version release"
    echo ""
    echo "GITOPS DEPLOYMENT:"
    echo "  1. $0 deploy v1.2.3         # Updates deploy/production.yaml"
    echo "  2. git add deploy/production.yaml"
    echo "  3. git commit -m 'deploy: v1.2.3'"
    echo "  4. git push                 # Triggers automatic deployment"
    echo ""
    echo "COST SUMMARY:"
    echo "  • Auto-schedule (9AM-9PM): ~$38/month"
    echo "  • Manual control: ~$30-47/month"  
    echo "  • Always stopped: ~$30/month"
    echo "  • Destroyed: $0/month"
}

# Main script logic
case "${1:-}" in
    "start")
        start_app
        ;;
    "stop")
        stop_app
        ;;
    "status")
        show_status
        ;;
    "deploy")
        deploy_version "$2"
        ;;
    "versions")
        list_versions
        ;;
    "release")
        create_release "$2"
        ;;
    "destroy")
        destroy_infrastructure
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        log_error "Unknown command: ${1:-}"
        echo ""
        show_help
        exit 1
        ;;
esac 