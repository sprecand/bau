# Deployment Guide

## Overview


Production deployment using Docker containers on AWS ECS with RDS PostgreSQL, automated via GitHub Actions CI/CD pipeline.

## Architecture


```mermaid
graph TB

```bash

subgraph "CI/CD Pipeline"
    A[GitHub Repository]
    B[GitHub Actions]
    C[ECR Container Registry]
end

```bash

```bash

subgraph "CDN & Static Files"
    D[CloudFront CDN]
    E[S3 Bucket - Static Files]
end

```bash

```bash

subgraph "Application Layer"
    F[Application Load Balancer]
    G[ECS Cluster]
    H[Backend Container]
    I[Frontend Container]
end

```bash

```bash

subgraph "Data Layer"
    J[RDS PostgreSQL]
    K[ElastiCache Redis]
end

```bash

```bash

subgraph "Authentication"
    L[AWS Cognito]
end

```bash

```bash

A --> B
B --> C
C --> G
D --> E
D --> F
F --> G
G --> H
G --> I
H --> J
H --> K
H --> L
I --> H

```bash

```bash

graph TB

```bash

subgraph "CI/CD Pipeline"
    A[GitHub Repository]
    B[GitHub Actions]
    C[ECR Container Registry]
end

```bash

```bash

subgraph "CDN & Static Files"
    D[CloudFront CDN]
    E[S3 Bucket - Static Files]
end

```bash

```bash

subgraph "Application Layer"
    F[Application Load Balancer]
    G[ECS Cluster]
    H[Backend Container]
    I[Frontend Container]
end

```bash

```bash

subgraph "Data Layer"
    J[RDS PostgreSQL]
    K[ElastiCache Redis]
end

```bash

```bash

subgraph "Authentication"
    L[AWS Cognito]
end

```bash

```bash

A --> B
B --> C
C --> G
D --> E
D --> F
F --> G
G --> H
G --> I
H --> J
H --> K
H --> L
I --> H

```bash

```bash

## CI/CD Pipeline


### GitHub Actions Workflows


#### 1. Continuous Integration (`ci.yml`)


**Triggers**: Push to `main`/`develop`, pull requests

**Jobs**:
- **Backend Tests**: Unit and integration tests with PostgreSQL service
- **Frontend Tests**: Linting and unit tests with coverage
- **Security Scan**: Trivy vulnerability scanner
- **Build**: Create artifacts (JAR + frontend dist)

**Features**:
- Caching for Maven and npm dependencies
- Test result artifacts
- Security scanning with SARIF output
- Parallel job execution

#### 2. Release Workflow (`release.yml`)


**Triggers**: Push to `main` branch or manual workflow dispatch

**Jobs**:
- **Create Release**: Build and tag Docker images
- **Update Config**: Update deployment configuration file
- **Push Images**: Push versioned images to ECR

**Features**:
- Automatic semantic versioning
- Docker image tagging with versions
- GitHub release creation
- Deployment config updates

#### 3. Deploy Workflow (`deploy.yml`)


**Triggers**: Changes to `deploy/production.yaml` or manual workflow dispatch

**Jobs**:
- **Deploy Infrastructure**: Setup AWS infrastructure if needed
- **Deploy Applications**: Deploy backend and frontend to ECS
- **Health Checks**: Verify deployment success

**Features**:
- GitOps deployment approach
- Infrastructure validation
- Health checks after deployment
- Version-controlled deployments

### Required GitHub Secrets


```bash

## CI/CD Pipeline


### GitHub Actions Workflows


#### 1. Continuous Integration (`ci.yml`)


**Triggers**: Push to `main`/`develop`, pull requests

**Jobs**:
- **Backend Tests**: Unit and integration tests with PostgreSQL service
- **Frontend Tests**: Linting and unit tests with coverage
- **Security Scan**: Trivy vulnerability scanner
- **Build**: Create artifacts (JAR + frontend dist)

