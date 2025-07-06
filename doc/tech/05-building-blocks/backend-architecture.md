# Backend Architecture

## Overview


Spring Boot application using **Hexagonal Architecture**(Ports & Adapters) with Java 21.

## Architecture Layers


```mermaid
graph TB

```bash

subgraph "In Adapters"
    A[REST API]
    B[Events]
end

```bash

```bash

subgraph "Application Layer"
    D[Domain Entities]
    E[Use Cases]
    F[Ports]
end

```bash

```bash

subgraph "Out Adapters"
    G[Database]
    H[External APIs]
    I[Messaging]
end

```bash

```bash

A --> F
B --> F
F --> E
F --> D
E --> D
E --> F
F --> G
F --> H
F --> I

```bash

```bash

graph TB

```bash

subgraph "In Adapters"
    A[REST API]
    B[Events]
end

```bash

```bash

subgraph "Application Layer"
    D[Domain Entities]
    E[Use Cases]
    F[Ports]
end

```bash

```bash

subgraph "Out Adapters"
    G[Database]
    H[External APIs]
    I[Messaging]
end

```bash

```bash

A --> F
B --> F
F --> E
F --> D
E --> D
E --> F
F --> G
F --> H
F --> I

```bash

```bash

## Package Structure


```bash

## Package Structure


```bash

backend/src/main/java/com/bau/
├── adapter/                 # All adapters
│   ├── in/                  # Driving adapters (controllers, events)
│   │   ├── web/             # REST controllers
│   │   └── event/           # Event listeners
│   └── out/                 # Driven adapters (repositories, external services)
│       ├── persistence/     # Database repositories
│       ├── web/            # External API clients
│       └── messaging/       # Message brokers
├── application/             # Application layer
│   ├── domain/              # Domain entities and business logic
│   ├── usecase/             # Use case implementations
│   └── port/                # Port interfaces
│       ├── in/              # Input ports (use case interfaces)
│       └── out/             # Output ports (repository interfaces)
└── shared/                  # Shared utilities and configuration

```bash

├── config/              # Configuration classes
├── util/                # Utility classes
└── exception/           # Custom exceptions

```bash

```bash

backend/src/main/java/com/bau/
├── adapter/                 # All adapters
│   ├── in/                  # Driving adapters (controllers, events)
│   │   ├── web/             # REST controllers
│   │   └── event/           # Event listeners
│   └── out/                 # Driven adapters (repositories, external services)
│       ├── persistence/     # Database repositories
│       ├── web/            # External API clients
│       └── messaging/       # Message brokers
├── application/             # Application layer
│   ├── domain/              # Domain entities and business logic
│   ├── usecase/             # Use case implementations
│   └── port/                # Port interfaces
│       ├── in/              # Input ports (use case interfaces)
│       └── out/             # Output ports (repository interfaces)
└── shared/                  # Shared utilities and configuration

```bash

├── config/              # Configuration classes
├── util/                # Utility classes
└── exception/           # Custom exceptions

```bash

```bash

## Key Components


### 1. Domain Layer (`application/domain/`)


-**Purpose**: Business logic and domain rules
- **Contains**: Entities, value objects, domain services
- **Dependencies**: None (pure business logic)

```java

## Key Components


### 1. Domain Layer (`application/domain/`)


- **Purpose**: Business logic and domain rules
- **Contains**: Entities, value objects, domain services
- **Dependencies**: None (pure business logic)

```java

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = false)
public class Bedarf {

```bash

private UUID id;
private UUID betriebId;
private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;
private BedarfStatus status;

```bash

```bash

/**
 *Validates if the date range is valid (end date after start date).*/
public boolean isValidDateRange() {
    return datumVon != null && datumBis != null && datumBis.isAfter(datumVon);
}

```bash

```bash

/**
 *Checks if tools are required for this bedarf.*/
public boolean requiresTools() {
    return Boolean.TRUE.equals(mitWerkzeug) && (holzbauAnzahl > 0 || zimmermannAnzahl > 0);
}

```bash

```bash

/**
 *Gets the total number of workers needed.*/
