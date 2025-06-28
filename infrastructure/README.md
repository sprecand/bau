# Bau Platform - AWS Infrastructure

This directory contains the OpenTofu configuration for deploying the Bau platform infrastructure on AWS.

## What's Included

- **VPC & Networking**: Custom VPC with public subnets across 2 AZs
- **ECR Repositories**: For storing Docker images (backend & frontend)
- **ECS Cluster**: Fargate cluster for running containers
- **ECS Services**: Auto-scaling services for backend and frontend
- **Security Groups**: Proper network security configuration
- **CloudWatch**: Logging and monitoring
- **IAM Roles**: Necessary permissions for ECS tasks

## Quick Start

### Prerequisites
- [OpenTofu](https://opentofu.org/docs/intro/install/) installed
- [AWS CLI](https://aws.amazon.com/cli/) configured
- Docker (for building and pushing images)

### Deploy Everything
```bash
# From project root
./deploy-aws.sh
```

### Manual Deployment
```bash
# From infrastructure directory
make help          # Show all available commands
make deploy        # Full deployment
make plan          # Preview changes
make apply         # Apply changes
make destroy       # Destroy infrastructure
make output        # Show outputs
```

## Configuration

Edit `terraform.tfvars` to customize:
```hcl
project_name = "bau"
aws_region   = "eu-central-1"
environment  = "main"
```

## Outputs

After deployment, you'll get:
- ECR repository URLs
- ECS cluster and service names
- VPC and subnet information
- All values needed for GitHub Actions

## GitHub Actions Integration

The infrastructure outputs are designed to work with the GitHub Actions workflow (`.github/workflows/cd-main.yml`).

Required GitHub Secrets:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

## Cost Optimization

- **ECS Fargate**: Pay only when containers are running
- **ECR**: Free tier includes 500MB storage
- **CloudWatch**: 7-day log retention to minimize costs
- **VPC**: Only public subnets (no NAT Gateway costs)

## Security

- Non-root containers
- Security groups with minimal required access
- IAM roles with least privilege
- VPC isolation

## Monitoring

- CloudWatch logs: `/ecs/bau-backend` and `/ecs/bau-frontend`
- ECS service health checks
- Container insights enabled

## Cleanup

```bash
make destroy  # Remove all infrastructure
```

⚠️ **Warning**: This will permanently delete all resources including data! 