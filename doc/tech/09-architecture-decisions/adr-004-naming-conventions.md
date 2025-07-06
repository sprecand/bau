# ADR-004: Naming Conventions

## Status


Accepted

## Context


Need consistent naming conventions across the codebase to improve readability and maintainability.

## Decision


Use package structure to distinguish object types rather than suffixes, except for JPA entities.

## Consequences


### Positive


- **Cleaner Names**: No suffix clutter in class names
- **Package Clarity**: Object type is clear from package location
- **Consistency**: Uniform naming across the codebase
- **Maintainability**: Easier to understand and modify

### Negative


- **Learning Curve**: Team needs to understand package structure
- **IDE Support**: Less automatic type detection without suffixes

## Implementation


### Package Structure


```bash

backend/src/main/java/com/bau/
├── adapter/
│   ├── in/web/dto/           # DTOs (no suffix)
│   └── out/persistence/entity/ # JPA Entities (Entity suffix)
├── application/
│   ├── domain/               # Business Objects (no suffix)
│   ├── usecase/              # Use Cases (no suffix)
│   └── port/
│       ├── in/               # Input Ports (no suffix)
│       └── out/              # Output Ports (no suffix)
└── shared/

```bash

├── config/               # Configuration (no suffix)
└── util/                 # Utilities (no suffix)

```bash

```bash

backend/src/main/java/com/bau/
├── adapter/
│   ├── in/web/dto/           # DTOs (no suffix)
│   └── out/persistence/entity/ # JPA Entities (Entity suffix)
├── application/
│   ├── domain/               # Business Objects (no suffix)
│   ├── usecase/              # Use Cases (no suffix)
│   └── port/
│       ├── in/               # Input Ports (no suffix)
│       └── out/              # Output Ports (no suffix)
└── shared/

```bash

├── config/               # Configuration (no suffix)
└── util/                 # Utilities (no suffix)

```bash

```bash

### Naming Rules


#### Classes


- **DTOs**: `CreateBedarfRequest`, `BedarfResponse` (no suffix)
- **Business Objects**: `Bedarf`, `Betrieb`, `User` (no suffix)
- **JPA Entities**: `BedarfEntity`, `BetriebEntity` (Entity suffix)
- **Use Cases**: `CreateBedarfUseCase`, `FindBedarfUseCase` (no suffix)
- **Ports**: `BedarfRepository`, `CreateBedarfUseCase` (no suffix)
- **Mappers**: `BedarfMapper`, `BedarfEntityMapper` (Mapper suffix)

#### Methods


- **Use Cases**: `execute()` for main business logic
- **Repositories**: `save()`, `findById()`, `findByXxx()`
- **Mappers**: `toDomain()`, `toEntity()`, `toResponse()`

#### Variables


- **Camel Case**: `bedarfId`, `betriebName`
- **Constants**: `UPPER_SNAKE_CASE`
- **Database Columns**: `snake_case`

### Example Implementation


```java

### Naming Rules


#### Classes


- **DTOs**: `CreateBedarfRequest`, `BedarfResponse` (no suffix)
- **Business Objects**: `Bedarf`, `Betrieb`, `User` (no suffix)
- **JPA Entities**: `BedarfEntity`, `BetriebEntity` (Entity suffix)
- **Use Cases**: `CreateBedarfUseCase`, `FindBedarfUseCase` (no suffix)
- **Ports**: `BedarfRepository`, `CreateBedarfUseCase` (no suffix)
- **Mappers**: `BedarfMapper`, `BedarfEntityMapper` (Mapper suffix)

#### Methods


- **Use Cases**: `execute()` for main business logic
- **Repositories**: `save()`, `findById()`, `findByXxx()`
- **Mappers**: `toDomain()`, `toEntity()`, `toResponse()`

#### Variables


- **Camel Case**: `bedarfId`, `betriebName`
- **Constants**: `UPPER_SNAKE_CASE`
- **Database Columns**: `snake_case`

### Example Implementation


```java
// DTO (adapter/in/web/dto/)
public class CreateBedarfRequest {

```bash

private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;

```bash

}

// Business Object (application/domain/)
public class Bedarf {

```bash

private Long id;
private Long betriebId;
private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;
private BedarfStatus status;

```bash

}

// JPA Entity (adapter/out/persistence/entity/)
@Entity
@Table(name = "bedarf")
public class BedarfEntity {

```bash

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

```bash

```bash

@Column(name = "betrieb_id")
private Long betriebId;

```bash

```bash

@Column(name = "holzbau_anzahl")
private Integer holzbauAnzahl;

```bash

```bash

@Column(name = "zimmermann_anzahl")
private Integer zimmermannAnzahl;

```bash

```bash

@Column(name = "datum_von")
private LocalDate datumVon;

```bash

```bash

@Column(name = "datum_bis")
private LocalDate datumBis;

```bash

```bash

@Column(name = "adresse")
private String adresse;