**Features**:
- Caching for Maven and npm dependencies
- Test result artifacts
- Security scanning with SARIF output
- Parallel job execution

#### 2. Release Workflow (`release.yml`)


**Triggers**: Push to `main` branch or manual workflow dispatch

**Jobs**:
- **Create Release**: Build and tag Docker images
- **Update Config**: Update deployment configuration file
- **Push Images**: Push versioned images to ECR

**Features**:
- Automatic semantic versioning
- Docker image tagging with versions
- GitHub release creation
- Deployment config updates

#### 3. Deploy Workflow (`deploy.yml`)


**Triggers**: Changes to `deploy/production.yaml` or manual workflow dispatch

**Jobs**:
- **Deploy Infrastructure**: Setup AWS infrastructure if needed
- **Deploy Applications**: Deploy backend and frontend to ECS
- **Health Checks**: Verify deployment success

**Features**:
- GitOps deployment approach
- Infrastructure validation
- Health checks after deployment
- Version-controlled deployments

### Required GitHub Secrets


```bash

## AWS Credentials


AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key

## Billing Alerts


BILLING_ALERT_EMAIL=your_email@example.com

## SonarQube Analysis


SONAR_TOKEN=your_sonar_token_here

```bash

## AWS Credentials


AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key

## Billing Alerts


BILLING_ALERT_EMAIL=your_email@example.com

## SonarQube Analysis


SONAR_TOKEN=your_sonar_token_here

```bash

## Prerequisites


- **AWS CLI**configured
-**Docker**installed
-**OpenTofu**(for infrastructure)
-**Domain name**configured
-**GitHub repository**with Actions enabled

## Infrastructure Setup


### 1. Create Infrastructure


```bash

## Prerequisites


-**AWS CLI**configured
-**Docker**installed
-**OpenTofu**(for infrastructure)
-**Domain name**configured
-**GitHub repository**with Actions enabled

## Infrastructure Setup


### 1. Create Infrastructure


```bash

## Clone infrastructure repo


git clone https://github.com/your-org/bau-infrastructure.git
cd bau-infrastructure

## Initialize OpenTofu


tofu init

## Plan deployment


tofu plan -var-file=tofu.tfvars

## Apply infrastructure


tofu apply -var-file=tofu.tfvars

```bash

## Clone infrastructure repo


git clone https://github.com/your-org/bau-infrastructure.git
cd bau-infrastructure

## Initialize OpenTofu


tofu init

## Plan deployment


tofu plan -var-file=tofu.tfvars

## Apply infrastructure


tofu apply -var-file=tofu.tfvars

```bash

### 2. Environment Variables


```env

### 2. Environment Variables


```env

## Production environment


AWS_REGION=eu-central-1
AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
SPRING_DATASOURCE_URL=jdbc:postgresql://bau-prod.cluster-xyz.eu-central-1.rds.amazonaws.com:5432/bau_prod
SPRING_DATASOURCE_USERNAME=bau_prod_user
SPRING_DATASOURCE_PASSWORD=secure_password_here

```bash

## Production environment


AWS_REGION=eu-central-1
AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
SPRING_DATASOURCE_URL=jdbc:postgresql://bau-prod.cluster-xyz.eu-central-1.rds.amazonaws.com:5432/bau_prod
SPRING_DATASOURCE_USERNAME=bau_prod_user
SPRING_DATASOURCE_PASSWORD=secure_password_here

```bash

## Application Deployment


### Automated Deployment (Recommended)


The GitHub Actions workflows handle all deployment automatically:

1.**Development**: Push to `develop` → automatic staging deployment
1. **Production**: Manual workflow dispatch → approval → production deployment

### Manual Deployment (Fallback)


```bash

## Application Deployment


### Automated Deployment (Recommended)


The GitHub Actions workflows handle all deployment automatically:

