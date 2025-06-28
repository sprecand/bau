#!/bin/bash

# Bau App Management Script
# Turn your AWS application on/off to save money

set -e

# Configuration
CLUSTER_NAME="bau-cluster"
BACKEND_SERVICE="bau-backend"
FRONTEND_SERVICE="bau-frontend"
AWS_REGION="eu-central-1"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}🏗️  Bau Platform - AWS App Manager${NC}"
    echo -e "${BLUE}====================================${NC}"
    echo
}

print_status() {
    echo -e "${YELLOW}📊 Current Status:${NC}"
    
    # Get service info
    BACKEND_INFO=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $BACKEND_SERVICE --region $AWS_REGION --query 'services[0].[desiredCount,runningCount,status]' --output text 2>/dev/null || echo "0 0 INACTIVE")
    FRONTEND_INFO=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $FRONTEND_SERVICE --region $AWS_REGION --query 'services[0].[desiredCount,runningCount,status]' --output text 2>/dev/null || echo "0 0 INACTIVE")
    
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
        DAILY_COST=$(echo "scale=2; $HOURLY_COST * 24" | bc)
        MONTHLY_COST=$(echo "scale=2; $DAILY_COST * 30" | bc)
        echo -e "   ${PURPLE}💰 Estimated cost: \$${HOURLY_COST}/hour, \$${DAILY_COST}/day, \$${MONTHLY_COST}/month${NC}"
    else
        echo -e "   ${GREEN}💰 Current cost: ~\$0.01/day (infrastructure only)${NC}"
    fi
    echo
}

start_app() {
    echo -e "${GREEN}🚀 Starting Bau application...${NC}"
    
    echo "   Scaling backend to 1 instance..."
    aws ecs update-service --cluster $CLUSTER_NAME --service $BACKEND_SERVICE --desired-count 1 --region $AWS_REGION > /dev/null
    
    echo "   Scaling frontend to 1 instance..."
    aws ecs update-service --cluster $CLUSTER_NAME --service $FRONTEND_SERVICE --desired-count 1 --region $AWS_REGION > /dev/null
    
    echo -e "${GREEN}✅ App startup initiated!${NC}"
    echo -e "${YELLOW}⏳ Services are starting up (this may take 2-3 minutes)${NC}"
    echo
    echo "💡 You can check progress with: $0 status"
    echo
}

stop_app() {
    echo -e "${RED}🛑 Stopping Bau application...${NC}"
    
    echo "   Scaling backend to 0 instances..."
    aws ecs update-service --cluster $CLUSTER_NAME --service $BACKEND_SERVICE --desired-count 0 --region $AWS_REGION > /dev/null
    
    echo "   Scaling frontend to 0 instances..."
    aws ecs update-service --cluster $CLUSTER_NAME --service $FRONTEND_SERVICE --desired-count 0 --region $AWS_REGION > /dev/null
    
    echo -e "${GREEN}✅ App shutdown initiated!${NC}"
    echo -e "${GREEN}💰 Now saving ~\$8-10/month in compute costs${NC}"
    echo
}

wait_for_services() {
    echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
    
    echo "   Waiting for backend service..."
    aws ecs wait services-stable --cluster $CLUSTER_NAME --services $BACKEND_SERVICE --region $AWS_REGION
    
    echo "   Waiting for frontend service..."  
    aws ecs wait services-stable --cluster $CLUSTER_NAME --services $FRONTEND_SERVICE --region $AWS_REGION
    
    echo -e "${GREEN}✅ All services are stable!${NC}"
}

show_urls() {
    echo -e "${BLUE}🌐 Application URLs:${NC}"
    
    # Get load balancer DNS (you'll need to add this to your terraform outputs)
    echo "   Backend:  http://3.72.241.15:8080"
    echo "   Frontend: http://18.184.228.177:8080"
    echo
    echo -e "${YELLOW}💡 Note: URLs will be accessible once services are fully started${NC}"
    echo
}

show_help() {
    echo -e "${BLUE}Usage:${NC}"
    echo "   $0 start      - Start the application (costs ~\$8-10/month)"
    echo "   $0 stop       - Stop the application (saves ~\$8-10/month)"
    echo "   $0 status     - Show current status"
    echo "   $0 wait       - Wait for services to be ready after start"
    echo "   $0 urls       - Show application URLs"
    echo "   $0 destroy    - Completely destroy infrastructure (saves ~95%)"
    echo "   $0 help       - Show this help"
    echo
}

destroy_infrastructure() {
    echo -e "${RED}💥 DESTROYING INFRASTRUCTURE${NC}"
    echo -e "${YELLOW}⚠️  This will permanently delete:${NC}"
    echo "   - ECS Cluster & Services"
    echo "   - ECR Repositories (with all Docker images)"
    echo "   - VPC & Networking"  
    echo "   - Load Balancers"
    echo "   - CloudWatch resources"
    echo "   - All application data"
    echo
    read -p "Are you ABSOLUTELY sure? Type 'destroy' to confirm: " confirm
    
    if [[ "$confirm" == "destroy" ]]; then
        echo -e "${RED}🗑️  Destroying infrastructure...${NC}"
        make destroy
        echo -e "${GREEN}✅ Infrastructure destroyed! You're now saving ~\$10/month${NC}"
    else
        echo -e "${YELLOW}❌ Destruction cancelled${NC}"
    fi
}

check_dependencies() {
    if ! command -v aws &> /dev/null; then
        echo -e "${RED}❌ AWS CLI not found. Please install it first.${NC}"
        exit 1
    fi
    
    if ! command -v bc &> /dev/null; then
        echo -e "${YELLOW}⚠️  'bc' not found. Cost calculations will be disabled.${NC}"
    fi
    
    # Check AWS credentials
    if ! aws sts get-caller-identity &> /dev/null; then
        echo -e "${RED}❌ AWS credentials not configured. Run: aws configure${NC}"
        exit 1
    fi
}

# Main script
main() {
    print_header
    check_dependencies
    
    case "${1:-status}" in
        "start"|"on"|"up")
            print_status
            start_app
            ;;
        "stop"|"off"|"down")
            print_status
            stop_app
            ;;
        "status"|"info"|"")
            print_status
            ;;
        "wait")
            wait_for_services
            print_status
            show_urls
            ;;
        "urls"|"url"|"links")
            show_urls
            ;;
        "destroy"|"nuke")
            destroy_infrastructure
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            echo -e "${RED}❌ Unknown command: $1${NC}"
            echo
            show_help
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@" 