```bash

```bash

@Column(name = "mit_werkzeug")
private Boolean mitWerkzeug;

```bash

```bash

@Column(name = "mit_fahrzeug")
private Boolean mitFahrzeug;

```bash

```bash

@Enumerated(EnumType.STRING)
@Column(name = "status")
private BedarfStatus status;

```bash

}

// Use Case (application/usecase/)
@Service
public class CreateBedarfUseCase {

```bash

private final BedarfRepository bedarfRepository;
private final BedarfMapper bedarfMapper;

```bash

```bash

public Bedarf execute(CreateBedarfRequest request) {
    Bedarf bedarf = bedarfMapper.toDomain(request);
    return bedarfRepository.save(bedarf);
}

```bash

}

// Port (application/port/out/)
public interface BedarfRepository {

```bash

Bedarf save(Bedarf bedarf);
Optional<Bedarf> findById(Long id);
List<Bedarf> findByBetriebId(Long betriebId);

```bash

}

// Mapper (adapter/in/web/mapper/)
@Component
public class BedarfMapper {

```bash

public Bedarf toDomain(CreateBedarfRequest request) {
    Bedarf bedarf = new Bedarf();
    bedarf.setHolzbauAnzahl(request.getHolzbauAnzahl());
    bedarf.setZimmermannAnzahl(request.getZimmermannAnzahl());
    bedarf.setDatumVon(request.getDatumVon());
    bedarf.setDatumBis(request.getDatumBis());
    bedarf.setAdresse(request.getAdresse());
    bedarf.setMitWerkzeug(request.getMitWerkzeug());
    bedarf.setMitFahrzeug(request.getMitFahrzeug());
    return bedarf;
}

```bash

```bash

public BedarfResponse toResponse(Bedarf bedarf) {
    BedarfResponse response = new BedarfResponse();
    response.setId(bedarf.getId());
    response.setBetriebId(bedarf.getBetriebId());
    response.setHolzbauAnzahl(bedarf.getHolzbauAnzahl());
    response.setZimmermannAnzahl(bedarf.getZimmermannAnzahl());
    response.setDatumVon(bedarf.getDatumVon());
    response.setDatumBis(bedarf.getDatumBis());
    response.setAdresse(bedarf.getAdresse());
    response.setMitWerkzeug(bedarf.getMitWerkzeug());
    response.setMitFahrzeug(bedarf.getMitFahrzeug());
    response.setStatus(bedarf.getStatus());
    return response;
}

```bash

}

// Entity Mapper (adapter/out/persistence/mapper/)
@Component
public class BedarfEntityMapper {

```bash

public BedarfEntity toEntity(Bedarf bedarf) {
    BedarfEntity entity = new BedarfEntity();
    entity.setId(bedarf.getId());
    entity.setBetriebId(bedarf.getBetriebId());
    entity.setHolzbauAnzahl(bedarf.getHolzbauAnzahl());
    entity.setZimmermannAnzahl(bedarf.getZimmermannAnzahl());
    entity.setDatumVon(bedarf.getDatumVon());
    entity.setDatumBis(bedarf.getDatumBis());
    entity.setAdresse(bedarf.getAdresse());
    entity.setMitWerkzeug(bedarf.getMitWerkzeug());
    entity.setMitFahrzeug(bedarf.getMitFahrzeug());
    entity.setStatus(bedarf.getStatus());
    return entity;
}

```bash

```bash

public Bedarf toDomain(BedarfEntity entity) {
    Bedarf bedarf = new Bedarf();
    bedarf.setId(entity.getId());
    bedarf.setBetriebId(entity.getBetriebId());
    bedarf.setHolzbauAnzahl(entity.getHolzbauAnzahl());
    bedarf.setZimmermannAnzahl(entity.getZimmermannAnzahl());
    bedarf.setDatumVon(entity.getDatumVon());
    bedarf.setDatumBis(entity.getDatumBis());
    bedarf.setAdresse(entity.getAdresse());
    bedarf.setMitWerkzeug(entity.getMitWerkzeug());
    bedarf.setMitFahrzeug(entity.getMitFahrzeug());
    bedarf.setStatus(entity.getStatus());
    return bedarf;
}

```bash

}

```bash

// DTO (adapter/in/web/dto/)
public class CreateBedarfRequest {

```bash

private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;

```bash

}

// Business Object (application/domain/)
public class Bedarf {

```bash

private Long id;
private Long betriebId;
private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;
private BedarfStatus status;

```bash

}

