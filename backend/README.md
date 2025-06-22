# Bau Backend

Spring Boot 3.2 application for the Bau construction worker matching platform.

## Technology Stack

- **Java 21** with Spring Boot 3.2
- **PostgreSQL** (production) / **H2** (local development)
- **Spring Security** with AWS Cognito JWT authentication
- **Spring Data JPA** with Flyway migrations
- **Hexagonal Architecture** with clean separation of concerns
- **Lombok** for reducing boilerplate code
- **UUID** for entity IDs
- **Swagger/OpenAPI** for API documentation

## Architecture

The application follows Hexagonal Architecture principles:

```
com.bau/
├── adapter/
│   ├── in/                    # Inbound adapters (REST controllers)
│   └── out/                   # Outbound adapters (repositories, external services)
├── application/
│   ├── domain/                # Domain entities and business logic
│   ├── port/
│   │   ├── in/               # Inbound ports (use cases)
│   │   └── out/              # Outbound ports (repositories)
│   └── usecase/              # Use case implementations
└── shared/                   # Shared configuration and utilities
```

## Local Development

### Prerequisites

- Java 21
- Maven 3.8+

### Running the Application

The application uses different profiles for different environments:

#### Local Development (No Authentication)

For local development, use the `local` profile which disables authentication and uses an H2 in-memory database:

```bash
# Option 1: Using Maven with profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Option 2: Using environment variable
export SPRING_PROFILES_ACTIVE=local
mvn spring-boot:run
```

#### Production (With AWS Cognito Authentication)

For production, use the default profile with AWS Cognito:

```bash
# Set required environment variables
export AWS_COGNITO_JWK_SET_URI="https://cognito-idp.<region>.amazonaws.com/<userPoolId>/.well-known/jwks.json"

# Run the application
mvn spring-boot:run
```

### Access Points

When running locally:

- **Application:** http://localhost:8080/api/v1
- **H2 Database Console:** http://localhost:8080/api/v1/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)
- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/api/v1/v3/api-docs

### Local Profile Features

The `local` profile provides:

- **No Authentication:** All endpoints are accessible without JWT tokens (security is disabled)
- **H2 In-Memory Database:** No external database required
- **H2 Console:** Web-based database browser
- **Debug Logging:** Enhanced logging for development
- **Auto Schema Creation:** Database schema created automatically

## API Endpoints

### Bedarf (Construction Demand)

- `GET /api/v1/bedarfs` - List all bedarfs with pagination
- `GET /api/v1/bedarfs/{id}` - Get bedarf by ID
- `POST /api/v1/bedarfs` - Create new bedarf (requires USER role)
- `PUT /api/v1/bedarfs/{id}` - Update bedarf (requires USER role)
- `PATCH /api/v1/bedarfs/{id}/status` - Update bedarf status (requires USER role)
- `DELETE /api/v1/bedarfs/{id}` - Delete bedarf (requires USER role)
- `GET /api/v1/bedarfs/betrieb/{betriebId}` - Get bedarfs by betrieb

### Betrieb (Company)

- `GET /api/v1/betriebs` - List all betriebs with pagination
- `GET /api/v1/betriebs/{id}` - Get betrieb by ID
- `POST /api/v1/betriebs` - Create new betrieb (requires ADMIN role)
- `PUT /api/v1/betriebs/{id}` - Update betrieb (requires ADMIN role)
- `PATCH /api/v1/betriebs/{id}/status` - Update betrieb status (requires ADMIN role)
- `DELETE /api/v1/betriebs/{id}` - Delete betrieb (requires ADMIN role)

## Database Schema

The application uses Flyway for database migrations. Migration files are located in `src/main/resources/db/migration/`.

### Key Tables

- `bedarf` - Construction demands
- `betrieb` - Companies
- `user` - User accounts

## Security

### Local Development
- No authentication required
- All endpoints are publicly accessible

### Production
- AWS Cognito JWT authentication
- Role-based access control
- Secure endpoints with proper authorization

## Development Guidelines

- Follow the hexagonal architecture pattern
- Use UUIDs for all entity IDs
- Implement proper validation in service layer
- Use Lombok annotations to reduce boilerplate
- Write comprehensive Javadoc for public methods
- Use SLF4J for logging with `@Slf4j` annotation

## Testing

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report
```

## Building

```bash
# Build the application
mvn clean compile

# Create executable JAR
mvn clean package
```

## Documentation

- [Technical Documentation](../doc/tech/README.md)
- [Business Requirements](../doc/fach/fachlich.md)
- [API Specification](../api/README.md)
- [Coding Standards](../doc/tech/08-cross-cutting-concepts/coding-standards.md) 