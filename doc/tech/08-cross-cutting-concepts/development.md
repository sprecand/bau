# Development Guide

## Prerequisites


- **Java 21**(OpenJDK or Oracle JDK)
-**Node.js 20+**and npm
-**Docker**and Docker Compose
-**AWS CLI**configured
-**IDE**: IntelliJ IDEA, VS Code, or Eclipse

## Quick Setup


### 1. Database


```bash
docker-compose up -d postgres

```bash

docker-compose up -d postgres

```bash

### 2. AWS Cognito Setup


```bash

### 2. AWS Cognito Setup


```bash

## Create user pool


aws cognito-idp create-user-pool \
--pool-name bau-dev-users \
--policies '{"PasswordPolicy":{"MinimumLength":8,"RequireUppercase":true,"RequireLowercase":true,"RequireNumbers":true,"RequireSymbols":true}}' \
--auto-verified-attributes email

## Create user pool client


aws cognito-idp create-user-pool-client \
--user-pool-id YOUR_USER_POOL_ID \
--client-name bau-dev-client \
--no-generate-secret \
--explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH

```bash

## Create user pool


aws cognito-idp create-user-pool \
--pool-name bau-dev-users \
--policies '{"PasswordPolicy":{"MinimumLength":8,"RequireUppercase":true,"RequireLowercase":true,"RequireNumbers":true,"RequireSymbols":true}}' \
--auto-verified-attributes email

## Create user pool client


aws cognito-idp create-user-pool-client \
--user-pool-id YOUR_USER_POOL_ID \
--client-name bau-dev-client \
--no-generate-secret \
--explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH

```bash

### 3. Environment Variables


Copy the environment template and customize:

```bash

### 3. Environment Variables


Copy the environment template and customize:

```bash

cp env.template .env

## Edit .env with your values


```bash

cp env.template .env

## Edit .env with your values


```bash

Example `.env` configuration:

```env

Example `.env` configuration:

```env

## Database (matches docker-compose.yml)


SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bau_dev
SPRING_DATASOURCE_USERNAME=bau_user
SPRING_DATASOURCE_PASSWORD=bau_password

## AWS Cognito


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

## Frontend


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com

```bash

## Database (matches docker-compose.yml)


SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bau_dev
SPRING_DATASOURCE_USERNAME=bau_user
SPRING_DATASOURCE_PASSWORD=bau_password

## AWS Cognito


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

## Frontend


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com

```bash

### 4. Start Applications


```bash

### 4. Start Applications


```bash

## Backend


cd backend
mvn spring-boot:run

## Frontend


cd frontend
npm install
npm start

```bash

## Backend


cd backend
mvn spring-boot:run

## Frontend


cd frontend
npm install
npm start

```bash

## Project Structure


```bash

## Project Structure


```bash

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

```bash

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

```bash

## Development Workflow


### 1. Feature Development


```bash

## Development Workflow


### 1. Feature Development


```bash

git checkout -b feature/new-feature

## Make changes


git commit -m "feat: add new feature"
git push origin feature/new-feature

```bash

git checkout -b feature/new-feature

## Make changes


git commit -m "feat: add new feature"
git push origin feature/new-feature

```bash

### 2. Testing


```bash

### 2. Testing


```bash

## Backend tests


mvn test

## Frontend tests


npm test

## Integration tests


mvn verify

```bash

## Backend tests


mvn test

## Frontend tests


npm test

## Integration tests


mvn verify

```bash

### 3. Code Quality


```bash

### 3. Code Quality


```bash

## Backend


mvn spotless:apply
mvn checkstyle:check

## Frontend


npm run lint
npm run format

```bash

## Backend


mvn spotless:apply
mvn checkstyle:check

## Frontend


npm run lint
npm run format

```bash

## Key Commands


### Backend


```bash

## Key Commands


### Backend


```bash

## Run application


mvn spring-boot:run

## Run tests


mvn test

## Build


mvn clean package

## Database migration


mvn flyway:migrate

## Generate OpenAPI client


mvn openapi-generator:generate

```bash

## Run application


mvn spring-boot:run

## Run tests


mvn test

## Build


mvn clean package

## Database migration


mvn flyway:migrate

## Generate OpenAPI client


mvn openapi-generator:generate

```bash

### Frontend


```bash

### Frontend


```bash

## Install dependencies


npm install

## Start development server


npm start

## Build for production


npm run build

## Run tests


npm test

## Lint code


npm run lint

```bash

## Install dependencies


npm install

## Start development server


npm start

## Build for production


npm run build

## Run tests


npm test

## Lint code


npm run lint

```bash

## API Development


### Adding New Endpoints


1. **Update OpenAPI spec**(`api/schemas/` and `api/paths/`)
2.**Generate DTOs**(if using code generation)
3.**Create controller**in `adapter/in/web/`
4.**Create use case**in `application/usecase/`
5.**Create repository**in `adapter/out/persistence/`
6.**Add mappers**in respective adapter folders
7.**Write tests**### Example: Adding Bedarf Endpoint

```java