// JPA Entity (adapter/out/persistence/entity/)
@Entity
@Table(name = "bedarf")
public class BedarfEntity {

```bash

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

```bash

```bash

@Column(name = "betrieb_id")
private Long betriebId;

```bash

```bash

@Column(name = "holzbau_anzahl")
private Integer holzbauAnzahl;

```bash

```bash

@Column(name = "zimmermann_anzahl")
private Integer zimmermannAnzahl;

```bash

```bash

@Column(name = "datum_von")
private LocalDate datumVon;

```bash

```bash

@Column(name = "datum_bis")
private LocalDate datumBis;

```bash

```bash

@Column(name = "adresse")
private String adresse;

```bash

```bash

@Column(name = "mit_werkzeug")
private Boolean mitWerkzeug;

```bash

```bash

@Column(name = "mit_fahrzeug")
private Boolean mitFahrzeug;

```bash

```bash

@Enumerated(EnumType.STRING)
@Column(name = "status")
private BedarfStatus status;

```bash

}

// Use Case (application/usecase/)
@Service
public class CreateBedarfUseCase {

```bash

private final BedarfRepository bedarfRepository;
private final BedarfMapper bedarfMapper;

```bash

```bash

public Bedarf execute(CreateBedarfRequest request) {
    Bedarf bedarf = bedarfMapper.toDomain(request);
    return bedarfRepository.save(bedarf);
}

```bash

}

// Port (application/port/out/)
public interface BedarfRepository {

```bash

Bedarf save(Bedarf bedarf);
Optional<Bedarf> findById(Long id);
List<Bedarf> findByBetriebId(Long betriebId);

```bash

}

// Mapper (adapter/in/web/mapper/)
@Component
public class BedarfMapper {

```bash

public Bedarf toDomain(CreateBedarfRequest request) {
    Bedarf bedarf = new Bedarf();
    bedarf.setHolzbauAnzahl(request.getHolzbauAnzahl());
    bedarf.setZimmermannAnzahl(request.getZimmermannAnzahl());
    bedarf.setDatumVon(request.getDatumVon());
    bedarf.setDatumBis(request.getDatumBis());
    bedarf.setAdresse(request.getAdresse());
    bedarf.setMitWerkzeug(request.getMitWerkzeug());
    bedarf.setMitFahrzeug(request.getMitFahrzeug());
    return bedarf;
}

```bash

```bash

public BedarfResponse toResponse(Bedarf bedarf) {
    BedarfResponse response = new BedarfResponse();
    response.setId(bedarf.getId());
    response.setBetriebId(bedarf.getBetriebId());
    response.setHolzbauAnzahl(bedarf.getHolzbauAnzahl());
    response.setZimmermannAnzahl(bedarf.getZimmermannAnzahl());
    response.setDatumVon(bedarf.getDatumVon());
    response.setDatumBis(bedarf.getDatumBis());
    response.setAdresse(bedarf.getAdresse());
    response.setMitWerkzeug(bedarf.getMitWerkzeug());
    response.setMitFahrzeug(bedarf.getMitFahrzeug());
    response.setStatus(bedarf.getStatus());
    return response;
}

```bash

}

// Entity Mapper (adapter/out/persistence/mapper/)
@Component
public class BedarfEntityMapper {

```bash

public BedarfEntity toEntity(Bedarf bedarf) {
    BedarfEntity entity = new BedarfEntity();
    entity.setId(bedarf.getId());
    entity.setBetriebId(bedarf.getBetriebId());
    entity.setHolzbauAnzahl(bedarf.getHolzbauAnzahl());
    entity.setZimmermannAnzahl(bedarf.getZimmermannAnzahl());
    entity.setDatumVon(bedarf.getDatumVon());
    entity.setDatumBis(bedarf.getDatumBis());
    entity.setAdresse(bedarf.getAdresse());
    entity.setMitWerkzeug(bedarf.getMitWerkzeug());
    entity.setMitFahrzeug(bedarf.getMitFahrzeug());
    entity.setStatus(bedarf.getStatus());
    return entity;
}

```bash

```bash

public Bedarf toDomain(BedarfEntity entity) {
    Bedarf bedarf = new Bedarf();
    bedarf.setId(entity.getId());
    bedarf.setBetriebId(entity.getBetriebId());
    bedarf.setHolzbauAnzahl(entity.getHolzbauAnzahl());
    bedarf.setZimmermannAnzahl(entity.getZimmermannAnzahl());
    bedarf.setDatumVon(entity.getDatumVon());
    bedarf.setDatumBis(entity.getDatumBis());
    bedarf.setAdresse(entity.getAdresse());
    bedarf.setMitWerkzeug(entity.getMitWerkzeug());
    bedarf.setMitFahrzeug(entity.getMitFahrzeug());
    bedarf.setStatus(entity.getStatus());
    return bedarf;
}

```bash

}

```bash

## Related


- [ADR-001: Hexagonal Architecture](adr-001-hexagonal-architecture.md) - Overall architecture
- [ADR-003: Entity & DTO Separation](adr-003-entity-dto-separation.md) - Object type separation

## Related


- [ADR-001: Hexagonal Architecture](adr-001-hexagonal-architecture.md) - Overall architecture
- [ADR-003: Entity & DTO Separation](adr-003-entity-dto-separation.md) - Object type separation
