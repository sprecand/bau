# Bau Platform

**B2B platform exclusively for construction companies**to share temporary workforce needs.
  Construction companies can post labor requirements and other construction companies can provide workers to meet those needs.**Important**: This platform is designed solely for business-to-business relationships between construction companies. Individual workers cannot register or use the platform directly.

## Quick Start

### Prerequisites

- Java 21
- Node.js 20+
- Docker & Docker Compose
- AWS CLI configured (for deployment)
- OpenTofu (for infrastructure management)

### Local Development

```bash

## Clone repository

git clone <repository-url>
cd bau

## Set up environment

cp env.template .env

## Edit .env with your configuration

## Start database

docker-compose up -d postgres

## Backend (Spring Boot)

cd backend
mvn spring-boot:run

## Frontend (Angular)

cd frontend
npm install
npm start

```

## Clone repository

git clone <repository-url>
cd bau

## Set up environment

cp env.template .env

## Edit .env with your configuration

## Start database

docker-compose up -d postgres

## Backend (Spring Boot)

cd backend
mvn spring-boot:run

## Frontend (Angular)

cd frontend
npm install
npm start

```

### Test Docker Builds

```bash

### Test Docker Builds

```bash

## Test containers before deployment

./test-docker-build.sh

```

## Test containers before deployment

./test-docker-build.sh

```

### Deploy Infrastructure to AWS

```bash

### Deploy Infrastructure to AWS

```bash

## One-time infrastructure setup with OpenTofu

./deploy-aws.sh

## Or manual steps

cd infrastructure
make deploy  # Initialize, plan, and apply

```

## One-time infrastructure setup with OpenTofu

./deploy-aws.sh

## Or manual steps

cd infrastructure
make deploy  # Initialize, plan, and apply

```

### Deploy Application to AWS

```bash

### Deploy Application to AWS

```bash

## After infrastructure is set up

## Automatic deployment on push to main

git push origin main

## Or manual deployment via GitHub Actions

## Go to: Actions → "CD - Deploy" → "Run workflow"

```

## After infrastructure is set up

## Automatic deployment on push to main

git push origin main

## Or manual deployment via GitHub Actions

## Go to: Actions → "CD - Deploy" → "Run workflow"

```

## 💰 Cost Management

**Save money with the AWS management script!**```bash

## 💰 Cost Management**Save money with the AWS management script!**```bash

## Turn app ON (costs ~$17/month)

./manage-app.sh start

## Turn app OFF (saves ~$16/month, costs ~$0.30/month)

./manage-app.sh stop

## Check current status and costs

./manage-app.sh status

## Completely destroy infrastructure (saves ~$17/month)

./manage-app.sh destroy

```

## Turn app ON (costs ~$17/month)

./manage-app.sh start

## Turn app OFF (saves ~$16/month, costs ~$0.30/month)

./manage-app.sh stop

## Check current status and costs

./manage-app.sh status

## Completely destroy infrastructure (saves ~$17/month)

./manage-app.sh destroy

```**Smart cost savings:**-**Daily on/off**: Save ~$5-8/month (work 8 hours/day)
- **Weekend shutdown**: Save ~$4-5/month
- **Auto-shutdown**: 10 PM UTC daily (already enabled)

📖 **Full guide**: [AWS Cost Management Guide](AWS-MANAGEMENT.md)

## Documentation

- [Business Requirements](doc/fach/fachlich.md) - Domain model and business rules
- [Technical Documentation](doc/tech/README.md) - Architecture and development guide
- [API Specification](api/) - OpenAPI specification

## Architecture

- **Frontend**: Angular 20+ with TypeScript
- **Backend**: Spring Boot 3.x with Java 21 (Hexagonal Architecture)
- **Database**: PostgreSQL 15
- **Authentication**: AWS Cognito
- **Infrastructure**: AWS (ECS, RDS, CloudFront)

## Project Status

**In Development**- MVP phase

### Current Focus

- Core domain implementation (Bedarf, Betrieb)
- Authentication system
- Basic CRUD operations
- Pilot program setup**Smart cost savings:**-**Daily on/off**: Save ~$5-8/month (work 8 hours/day)
- **Weekend shutdown**: Save ~$4-5/month
- **Auto-shutdown**: 10 PM UTC daily (already enabled)

📖 **Full guide**: [AWS Cost Management Guide](AWS-MANAGEMENT.md)

## Documentation

- [Business Requirements](doc/fach/fachlich.md) - Domain model and business rules
- [Technical Documentation](doc/tech/README.md) - Architecture and development guide
- [API Specification](api/) - OpenAPI specification

## Architecture

- **Frontend**: Angular 20+ with TypeScript
- **Backend**: Spring Boot 3.x with Java 21 (Hexagonal Architecture)
- **Database**: PostgreSQL 15
- **Authentication**: AWS Cognito
- **Infrastructure**: AWS (ECS, RDS, CloudFront)

## Project Status

**In Development** - MVP phase

### Current Focus

- Core domain implementation (Bedarf, Betrieb)
- Authentication system
- Basic CRUD operations
- Pilot program setup
