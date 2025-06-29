# ADR-003: Entity & DTO Separation

## Status

Accepted

## Context

Need to define clear separation between different object types in the application to maintain clean architecture and avoid tight coupling between layers.

## Decision

Use three distinct object types with clear responsibilities:

- **DTOs**: API contracts (defined in OpenAPI spec)
- **Business Objects**: Domain logic and business rules
- **JPA Entities**: Database representation

## Consequences

### Positive

- **Loose Coupling**: Changes in one layer don't affect others
- **Testability**: Each layer can be tested independently
- **Flexibility**: Easy to change API contracts or database schema
- **Maintainability**: Clear separation of concerns

### Negative

- **More Code**: Additional mapper classes required
- **Complexity**: More files and interfaces to maintain
- **Learning Curve**: Team needs to understand the separation

## Implementation

### Object Types

#### DTOs (Data Transfer Objects)

- **Location**: `adapter/in/web/dto/`
- **Purpose**: API contracts, input/output validation
- **Source**: Defined in `api/schemas/` (OpenAPI spec)
- **Example**: `CreateBedarfRequest`, `BedarfResponse`

#### Business Objects

- **Location**: `application/domain/`
- **Purpose**: Business logic, domain rules, core entities
- **Source**: Domain modeling
- **Example**: `Bedarf`, `Betrieb`, `User`

#### JPA Entities

- **Location**: `adapter/out/persistence/entity/`
- **Purpose**: Database mapping, persistence concerns
- **Source**: Database schema
- **Example**: `BedarfEntity`, `BetriebEntity`

### Mapping Flow

```

HTTP Request → DTO → Mapper → Business Object → Mapper → Entity → Database
Database → Entity → Mapper → Business Object → Mapper → DTO → HTTP Response

```

HTTP Request → DTO → Mapper → Business Object → Mapper → Entity → Database
Database → Entity → Mapper → Business Object → Mapper → DTO → HTTP Response

```

### Example Implementation

```java

### Example Implementation

```java

// DTO (API Contract)
public class CreateBedarfRequest {

```

private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;

```

}

// Business Object (Domain Logic)
public class Bedarf {

```

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

```

```

// Business logic methods
public boolean isValidDateRange() {
    return datumVon.isBefore(datumBis);
}

```

```

public boolean requiresTools() {
    return mitWerkzeug && (holzbauAnzahl > 0 || zimmermannAnzahl > 0);
}

```

}

// JPA Entity (Database)
@Entity
@Table(name = "bedarf")
public class BedarfEntity {

```

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

```

```

@Column(name = "betrieb_id")
private Long betriebId;

```

```

@Column(name = "holzbau_anzahl")
private Integer holzbauAnzahl;

```

```

@Column(name = "zimmermann_anzahl")
private Integer zimmermannAnzahl;

```

```

@Column(name = "datum_von")
private LocalDate datumVon;

```

```

@Column(name = "datum_bis")
private LocalDate datumBis;

```

```

@Column(name = "adresse")
private String adresse;

```

```

@Column(name = "mit_werkzeug")
private Boolean mitWerkzeug;

```

```

@Column(name = "mit_fahrzeug")
private Boolean mitFahrzeug;

```

```

@Enumerated(EnumType.STRING)
@Column(name = "status")
private BedarfStatus status;

```

```

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "betrieb_id", insertable = false, updatable = false)
private BetriebEntity betrieb;

```

}

```

// DTO (API Contract)
public class CreateBedarfRequest {

```

private Integer holzbauAnzahl;
private Integer zimmermannAnzahl;
private LocalDate datumVon;
private LocalDate datumBis;
private String adresse;
private Boolean mitWerkzeug;
private Boolean mitFahrzeug;

```

}

// Business Object (Domain Logic)
public class Bedarf {

```

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

```

```

// Business logic methods
public boolean isValidDateRange() {
    return datumVon.isBefore(datumBis);
}

```

```

public boolean requiresTools() {
    return mitWerkzeug && (holzbauAnzahl > 0 || zimmermannAnzahl > 0);
}

```

}

// JPA Entity (Database)
@Entity
@Table(name = "bedarf")
public class BedarfEntity {

```

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

```

```

@Column(name = "betrieb_id")
private Long betriebId;

```

```

@Column(name = "holzbau_anzahl")
private Integer holzbauAnzahl;

```

```

@Column(name = "zimmermann_anzahl")
private Integer zimmermannAnzahl;

```

```

@Column(name = "datum_von")
private LocalDate datumVon;

```

```

@Column(name = "datum_bis")
private LocalDate datumBis;

```

```

@Column(name = "adresse")
private String adresse;

```

```

@Column(name = "mit_werkzeug")
private Boolean mitWerkzeug;

```

```

@Column(name = "mit_fahrzeug")
private Boolean mitFahrzeug;

```

```

@Enumerated(EnumType.STRING)
@Column(name = "status")
private BedarfStatus status;

```

```

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "betrieb_id", insertable = false, updatable = false)
private BetriebEntity betrieb;

```

}

```

## Related

- [ADR-001: Hexagonal Architecture](adr-001-hexagonal-architecture.md) - Overall architecture
- [ADR-004: Naming Conventions](adr-004-naming-conventions.md) - Naming guidelines

## Related

- [ADR-001: Hexagonal Architecture](adr-001-hexagonal-architecture.md) - Overall architecture
- [ADR-004: Naming Conventions](adr-004-naming-conventions.md) - Naming guidelines
