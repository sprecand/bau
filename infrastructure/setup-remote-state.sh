#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="bau"
AWS_REGION="eu-central-1"
STATE_BUCKET="${PROJECT_NAME}-terraform-state"
LOCK_TABLE="${PROJECT_NAME}-terraform-locks"

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

check_prerequisites() {
    print_step "Checking prerequisites..."
    
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
    
    # Get AWS account ID and region
    AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    CURRENT_REGION=$(aws configure get region)
    
    if [[ "$CURRENT_REGION" != "$AWS_REGION" ]]; then
        print_warning "Your configured AWS region ($CURRENT_REGION) differs from the expected region ($AWS_REGION)"
        echo "You may want to run: aws configure set region $AWS_REGION"
    fi
    
    print_success "Prerequisites check passed"
    echo "AWS Account ID: $AWS_ACCOUNT_ID"
    echo "AWS Region: $CURRENT_REGION"
}

create_s3_bucket() {
    print_step "Creating S3 bucket for Terraform state..."
    
    # Check if bucket already exists
    if aws s3api head-bucket --bucket "$STATE_BUCKET" 2>/dev/null; then
        print_warning "S3 bucket '$STATE_BUCKET' already exists"
        return 0
    fi
    
    # Create bucket
    if [[ "$AWS_REGION" == "us-east-1" ]]; then
        aws s3api create-bucket --bucket "$STATE_BUCKET" --region "$AWS_REGION"
    else
        aws s3api create-bucket --bucket "$STATE_BUCKET" --region "$AWS_REGION" \
            --create-bucket-configuration LocationConstraint="$AWS_REGION"
    fi
    
    # Enable versioning
    aws s3api put-bucket-versioning --bucket "$STATE_BUCKET" \
        --versioning-configuration Status=Enabled
    
    # Enable server-side encryption
    aws s3api put-bucket-encryption --bucket "$STATE_BUCKET" \
        --server-side-encryption-configuration '{
            "Rules": [
                {
                    "ApplyServerSideEncryptionByDefault": {
                        "SSEAlgorithm": "AES256"
                    }
                }
            ]
        }'
    
    # Block public access
    aws s3api put-public-access-block --bucket "$STATE_BUCKET" \
        --public-access-block-configuration \
        BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
    
    print_success "S3 bucket '$STATE_BUCKET' created successfully"
}

create_dynamodb_table() {
    print_step "Creating DynamoDB table for state locking..."
    
    # Check if table already exists
    if aws dynamodb describe-table --table-name "$LOCK_TABLE" &>/dev/null; then
        print_warning "DynamoDB table '$LOCK_TABLE' already exists"
        return 0
    fi
    
    # Create table
    aws dynamodb create-table \
        --table-name "$LOCK_TABLE" \
        --attribute-definitions AttributeName=LockID,AttributeType=S \
        --key-schema AttributeName=LockID,KeyType=HASH \
        --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
        --region "$AWS_REGION"
    
    # Wait for table to be created
    print_step "Waiting for DynamoDB table to be created..."
    aws dynamodb wait table-exists --table-name "$LOCK_TABLE" --region "$AWS_REGION"
    
    print_success "DynamoDB table '$LOCK_TABLE' created successfully"
}

show_completion_info() {
    print_success "🎉 Remote state setup completed!"
    echo
    echo "Created resources:"
    echo "- S3 Bucket: $STATE_BUCKET"
    echo "- DynamoDB Table: $LOCK_TABLE"
    echo
    echo "Your Terraform configuration is ready to use remote state."
    echo "The backend configuration in main.tf should work correctly now."
    echo
    echo "Next steps:"
    echo "1. Run 'tofu init' to initialize the backend"
    echo "2. Run 'tofu plan' to preview changes"
    echo "3. Run 'tofu apply' to deploy infrastructure"
    echo
    echo "Or use the deployment script: ./deploy-aws.sh"
}

# Main execution
main() {
    echo "🏗️  Bau Platform - Remote State Setup"
    echo "====================================="
    echo
    
    check_prerequisites
    create_s3_bucket
    create_dynamodb_table
    show_completion_info
}

# Run main function
main "$@" 