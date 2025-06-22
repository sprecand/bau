# Development Guide

## Prerequisites

- **Java 21** (OpenJDK or Oracle JDK)
- **Node.js 20+** and npm
- **Docker** and Docker Compose
- **AWS CLI** configured
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse

## Quick Setup

### 1. Database
```bash
docker-compose up -d postgres
```

### 2. AWS Cognito Setup
```bash
# Create user pool
aws cognito-idp create-user-pool \
  --pool-name bau-dev-users \
  --policies '{"PasswordPolicy":{"MinimumLength":8,"RequireUppercase":true,"RequireLowercase":true,"RequireNumbers":true,"RequireSymbols":true}}' \
  --auto-verified-attributes email

# Create user pool client
aws cognito-idp create-user-pool-client \
  --user-pool-id YOUR_USER_POOL_ID \
  --client-name bau-dev-client \
  --no-generate-secret \
  --explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH
```

### 3. Environment Variables
```env
# Backend (.env)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bau_dev
SPRING_DATASOURCE_USERNAME=bau_user
SPRING_DATASOURCE_PASSWORD=bau_password
AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

# Frontend (.env)
AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com
```

### 4. Start Applications
```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm start
```

## Project Structure

```
backend/src/main/java/com/bau/
├── adapter/
│   ├── in/                # Driving adapters
│   │   ├── web/          # REST controllers
│   │   ├── cli/          # Command line interface
│   │   └── event/        # Event listeners
│   └── out/              # Driven adapters
│       ├── persistence/  # Database repositories
│       ├── external/     # External API clients
│       └── messaging/    # Message brokers
├── application/
│   ├── domain/          # Domain entities and business logic
│   ├── usecase/         # Use case implementations
│   └── port/            # Port interfaces
└── shared/              # Shared utilities and configuration
```

## Development Workflow

### 1. Feature Development
```bash
git checkout -b feature/new-feature
# Make changes
git commit -m "feat: add new feature"
git push origin feature/new-feature
```

### 2. Testing
```bash
# Backend tests
mvn test

# Frontend tests
npm test

# Integration tests
mvn verify
```

### 3. Code Quality
```bash
# Backend
mvn spotless:apply
mvn checkstyle:check

# Frontend
npm run lint
npm run format
```

## Key Commands

### Backend
```bash
# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build
mvn clean package

# Database migration
mvn flyway:migrate

# Generate OpenAPI client
mvn openapi-generator:generate
```

### Frontend
```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Lint code
npm run lint
```

## API Development

### Adding New Endpoints

1. **Update OpenAPI spec** (`api/schemas/` and `api/paths/`)
2. **Generate DTOs** (if using code generation)
3. **Create controller** in `adapter/in/web/`
4. **Create use case** in `application/usecase/`
5. **Create repository** in `adapter/out/persistence/`
6. **Add mappers** in respective adapter folders
7. **Write tests**

### Example: Adding Bedarf Endpoint

```java
// 1. Controller
@RestController
@RequestMapping("/api/v1/bedarf")
public class BedarfController {
    
    @PostMapping
    public ResponseEntity<BedarfResponse> createBedarf(@RequestBody CreateBedarfRequest request) {
        Optional<Bedarf> bedarfOpt = createBedarfUseCase.execute(request);
        
        if (bedarfOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        BedarfResponse response = bedarfMapper.toResponse(bedarfOpt.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// 2. Use Case
@Service
public class CreateBedarfUseCase {
    
    public Optional<Bedarf> execute(CreateBedarfRequest request) {
        Bedarf bedarf = bedarfMapper.toDomain(request);
        
        if (!bedarf.isValidDateRange()) {
            return Optional.empty();
        }
        
        Bedarf savedBedarf = bedarfRepository.save(bedarf);
        return Optional.of(savedBedarf);
    }
}

// 3. Repository
@Repository
public interface BedarfRepository extends JpaRepository<BedarfEntity, Long> {
    List<BedarfEntity> findByBetriebId(Long betriebId);
}
```

## Database

### Schema Management
- **Migrations**: Use Flyway for database migrations
- **Location**: `backend/src/main/resources/db/migration/`
- **Naming**: `V1__Create_bedarf_table.sql`

### Example Migration
```sql
-- V1__Create_bedarf_table.sql
CREATE TABLE bedarf (
    id BIGSERIAL PRIMARY KEY,
    betrieb_id BIGINT NOT NULL,
    holzbau_anzahl INTEGER NOT NULL,
    zimmermann_anzahl INTEGER NOT NULL,
    datum_von DATE NOT NULL,
    datum_bis DATE NOT NULL,
    adresse VARCHAR(500) NOT NULL,
    mit_werkzeug BOOLEAN DEFAULT false,
    mit_fahrzeug BOOLEAN DEFAULT false,
    status VARCHAR(20) DEFAULT 'INACTIV',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bedarf_betrieb_id ON bedarf(betrieb_id);
CREATE INDEX idx_bedarf_status ON bedarf(status);
```

## Testing

### Backend Testing
```java
@SpringBootTest
class BedarfControllerTest {
    
    @Test
    void shouldCreateBedarf() {
        // Given
        CreateBedarfRequest request = new CreateBedarfRequest();
        request.setHolzbauAnzahl(2);
        request.setZimmermannAnzahl(1);
        request.setDatumVon(LocalDate.now().plusDays(1));
        request.setDatumBis(LocalDate.now().plusDays(15));
        request.setAdresse("Test Address");
        
        // When
        ResponseEntity<BedarfResponse> response = restTemplate.postForEntity(
            "/api/v1/bedarf", request, BedarfResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getHolzbauAnzahl()).isEqualTo(2);
    }
}
```

### Frontend Testing
```typescript
describe('BedarfService', () => {
  it('should create bedarf', async () => {
    const bedarf = await bedarfService.createBedarf({
      holzbauAnzahl: 2,
      zimmermannAnzahl: 1,
      datumVon: '2024-02-01',
      datumBis: '2024-02-15',
      adresse: 'Test Address'
    });
    
    expect(bedarf.holzbauAnzahl).toBe(2);
  });
});
```

## Troubleshooting

### Common Issues

1. **Database Connection**
   ```bash
   # Check if PostgreSQL is running
   docker ps
   
   # Check logs
   docker logs bau-postgres
   ```

2. **AWS Cognito Issues**
   ```bash
   # Verify configuration
   aws cognito-idp describe-user-pool --user-pool-id YOUR_POOL_ID
   ```

3. **Port Conflicts**
   ```bash
   # Check what's using port 8080
   lsof -i :8080
   
   # Kill process
   kill -9 PID
   ```

## Related
- [Backend Architecture](../05-building-blocks/backend-architecture.md) - Architecture details
- [Authentication Flow](../06-runtime/authentication-flow.md) - AWS Cognito setup
- [Deployment Guide](../07-deployment/deployment.md) - Production deployment 