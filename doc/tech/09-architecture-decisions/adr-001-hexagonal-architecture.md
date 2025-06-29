# ADR-001: Hexagonal Architecture

## Status

Accepted

## Context

Need to choose an architecture pattern for the Bau platform that provides:
- Clear separation of concerns
- Testability
- Maintainability
- Technology independence

## Decision

Use **Hexagonal Architecture**(Ports & Adapters) with the following package structure:

```
backend/src/main/java/com/bau/
├── adapter/                  # All adapters
│   ├── in/                   # Driving adapters (controllers, events)
│   └── out/                  # Driven adapters (repositories, external services)
├── application/              # Application layer
│   ├── domain/               # Domain entities and business logic
│   ├── usecase/              # Use case implementations
│   └── port/                 # Port interfaces
└── shared/                   # Shared utilities and configuration
```

backend/src/main/java/com/bau/
├── adapter/                  # All adapters
│   ├── in/                   # Driving adapters (controllers, events)
│   └── out/                  # Driven adapters (repositories, external services)
├── application/              # Application layer
│   ├── domain/               # Domain entities and business logic
│   ├── usecase/              # Use case implementations
│   └── port/                 # Port interfaces
└── shared/                   # Shared utilities and configuration

```

## Consequences

### Positive

-**Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Flexibility**: Easy to change implementations
- **Technology Independence**: Domain logic is framework-agnostic

### Negative

- **Complexity**: More initial setup required
- **Learning Curve**: Team needs to understand the pattern
- **Overhead**: More files and interfaces for simple features

## Related

- [Backend Architecture](../05-building-blocks/backend-architecture.md) - Detailed implementation
- [Naming Conventions](../naming-conventions.md) - Package and class naming

## Consequences

### Positive

- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear separation of concerns
- **Flexibility**: Easy to change implementations
- **Technology Independence**: Domain logic is framework-agnostic

### Negative

- **Complexity**: More initial setup required
- **Learning Curve**: Team needs to understand the pattern
- **Overhead**: More files and interfaces for simple features

## Related

- [Backend Architecture](../05-building-blocks/backend-architecture.md) - Detailed implementation
- [Naming Conventions](../naming-conventions.md) - Package and class naming