## API Development


### Adding New Endpoints


1.**Update OpenAPI spec**(`api/schemas/` and `api/paths/`)
2.**Generate DTOs**(if using code generation)
3.**Create controller**in `adapter/in/web/`
4.**Create use case**in `application/usecase/`
5.**Create repository**in `adapter/out/persistence/`
6.**Add mappers**in respective adapter folders
7.**Write tests**

### Example: Adding Bedarf Endpoint


```java
// 1. API Controller (implements generated interface)
@RestController
@RequiredArgsConstructor
@Slf4j
public class BedarfApiController implements BedarfApi {

```bash

private final BedarfUseCase bedarfUseCase;
private final BedarfWebMapper bedarfWebMapper;

```bash

```bash

@Override
public ResponseEntity<BedarfResponseDto> createBedarf(BedarfCreateRequestDto request) {
    log.debug("Creating bedarf: {}", request);

```bash

```bash

Optional<Bedarf> bedarfOpt = bedarfUseCase.createBedarf(request);

```bash

```bash

if (bedarfOpt.isEmpty()) {
    log.warn("Bedarf creation failed");
    return ResponseEntity.badRequest().build();
}

```bash

```bash

BedarfResponseDto response = bedarfWebMapper.toResponseDto(bedarfOpt.get());
log.info("Successfully created bedarf with id: {}", bedarfOpt.get().getId());

```bash

```bash

return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

```bash

```bash

@Override
public ResponseEntity<List<BedarfResponseDto>> getAllBedarfe(/*pagination params*/) {
    List<Bedarf> bedarfe = bedarfUseCase.getAllBedarfe();
    List<BedarfResponseDto> response = bedarfe.stream()
        .map(bedarfWebMapper::toResponseDto)
        .toList();
    return ResponseEntity.ok(response);
}

```bash

```bash

// ... other CRUD operations from BedarfApi interface

```bash

}

// 2. Use Case Service
@Service
@RequiredArgsConstructor
@Slf4j
public class BedarfService implements BedarfUseCase {

```bash

private final BedarfRepository bedarfRepository;

```bash

```bash

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

```bash

```bash

if (!bedarf.isValidDateRange()) {
    log.warn("Invalid date range for bedarf");
    return Optional.empty();
}

```bash

```bash

Bedarf savedBedarf = bedarfRepository.save(bedarf);
log.info("Successfully created bedarf with id: {}", savedBedarf.getId());

```bash

```bash

return Optional.of(savedBedarf);
}

```bash

}

// 3. Repository Interface
public interface BedarfRepository {

```bash

Bedarf save(Bedarf bedarf);
Optional<Bedarf> findById(UUID id);
List<Bedarf> findByBetriebId(UUID betriebId);
List<Bedarf> findAll();
void deleteById(UUID id);

```bash

}

```bash

// 1. API Controller (implements generated interface)
@RestController
@RequiredArgsConstructor
@Slf4j
public class BedarfApiController implements BedarfApi {

```bash

private final BedarfUseCase bedarfUseCase;
private final BedarfWebMapper bedarfWebMapper;

```bash

```bash

@Override
public ResponseEntity<BedarfResponseDto> createBedarf(BedarfCreateRequestDto request) {
    log.debug("Creating bedarf: {}", request);

```bash

```bash

Optional<Bedarf> bedarfOpt = bedarfUseCase.createBedarf(request);

```bash

```bash

if (bedarfOpt.isEmpty()) {
    log.warn("Bedarf creation failed");
    return ResponseEntity.badRequest().build();
}

```bash

```bash

BedarfResponseDto response = bedarfWebMapper.toResponseDto(bedarfOpt.get());
log.info("Successfully created bedarf with id: {}", bedarfOpt.get().getId());

```bash

```bash

return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

```bash

```bash

@Override
public ResponseEntity<List<BedarfResponseDto>> getAllBedarfe(/*pagination params*/) {
    List<Bedarf> bedarfe = bedarfUseCase.getAllBedarfe();
    List<BedarfResponseDto> response = bedarfe.stream()
        .map(bedarfWebMapper::toResponseDto)
        .toList();
    return ResponseEntity.ok(response);
}

```bash

```bash

// ... other CRUD operations from BedarfApi interface

```bash

}

// 2. Use Case Service
@Service
@RequiredArgsConstructor
@Slf4j
public class BedarfService implements BedarfUseCase {

```bash

private final BedarfRepository bedarfRepository;

```bash

```bash

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

```bash

```bash

if (!bedarf.isValidDateRange()) {
    log.warn("Invalid date range for bedarf");
    return Optional.empty();
}

```bash

```bash

Bedarf savedBedarf = bedarfRepository.save(bedarf);
log.info("Successfully created bedarf with id: {}", savedBedarf.getId());

```bash

```bash

return Optional.of(savedBedarf);
}

```bash

}

