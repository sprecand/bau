# Bau Platform

**B2B platform exclusively for construction companies** to share temporary workforce needs.

Construction companies can post labor requirements and other construction companies can provide workers to meet those needs.

**Important**: This platform is designed solely for business-to-business relationships between construction companies.
Individual workers cannot register or use the platform directly.

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

# Set up environment
cp env.template .env
# Edit .env with your configuration

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

# Or manual steps
cd infrastructure
make deploy  # Initialize, plan, and apply
```

### Deploy Application to AWS

```bash
# After infrastructure is set up

# Automatic deployment on push to main
git push origin main

# Or manual deployment via GitHub Actions
# Go to: Actions → "CD - Deploy" → "Run workflow"
```

## 💰 Cost Management

**Automatic daily schedule: 9 AM - 9 PM CET/CEST** (already enabled!)

```bash
# Check current status and costs
./manage-app.sh status

# Manual override: Turn app ON
./manage-app.sh start

# Manual override: Turn app OFF
./manage-app.sh stop

# Completely destroy infrastructure (costs $0/month)
./manage-app.sh destroy
```

**Cost breakdown:**

- **Auto-schedule (9AM-9PM)**: ~$38/month (50% savings)
- **Manual control**: ~$30-47/month  
- **Always stopped**: ~$30/month (infrastructure only)
- **Destroyed**: $0/month

**Your app automatically:**
- ✅ **Starts at 9 AM** (including weekends)
- ✅ **Stops at 9 PM** daily
- ✅ **Saves ~$10/month** vs 24/7 running

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

### Next Steps

- Frontend-backend integration
- Comprehensive testing
- Performance optimization
- Production deployment

## Contributing

1. Follow the [coding standards](doc/tech/08-cross-cutting-concepts/coding-standards.md)
2. Write tests for new functionality
3. Update documentation as needed
4. Submit pull requests for review

## License

Private project - All rights reserved.
