# Technical Documentation

## Overview

Bau platform technical architecture and development guidelines.

## Documentation Structure

### 1. Introduction and Goals

- [Requirements](01-introduction/requirements.md) - Functional and non-functional requirements

### 2. Architecture Constraints

- [Architecture Decisions](09-architecture-decisions/) - ADRs for key technical decisions

### 3. System Scope and Context

- [System Overview](05-building-blocks/system-overview.md) - High-level system architecture

### 4. Solution Strategy

- [Backend Architecture](05-building-blocks/backend-architecture.md) - Hexagonal architecture implementation
- [Authentication Flow](06-runtime/authentication-flow.md) - AWS Cognito integration

### 5. Building Block View

- [Backend Architecture](05-building-blocks/backend-architecture.md) - Detailed component structure

### 6. Runtime View

- [Authentication Flow](06-runtime/authentication-flow.md) - Runtime authentication process

### 7. Deployment View

- [Deployment Guide](07-deployment/deployment.md) - Production deployment and infrastructure

### 8. Cross-cutting Concepts

- [Development Guide](08-cross-cutting-concepts/development.md) - Development setup and workflow
- [Coding Standards](08-cross-cutting-concepts/coding-standards.md) - Complete coding standards and patterns
- [Implementation Checklist](08-cross-cutting-concepts/implementation-checklist.md) - Development roadmap

### 9. Architecture Decisions

- [ADR-001: Hexagonal Architecture](09-architecture-decisions/adr-001-hexagonal-architecture.md)
- [ADR-002: AWS Cognito](09-architecture-decisions/adr-002-aws-cognito-authentication.md)
- [ADR-003: Entity & DTO Separation](09-architecture-decisions/adr-003-entity-dto-separation.md)
- [ADR-004: Naming Conventions](09-architecture-decisions/adr-004-naming-conventions.md)

### 10. Coding Standards

- [Coding Standards & Patterns](coding-standards.md) - Complete coding standards and patterns

## Quick Start

### Prerequisites

- Java 21
- Node.js 20+ (for Angular 20)
- Docker & Docker Compose
- AWS CLI configured

### Local Development

```bash

## Clone repository

git clone <repository-url>
cd bau

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

## Technology Stack

### Backend

- **Framework**: Spring Boot 3.x with Java 21
- **Architecture**: Hexagonal Architecture (Ports & Adapters)
- **Database**: PostgreSQL 15 with JPA/Hibernate
- **Authentication**: AWS Cognito with Spring Security
- **Logging**: SLF4J with Logback
- **API**: RESTful endpoints with OpenAPI specification

### Frontend

- **Framework**: Angular 20+ with TypeScript
- **UI Framework**: Angular Material
- **Styling**: Tailwind CSS
- **Authentication**: AWS Amplify
- **State Management**: Angular Signals

### Infrastructure

- **Platform**: AWS (ECS, RDS, Cognito, CloudFront)
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Monitoring**: CloudWatch

### CI/CD Pipeline

- **Continuous Integration**: Automated testing, security scanning, build artifacts
- **Staging Deployment**: Automatic deployment on `develop` branch
- **Production Deployment**: Manual approval workflow with version selection
- **Container Registry**: AWS ECR
- **Orchestration**: AWS ECS
- **Notifications**: GitHub Actions notifications

## Development Guidelines

### Code Quality

- **Logging**: Use SLF4J with proper log levels
- **Documentation**: Javadoc for all public methods
- **Comments**: Avoid inline comments, prefer Javadoc
- **Exceptions**: Minimize exception throwing, use return values
- **Testing**: Unit tests for all business logic

### Architecture Principles

- **Hexagonal Architecture**: Clear separation of concerns
- **Domain-Driven Design**: Rich domain models
- **Clean Code**: Readable, maintainable code
- **SOLID Principles**: Single responsibility, dependency inversion

### CI/CD Workflow

1. **Development**: Push to `develop` → automatic staging deployment
2. **Testing**: Automated tests, security scans, build verification
3. **Production**: Manual approval → production deployment with health checks
4. **Monitoring**: GitHub Actions status, health monitoring, rollback procedures

## Related Documentation

- [Business Requirements](../fach/fachlich.md) - Domain model and business rules
- [API Specification](../../api/) - OpenAPI specification
- [CI/CD Pipeline](07-deployment/deployment.md#cicd-pipeline) - GitHub Actions workflows

## Technology Stack

### Backend

- **Framework**: Spring Boot 3.x with Java 21
- **Architecture**: Hexagonal Architecture (Ports & Adapters)
- **Database**: PostgreSQL 15 with JPA/Hibernate
- **Authentication**: AWS Cognito with Spring Security
- **Logging**: SLF4J with Logback
- **API**: RESTful endpoints with OpenAPI specification

### Frontend

- **Framework**: Angular 20+ with TypeScript
- **UI Framework**: Angular Material
- **Styling**: Tailwind CSS
- **Authentication**: AWS Amplify
- **State Management**: Angular Signals

### Infrastructure

- **Platform**: AWS (ECS, RDS, Cognito, CloudFront)
- **Containerization**: Docker
- **CI/CD**: GitHub Actions
- **Monitoring**: CloudWatch

### CI/CD Pipeline

- **Continuous Integration**: Automated testing, security scanning, build artifacts
- **Staging Deployment**: Automatic deployment on `develop` branch
- **Production Deployment**: Manual approval workflow with version selection
- **Container Registry**: AWS ECR
- **Orchestration**: AWS ECS
- **Notifications**: GitHub Actions notifications

## Development Guidelines

### Code Quality

- **Logging**: Use SLF4J with proper log levels
- **Documentation**: Javadoc for all public methods
- **Comments**: Avoid inline comments, prefer Javadoc
- **Exceptions**: Minimize exception throwing, use return values
- **Testing**: Unit tests for all business logic

### Architecture Principles

- **Hexagonal Architecture**: Clear separation of concerns
- **Domain-Driven Design**: Rich domain models
- **Clean Code**: Readable, maintainable code
- **SOLID Principles**: Single responsibility, dependency inversion

### CI/CD Workflow

1. **Development**: Push to `develop` → automatic staging deployment
2. **Testing**: Automated tests, security scans, build verification
3. **Production**: Manual approval → production deployment with health checks
4. **Monitoring**: GitHub Actions status, health monitoring, rollback procedures

## Related Documentation

- [Business Requirements](../fach/fachlich.md) - Domain model and business rules
- [API Specification](../../api/) - OpenAPI specification
- [CI/CD Pipeline](07-deployment/deployment.md#cicd-pipeline) - GitHub Actions workflows
