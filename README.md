# Bau Platform

B2B platform for construction companies to coordinate temporary workforce requirements.

Construction companies can post labor requirements and connect with other construction companies to fulfill those needs through temporary worker assignments.

**Business Model**: Exclusively B2B relationships between registered construction companies. Individual workers cannot register directly.

## Quick Start

### Prerequisites

- Java 21
- Node.js 20+
- Docker & Docker Compose
- AWS CLI configured
- OpenTofu for infrastructure management

### Local Development

```bash
git clone <repository-url>
cd bau

# Environment setup
cp env.template .env
# Configure .env with required settings

# Start database
docker-compose up -d postgres

# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm start
```

### Deployment

```bash
# Infrastructure setup (one-time)
./deploy-aws.sh

# Application deployment
git push origin main  # Automatic via CI/CD
```

## Cost Management

**Automatic Schedule**: 9 AM - 9 PM CET/CEST daily

```bash
# Status and cost monitoring
./manage-app.sh status

# Manual controls
./manage-app.sh start    # Override: force start
./manage-app.sh stop     # Override: force stop
./manage-app.sh destroy  # Remove infrastructure
```

**Monthly Costs**:
- Auto-schedule (9AM-9PM): ~$38
- Manual control: ~$30-47
- Always stopped: ~$30 (infrastructure only)
- Destroyed: $0

## Architecture

- **Frontend**: Angular 20+ with TypeScript
- **Backend**: Spring Boot 3.x with Java 21 (Hexagonal Architecture)
- **Database**: PostgreSQL 15
- **Authentication**: AWS Cognito
- **Infrastructure**: AWS ECS, RDS, CloudFront

## Documentation

- [Business Requirements](doc/fach/fachlich.md)
- [Technical Documentation](doc/tech/README.md)
- [API Specification](api/)

## Development Status

**Phase**: MVP Development

**Current**: Core domain implementation, authentication system, basic operations
**Next**: Frontend-backend integration, testing, production deployment

## Development Guidelines

1. Follow [coding standards](doc/tech/08-cross-cutting-concepts/coding-standards.md)
2. Write comprehensive tests
3. Update documentation with changes
4. Submit pull requests for review

## License

Private project. All rights reserved.
