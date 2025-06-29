# Requirements

## Functional Requirements

### Core Features
- **User Management**: Registration, login, profile management
- **Bedarf Management**: Create, view, update, delete construction needs
- **Betrieb Management**: Company profile and management
- **Search & Matching**: Find suitable workers for construction needs
- **Communication**: Messaging between companies and workers

### User Roles
- **Construction Companies**: Post bedarf, manage profiles, browse opportunities, apply for bedarf
- **Administrators**: System management and oversight

## Non-Functional Requirements

### Performance
- **Response Time**: < 2 seconds for API calls
- **Availability**: 99% uptime
- **Scalability**: Support 100 concurrent users

### Security
- **Authentication**: AWS Cognito integration
- **Authorization**: Role-based access control
- **Data Protection**: Neues Datenschutzgesetz (revDSG) compliance

### Technology
- **Backend**: Spring Boot with Java 21
- **Frontend**: Angular 20+
- **Database**: PostgreSQL
- **Infrastructure**: AWS (ECS, RDS, Cognito)

## Constraints
- **Budget**: Cost-effective for small to medium scale
- **Team Size**: Single developer initially
- **Timeline**: MVP till end of July 2025
- **Compliance**: Swiss data protection laws

## Success Criteria
- **User Adoption**: pilot companies would pay money for product
- **System Reliability**: < 1% error rate