1. **Development**: Push to `develop` → automatic staging deployment
2. **Production**: Manual workflow dispatch → approval → production deployment

### Manual Deployment (Fallback)


```bash

## Build Images


docker build -t bau-backend:latest ./backend
docker tag bau-backend:latest 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-backend:latest

docker build -t bau-frontend:latest ./frontend
docker tag bau-frontend:latest 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-frontend:latest

## Push to ECR


aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.eu-central-1.amazonaws.com
docker push 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-backend:latest
docker push 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-frontend:latest

## Deploy to ECS


aws ecs update-service --cluster bau-cluster --service bau-backend --force-new-deployment
aws ecs update-service --cluster bau-cluster --service bau-frontend --force-new-deployment

```bash

## Build Images


docker build -t bau-backend:latest ./backend
docker tag bau-backend:latest 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-backend:latest

docker build -t bau-frontend:latest ./frontend
docker tag bau-frontend:latest 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-frontend:latest

## Push to ECR


aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.eu-central-1.amazonaws.com
docker push 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-backend:latest
docker push 123456789012.dkr.ecr.eu-central-1.amazonaws.com/bau-frontend:latest

## Deploy to ECS


aws ecs update-service --cluster bau-cluster --service bau-backend --force-new-deployment
aws ecs update-service --cluster bau-cluster --service bau-frontend --force-new-deployment

```bash

## Docker Configuration


### Backend Dockerfile


```dockerfile

## Docker Configuration


### Backend Dockerfile


```dockerfile

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/bau-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

```bash

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/bau-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

```bash

### Frontend Dockerfile


```dockerfile

### Frontend Dockerfile


```dockerfile

FROM nginx:alpine

COPY dist/bau-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

```bash

FROM nginx:alpine

COPY dist/bau-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

```bash

### Docker Compose (Development)


```yaml

### Docker Compose (Development)


```yaml

version: '3.8'

services:
  postgres:

```bash

image: postgres:15
environment:
  POSTGRES_DB: bau_dev
  POSTGRES_USER: bau_user
  POSTGRES_PASSWORD: bau_password
ports:
- "5432:5432"
volumes:
- postgres_data:/var/lib/postgresql/data

```bash

  backend:

```bash

build: ./backend
ports:
- "8080:8080"
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bau_dev
  SPRING_DATASOURCE_USERNAME: bau_user
  SPRING_DATASOURCE_PASSWORD: bau_password
depends_on:
- postgres

```bash

  frontend:

```bash

build: ./frontend
ports:
- "4200:80"
depends_on:
- backend

```bash

volumes:
  postgres_data:

```bash

version: '3.8'

services:
  postgres:

```bash

image: postgres:15
environment:
  POSTGRES_DB: bau_dev
  POSTGRES_USER: bau_user
  POSTGRES_PASSWORD: bau_password
ports:
- "5432:5432"
volumes:
- postgres_data:/var/lib/postgresql/data

```bash

  backend:

```bash

build: ./backend
ports:
- "8080:8080"
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bau_dev
  SPRING_DATASOURCE_USERNAME: bau_user
  SPRING_DATASOURCE_PASSWORD: bau_password
depends_on:
- postgres

```bash

  frontend:

```bash

build: ./frontend
ports:
- "4200:80"
depends_on:
- backend

```bash

volumes:
  postgres_data:

```bash

## Monitoring and Logging


### CloudWatch Logs


```yaml

## Monitoring and Logging


### CloudWatch Logs


```yaml

## Backend logging configuration


logging:
  driver: awslogs
  options:

```bash

awslogs-group: /ecs/bau-backend
awslogs-region: eu-central-1
awslogs-stream-prefix: ecs

```bash

```bash

## Backend logging configuration


logging:
  driver: awslogs
  options:

```bash

awslogs-group: /ecs/bau-backend
awslogs-region: eu-central-1
awslogs-stream-prefix: ecs

```bash

```bash

### Health Checks


```yaml