public int getTotalWorkers() {
    return (holzbauAnzahl != null ? holzbauAnzahl : 0) +
           (zimmermannAnzahl != null ? zimmermannAnzahl : 0);
}

```bash

}

```bash

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = false)
public class Bedarf {

```bash

private UUID id;
private UUID betriebId;
private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;
private BedarfStatus status;

```bash

```bash

/**
 *Validates if the date range is valid (end date after start date).*/
public boolean isValidDateRange() {
    return datumVon != null && datumBis != null && datumBis.isAfter(datumVon);
}

```bash

```bash

/**
 *Checks if tools are required for this bedarf.*/
public boolean requiresTools() {
    return Boolean.TRUE.equals(mitWerkzeug) && (holzbauAnzahl > 0 || zimmermannAnzahl > 0);
}

```bash

```bash

/**
 *Gets the total number of workers needed.*/
public int getTotalWorkers() {
    return (holzbauAnzahl != null ? holzbauAnzahl : 0) +
           (zimmermannAnzahl != null ? zimmermannAnzahl : 0);
}

```bash

}

```bash

### 2. Use Cases (`application/usecase/`)


- **Purpose**: Application business logic
- **Contains**: Use case implementations
- **Dependencies**: Domain entities, input/output ports

```java

### 2. Use Cases (`application/usecase/`)


- **Purpose**: Application business logic
- **Contains**: Use case implementations
- **Dependencies**: Domain entities, input/output ports

```java

@Service
@Slf4j
public class CreateBedarfUseCase {

```bash

private static final Logger log = LoggerFactory.getLogger(CreateBedarfUseCase.class);

```bash

```bash

private final BedarfRepository bedarfRepository;
private final BedarfMapper bedarfMapper;

```bash

```bash