// 3. Repository Interface
public interface BedarfRepository {

```bash

Bedarf save(Bedarf bedarf);
Optional<Bedarf> findById(UUID id);
List<Bedarf> findByBetriebId(UUID betriebId);
List<Bedarf> findAll();
void deleteById(UUID id);

```bash

}

```bash

## Database


### Schema Management


- **Migrations**: Use Flyway for database migrations
- **Location**: `backend/src/main/resources/db/migration/`
- **Naming**: `V1__Create_bedarf_table.sql`

### Example Migration


```sql

## Database


### Schema Management


- **Migrations**: Use Flyway for database migrations
- **Location**: `backend/src/main/resources/db/migration/`
- **Naming**: `V1__Create_bedarf_table.sql`

### Example Migration


```sql
-- V1__Create_bedarf_table.sql
CREATE TABLE bedarf (

```bash

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

```bash

);

CREATE INDEX idx_bedarf_betrieb_id ON bedarf(betrieb_id);
CREATE INDEX idx_bedarf_status ON bedarf(status);

```bash

-- V1__Create_bedarf_table.sql
CREATE TABLE bedarf (

```bash

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

```bash

);

CREATE INDEX idx_bedarf_betrieb_id ON bedarf(betrieb_id);
CREATE INDEX idx_bedarf_status ON bedarf(status);

```bash

## Testing


### Backend Testing


```java

## Testing


### Backend Testing


```java

@SpringBootTest
class BedarfApiControllerTest {

```bash

@Autowired
private TestRestTemplate restTemplate;

```bash

```bash

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

```bash

```bash

// When
ResponseEntity<BedarfResponseDto> response = restTemplate.postForEntity(
    "/api/v1/bedarfe", request, BedarfResponseDto.class);

```bash

```bash

// Then
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
assertThat(response.getBody().getHolzbauAnzahl()).isEqualTo(2);
assertThat(response.getBody().getStatus()).isEqualTo(BedarfStatus.INACTIV);
}

```bash

}

```bash

@SpringBootTest
class BedarfApiControllerTest {

```bash

@Autowired
private TestRestTemplate restTemplate;

```bash

```bash

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

```bash

```bash

// When
ResponseEntity<BedarfResponseDto> response = restTemplate.postForEntity(
    "/api/v1/bedarfe", request, BedarfResponseDto.class);

```bash

```bash

// Then
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
assertThat(response.getBody().getHolzbauAnzahl()).isEqualTo(2);
assertThat(response.getBody().getStatus()).isEqualTo(BedarfStatus.INACTIV);
}

```bash

}

```bash

### Frontend Testing


```typescript

### Frontend Testing


```typescript

describe('BedarfService', () => {
  it('should create bedarf', async () => {

```bash

const bedarf = await bedarfService.createBedarf({
  holzbauAnzahl: 2,
  zimmermannAnzahl: 1,
  datumVon: '2024-02-01',
  datumBis: '2024-02-15',
  adresse: 'Test Address'
});

```bash

```bash

expect(bedarf.holzbauAnzahl).toBe(2);

```bash

  });
});

```bash

describe('BedarfService', () => {
  it('should create bedarf', async () => {

```bash

const bedarf = await bedarfService.createBedarf({
  holzbauAnzahl: 2,
  zimmermannAnzahl: 1,
  datumVon: '2024-02-01',
  datumBis: '2024-02-15',
  adresse: 'Test Address'
});

```bash

```bash

expect(bedarf.holzbauAnzahl).toBe(2);

```bash

  });
});

```bash

## Troubleshooting


### Common Issues


1. **Database Connection**```bash

## Troubleshooting


### Common Issues


1.**Database Connection**```bash

## Check if PostgreSQL is running


   docker ps

## Check logs


   docker logs bau-postgres

   ```

## Check if PostgreSQL is running


   docker ps

## Check logs


   docker logs bau-postgres

   ```

2.**AWS Cognito Issues**```bash

2.**AWS Cognito Issues**```bash

## Verify configuration


   aws cognito-idp describe-user-pool --user-pool-id YOUR_POOL_ID

   ```

## Verify configuration


   aws cognito-idp describe-user-pool --user-pool-id YOUR_POOL_ID

   ```

3.**Port Conflicts**```bash

3.**Port Conflicts**

   ```bash

## Check what's using port 8080


   lsof -i :8080

## Kill process


   kill -9 PID

   ```

## Check what's using port 8080


   lsof -i :8080

## Kill process


   kill -9 PID

   ```

## Related


- [Backend Architecture](../05-building-blocks/backend-architecture.md) - Architecture details
- [Authentication Flow](../06-runtime/authentication-flow.md) - AWS Cognito setup
- [Deployment Guide](../07-deployment/deployment.md) - Production deployment

## Related


- [Backend Architecture](../05-building-blocks/backend-architecture.md) - Architecture details
- [Authentication Flow](../06-runtime/authentication-flow.md) - AWS Cognito setup
- [Deployment Guide](../07-deployment/deployment.md) - Production deployment