### Health Checks


```yaml

## ECS service health check


healthCheck:
  command:

```bash

- CMD-SHELL
- curl -f http://localhost:8080/actuator/health || exit 1

```bash

  interval: 30
  timeout: 5
  retries: 3
  startPeriod: 60

```bash

## ECS service health check


healthCheck:
  command:

```bash

- CMD-SHELL
- curl -f http://localhost:8080/actuator/health || exit 1

```bash

  interval: 30
  timeout: 5
  retries: 3
  startPeriod: 60

```bash

## Security


### Secrets Management


- Use AWS Secrets Manager for database passwords
- Store Cognito credentials in secure environment variables
- Enable VPC for ECS tasks
- Use IAM roles for service permissions

### Network Security


- Application Load Balancer with HTTPS
- VPC with private subnets for ECS tasks
- Security groups limiting access
- WAF for additional protection

### CI/CD Security


- GitHub Actions secrets for sensitive data
- Trivy vulnerability scanning
- SARIF integration with GitHub Security tab
- Manual approval for production deployments

## Deployment Environments


### Development


- **Branch**: `develop`
- **Deployment**: Automatic on push
- **Environment**: Staging
- **URL**: `https://staging.bau.ch`

### Production


- **Branch**: `main`
- **Deployment**: Manual approval
- **Environment**: Production
- **URL**: `https://bau.ch`

## Troubleshooting


### Common Issues


1. **ECS Service Not Updating**: Check task definition and service configuration
2. **Health Check Failures**: Verify application health endpoint
3. **Database Connection Issues**: Check security groups and credentials
4. **GitHub Actions Failures**: Review workflow logs and secrets configuration

### Rollback Procedure


```bash

## Security


### Secrets Management


- Use AWS Secrets Manager for database passwords
- Store Cognito credentials in secure environment variables
- Enable VPC for ECS tasks
- Use IAM roles for service permissions

### Network Security


- Application Load Balancer with HTTPS
- VPC with private subnets for ECS tasks
- Security groups limiting access
- WAF for additional protection

### CI/CD Security


- GitHub Actions secrets for sensitive data
- Trivy vulnerability scanning
- SARIF integration with GitHub Security tab
- Manual approval for production deployments

## Deployment Environments


### Development


- **Branch**: `develop`
- **Deployment**: Automatic on push
- **Environment**: Staging
- **URL**: `https://staging.bau.ch`

### Production


- **Branch**: `main`
- **Deployment**: Manual approval
- **Environment**: Production
- **URL**: `https://bau.ch`

## Troubleshooting


### Common Issues


1. **ECS Service Not Updating**: Check task definition and service configuration
2. **Health Check Failures**: Verify application health endpoint
3. **Database Connection Issues**: Check security groups and credentials
4. **GitHub Actions Failures**: Review workflow logs and secrets configuration

### Rollback Procedure


```bash

## Rollback to previous version


aws ecs update-service --cluster bau-cluster --service bau-backend --task-definition bau-backend:previous-version
aws ecs update-service --cluster bau-cluster --service bau-frontend --task-definition bau-frontend:previous-version

```bash

## Rollback to previous version


aws ecs update-service --cluster bau-cluster --service bau-backend --task-definition bau-backend:previous-version
aws ecs update-service --cluster bau-cluster --service bau-frontend --task-definition bau-frontend:previous-version

```bash

## Related Documentation


- [CI/CD Pipeline](../08-cross-cutting-concepts/ci-cd-pipeline.md)
- [Infrastructure Setup](../05-building-blocks/infrastructure.md)
- [Monitoring and Alerting](../06-runtime/monitoring.md)

## Related Documentation


- [CI/CD Pipeline](../08-cross-cutting-concepts/ci-cd-pipeline.md)
- [Infrastructure Setup](../05-building-blocks/infrastructure.md)
- [Monitoring and Alerting](../06-runtime/monitoring.md)