public Optional<Bedarf> execute(CreateBedarfRequest request) {
    Bedarf bedarf = bedarfMapper.toDomain(request);

```bash

```bash

if (!bedarf.isValidDateRange()) {
    log.info("End date must be after start date {}", request.getId());
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

```bash

@Service
@Slf4j
public class CreateBedarfUseCase {

```bash

private static final Logger log = LoggerFactory.getLogger(CreateBedarfUseCase.class);

```bash

```bash

private final BedarfRepository bedarfRepository;
private final BedarfMapper bedarfMapper;

```bash

```bash

public Optional<Bedarf> execute(CreateBedarfRequest request) {
    Bedarf bedarf = bedarfMapper.toDomain(request);

```bash

```bash

if (!bedarf.isValidDateRange()) {
    log.info("End date must be after start date {}", request.getId());
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

```bash

### 3. Ports (`application/port/`)


- **Purpose**: Interface definitions
- **Contains**: Input ports (use case interfaces), output ports (repository interfaces)

```java

### 3. Ports (`application/port/`)


- **Purpose**: Interface definitions
- **Contains**: Input ports (use case interfaces), output ports (repository interfaces)

```java

public interface CreateBedarfUseCase {

```bash

Bedarf execute(CreateBedarfRequest request);

```bash

}

public interface BedarfRepository {

```bash

Bedarf save(Bedarf bedarf);
Optional<Bedarf> findById(Long id);
List<Bedarf> findByBetriebId(Long betriebId);

```bash

}

```bash

public interface CreateBedarfUseCase {

```bash

Bedarf execute(CreateBedarfRequest request);

```bash

}

public interface BedarfRepository {

```bash

Bedarf save(Bedarf bedarf);
Optional<Bedarf> findById(Long id);
List<Bedarf> findByBetriebId(Long betriebId);

```bash

}

```bash

### 4. Driving Adapters (`adapter/in/`)


- **Purpose**: Handle external requests
- **Contains**: REST controllers, CLI commands, event listeners

```java

### 4. Driving Adapters (`adapter/in/`)


- **Purpose**: Handle external requests
- **Contains**: REST controllers, CLI commands, event listeners

```java

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
public ResponseEntity<BedarfResponseDto> createBedarf(BedarfCreateRequestDto bedarfCreateRequestDto) {
    log.debug("Creating bedarf: {}", bedarfCreateRequestDto);

```bash

```bash

Optional<Bedarf> bedarfOpt = bedarfUseCase.createBedarf(bedarfCreateRequestDto);

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

// ... other CRUD operations

```bash

}

```bash

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
public ResponseEntity<BedarfResponseDto> createBedarf(BedarfCreateRequestDto bedarfCreateRequestDto) {
    log.debug("Creating bedarf: {}", bedarfCreateRequestDto);

```bash

```bash

Optional<Bedarf> bedarfOpt = bedarfUseCase.createBedarf(bedarfCreateRequestDto);

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

// ... other CRUD operations

```bash

}

```bash

### 5. Driven Adapters (`adapter/out/`)


- **Purpose**: External system integration
- **Contains**: Database repositories, external API clients

```java

### 5. Driven Adapters (`adapter/out/`)


- **Purpose**: External system integration
- **Contains**: Database repositories, external API clients

```java

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaBedarfRepository implements BedarfRepository {

```bash

private static final Logger log = LoggerFactory.getLogger(JpaBedarfRepository.class);

```bash

```bash

private final BedarfJpaRepository jpaRepository;
private final BedarfEntityMapper mapper;

```bash

```bash

@Override
public Bedarf save(Bedarf bedarf) {
    log.debug("Saving bedarf entity for betrieb: {}", bedarf.getBetriebId());

```bash

```bash

BedarfEntity entity = mapper.toEntity(bedarf);
BedarfEntity saved = jpaRepository.save(entity);
Bedarf result = mapper.toDomain(saved);

```bash

```bash

log.debug("Successfully saved bedarf with id: {}", result.getId());
return result;
}

```bash

```bash

@Override
public Optional<Bedarf> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
}

```bash

```bash

@Override
public List<Bedarf> findByBetriebId(UUID betriebId) {
    return jpaRepository.findByBetriebId(betriebId)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
}

```bash

}

```bash

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaBedarfRepository implements BedarfRepository {

```bash

private static final Logger log = LoggerFactory.getLogger(JpaBedarfRepository.class);

```bash

```bash

private final BedarfJpaRepository jpaRepository;
private final BedarfEntityMapper mapper;

```bash

```bash

@Override
public Bedarf save(Bedarf bedarf) {
    log.debug("Saving bedarf entity for betrieb: {}", bedarf.getBetriebId());

```bash

```bash

BedarfEntity entity = mapper.toEntity(bedarf);
BedarfEntity saved = jpaRepository.save(entity);
Bedarf result = mapper.toDomain(saved);

```bash

```bash

log.debug("Successfully saved bedarf with id: {}", result.getId());
return result;
}

```bash

```bash

@Override
public Optional<Bedarf> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
}

```bash

```bash

@Override
public List<Bedarf> findByBetriebId(UUID betriebId) {
    return jpaRepository.findByBetriebId(betriebId)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
}

```bash

}

```bash

## Data Flow


```bash

## Data Flow


```bash

HTTP Request → Controller → Use Case → Domain → Repository → Database
Database → Repository → Domain → Use Case → Controller → HTTP Response

```bash

HTTP Request → Controller → Use Case → Domain → Repository → Database
Database → Repository → Domain → Use Case → Controller → HTTP Response

```bash

## Benefits


- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Flexibility**: Easy to change implementations
- **Scalability**: Well-defined boundaries

## Related


- [Entity & DTO Separation](../09-architecture-decisions/adr-003-entity-dto-separation.md) - Object types
- [Naming Conventions](../09-architecture-decisions/adr-004-naming-conventions.md) - Naming guidelines
- [Development Guide](../development.md) - Setup and development

## Benefits


- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Flexibility**: Easy to change implementations
- **Scalability**: Well-defined boundaries

## Related


- [Entity & DTO Separation](../09-architecture-decisions/adr-003-entity-dto-separation.md) - Object types
- [Naming Conventions](../09-architecture-decisions/adr-004-naming-conventions.md) - Naming guidelines
- [Development Guide](../development.md) - Setup and development
