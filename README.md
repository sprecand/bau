# Bau Platform

**B2B platform exclusively for construction companies** to share temporary workforce needs. Construction companies can post labor requirements and other construction companies can provide workers to meet those needs.

**Important**: This platform is designed solely for business-to-business relationships between construction companies. Individual workers cannot register or use the platform directly.

## Quick Start

### Prerequisites
- Java 21
- Node.js 20+
- Docker & Docker Compose
- AWS CLI configured (for deployment)
- OpenTofu (for infrastructure management)

### Local Development
```bash
# Clone repository
git clone <repository-url>
cd bau

# Start database
docker-compose up -d postgres

# Backend (Spring Boot)
cd backend
mvn spring-boot:run

# Frontend (Angular)
cd frontend
npm install
npm start
```

### Test Docker Builds
```bash
# Test containers before deployment
./test-docker-build.sh
```

### Deploy Infrastructure to AWS
```bash
# One-time infrastructure setup with OpenTofu
./deploy-aws.sh

# Or manual steps:
cd infrastructure
make deploy  # Initialize, plan, and apply
```

### Deploy Application to AWS
```bash
# After infrastructure is set up:
# Automatic deployment on push to main
git push origin main

# Or manual deployment via GitHub Actions
# Go to: Actions → "CD - Deploy" → "Run workflow"
```

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