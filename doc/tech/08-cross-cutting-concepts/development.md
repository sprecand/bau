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
Copy the environment template and customize:
```bash
cp env.template .env
# Edit .env with your values
```

Example `.env` configuration:
```env
# Database (matches docker-compose.yml)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bau_dev
SPRING_DATASOURCE_USERNAME=bau_user
SPRING_DATASOURCE_PASSWORD=bau_password

# AWS Cognito
AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

# Frontend
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
// 1. API Controller (implements generated interface)
@RestController
@RequiredArgsConstructor
@Slf4j
public class BedarfApiController implements BedarfApi {
    
    private final BedarfUseCase bedarfUseCase;
    private final BedarfWebMapper bedarfWebMapper;
    
    @Override
    public ResponseEntity<BedarfResponseDto> createBedarf(BedarfCreateRequestDto request) {
        log.debug("Creating bedarf: {}", request);
        
        Optional<Bedarf> bedarfOpt = bedarfUseCase.createBedarf(request);
        
        if (bedarfOpt.isEmpty()) {
            log.warn("Bedarf creation failed");
            return ResponseEntity.badRequest().build();
        }
        
        BedarfResponseDto response = bedarfWebMapper.toResponseDto(bedarfOpt.get());
        log.info("Successfully created bedarf with id: {}", bedarfOpt.get().getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<List<BedarfResponseDto>> getAllBedarfe(/* pagination params */) {
        List<Bedarf> bedarfe = bedarfUseCase.getAllBedarfe();
        List<BedarfResponseDto> response = bedarfe.stream()
            .map(bedarfWebMapper::toResponseDto)
            .toList();
        return ResponseEntity.ok(response);
    }
    
    // ... other CRUD operations from BedarfApi interface
}

// 2. Use Case Service
@Service
@RequiredArgsConstructor
@Slf4j
public class BedarfService implements BedarfUseCase {
    
    private final BedarfRepository bedarfRepository;
    
    @Override
    public Optional<Bedarf> createBedarf(BedarfCreateRequestDto request) {
        Bedarf bedarf = Bedarf.builder()
            .betriebId(request.getBetriebId())
            .holzbauAnzahl(request.getHolzbauAnzahl())
            .zimmermannAnzahl(request.getZimmermannAnzahl())
            .datumVon(request.getDatumVon())
            .datumBis(request.getDatumBis())
            .adresse(request.getAdresse())
            .mitWerkzeug(request.getMitWerkzeug())
            .mitFahrzeug(request.getMitFahrzeug())
            .status(BedarfStatus.INACTIV)
            .build();
        
        if (!bedarf.isValidDateRange()) {
            log.warn("Invalid date range for bedarf");
            return Optional.empty();
        }
        
        Bedarf savedBedarf = bedarfRepository.save(bedarf);
        log.info("Successfully created bedarf with id: {}", savedBedarf.getId());
        
        return Optional.of(savedBedarf);
    }
}

// 3. Repository Interface
public interface BedarfRepository {
    Bedarf save(Bedarf bedarf);
    Optional<Bedarf> findById(UUID id);
    List<Bedarf> findByBetriebId(UUID betriebId);
    List<Bedarf> findAll();
    void deleteById(UUID id);
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
class BedarfApiControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateBedarf() {
        // Given
        BedarfCreateRequestDto request = BedarfCreateRequestDto.builder()
            .betriebId(UUID.randomUUID())
            .holzbauAnzahl(2)
            .zimmermannAnzahl(1)
            .datumVon(LocalDate.now().plusDays(1))
            .datumBis(LocalDate.now().plusDays(15))
            .adresse("Dorfstrasse 1234, 9472 Grabs")
            .mitWerkzeug(true)
            .mitFahrzeug(false)
            .build();
        
        // When
        ResponseEntity<BedarfResponseDto> response = restTemplate.postForEntity(
            "/api/v1/bedarfe", request, BedarfResponseDto.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getHolzbauAnzahl()).isEqualTo(2);
        assertThat(response.getBody().getStatus()).isEqualTo(BedarfStatus.INACTIV);
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