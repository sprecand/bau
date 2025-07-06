# System Overview

## High-Level Architecture


```mermaid
graph TB

```bash

A[Angular Frontend] --> B[Spring Boot Backend]
A --> C[AWS Cognito User Pool]
B --> D[PostgreSQL Database]
B --> E[AWS Cognito Identity Pool]
B --> F[External Services]

```bash

```bash

subgraph "Frontend"
    A1[Components] --> A2[Services]
    A2 --> A3[State Management]
    A4[AWS Amplify] --> A2
end

```bash

```bash

subgraph "Backend"
    B1[Controllers] --> B2[Services]
    B2 --> B3[Repositories]
    B3 --> B4[Domain Models]
    B5[Cognito JWT Filter] --> B1
end

```bash

```bash

subgraph "Database"
    D1[Users] --> D2[Betrieb]
    D2 --> D3[Bedarf]
    D3 --> D4[Applications]
end

```bash

```bash

graph TB

```bash

A[Angular Frontend] --> B[Spring Boot Backend]
A --> C[AWS Cognito User Pool]
B --> D[PostgreSQL Database]
B --> E[AWS Cognito Identity Pool]
B --> F[External Services]

```bash

```bash

subgraph "Frontend"
    A1[Components] --> A2[Services]
    A2 --> A3[State Management]
    A4[AWS Amplify] --> A2
end

```bash

```bash

subgraph "Backend"
    B1[Controllers] --> B2[Services]
    B2 --> B3[Repositories]
    B3 --> B4[Domain Models]
    B5[Cognito JWT Filter] --> B1
end

```bash

```bash

subgraph "Database"
    D1[Users] --> D2[Betrieb]
    D2 --> D3[Bedarf]
    D3 --> D4[Applications]
end

```bash

```bash

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

## Frontend Styling Strategy


### Angular Material + Tailwind CSS


- **Angular Material**: Provides pre-built components (buttons, forms, dialogs, etc.)
- **Tailwind CSS**: Custom styling, layout, and responsive design
- **Combination Benefits**:
  - Material components for consistency and accessibility
  - Tailwind utilities for custom layouts and styling
  - Responsive design with Tailwind's utility classes
  - Custom theming with Tailwind's design system

### Example Usage


```html

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

## Frontend Styling Strategy


### Angular Material + Tailwind CSS


- **Angular Material**: Provides pre-built components (buttons, forms, dialogs, etc.)
- **Tailwind CSS**: Custom styling, layout, and responsive design
- **Combination Benefits**:
  - Material components for consistency and accessibility
  - Tailwind utilities for custom layouts and styling
  - Responsive design with Tailwind's utility classes
  - Custom theming with Tailwind's design system

### Example Usage


```html
<!-- Material component with Tailwind styling -->
<mat-card class="p-6 bg-white shadow-lg rounded-lg">
  <mat-card-header class="mb-4">

```bash

<mat-card-title class="text-2xl font-bold text-gray-800">
  Bedarf Details
</mat-card-title>

```bash

  </mat-card-header>

  <mat-card-content class="space-y-4">

```bash

<mat-form-field class="w-full">
  <mat-label>Holzbau Anzahl</mat-label>
  <input matInput type="number" class="text-lg">
</mat-form-field>

```bash

  </mat-card-content>

  <mat-card-actions class="flex justify-end space-x-2">

```bash

<button mat-button class="px-4 py-2 text-gray-600 hover:text-gray-800">
  Cancel
</button>
<button mat-raised-button color="primary" class="px-6 py-2">
  Save
</button>

```bash

  </mat-card-actions>
</mat-card>

```bash

<!-- Material component with Tailwind styling -->
<mat-card class="p-6 bg-white shadow-lg rounded-lg">
  <mat-card-header class="mb-4">

```bash

<mat-card-title class="text-2xl font-bold text-gray-800">
  Bedarf Details
</mat-card-title>

```bash

  </mat-card-header>

  <mat-card-content class="space-y-4">

```bash

<mat-form-field class="w-full">
  <mat-label>Holzbau Anzahl</mat-label>
  <input matInput type="number" class="text-lg">
</mat-form-field>

```bash

  </mat-card-content>

  <mat-card-actions class="flex justify-end space-x-2">

```bash

<button mat-button class="px-4 py-2 text-gray-600 hover:text-gray-800">
  Cancel
</button>
<button mat-raised-button color="primary" class="px-6 py-2">
  Save
</button>

```bash

  </mat-card-actions>
</mat-card>

```bash

## Key Design Principles


### 1. Hexagonal Architecture


- **Adapters**: Handle external concerns (HTTP, database, messaging)
- **Application**: Contains business logic and use cases
- **Shared**: Cross-cutting concerns and configuration

### 2. Domain-Driven Design


- Rich domain models with business logic
- Repository pattern for data access
- Value objects for complex concepts

### 3. Clean Architecture


- Dependencies point inward
- Domain is independent of frameworks
- Easy to test and maintain

## Data Flow


### Authentication Flow


```mermaid

## Key Design Principles


### 1. Hexagonal Architecture


- **Adapters**: Handle external concerns (HTTP, database, messaging)
- **Application**: Contains business logic and use cases
- **Shared**: Cross-cutting concerns and configuration

### 2. Domain-Driven Design


- Rich domain models with business logic
- Repository pattern for data access
- Value objects for complex concepts

### 3. Clean Architecture


- Dependencies point inward
- Domain is independent of frameworks
- Easy to test and maintain

## Data Flow


### Authentication Flow


```mermaid
sequenceDiagram

```bash

participant U as User
participant F as Frontend
participant C as Cognito User Pool
participant B as Backend
participant D as Database

```bash

```bash

U->>F: Enter credentials
F->>C: Authenticate with Cognito
C-->>F: JWT tokens (ID, Access, Refresh)
F->>B: API request with Access token
B->>B: Validate JWT token
B->>D: Get user profile
D-->>B: User data
B-->>F: API response
F-->>U: Display data

```bash

```bash

sequenceDiagram

```bash

participant U as User
participant F as Frontend
participant C as Cognito User Pool
participant B as Backend
participant D as Database

```bash

```bash

U->>F: Enter credentials
F->>C: Authenticate with Cognito
C-->>F: JWT tokens (ID, Access, Refresh)
F->>B: API request with Access token
B->>B: Validate JWT token
B->>D: Get user profile
D-->>B: User data
B-->>F: API response
F-->>U: Display data

```bash

```bash

### Bedarf Creation Flow


```mermaid

### Bedarf Creation Flow


```mermaid

sequenceDiagram

```bash

participant U as User
participant F as Frontend
participant B as Backend
participant D as Database

```bash

```bash

U->>F: Create bedarf
F->>B: POST /api/v1/bedarf
B->>B: Validate business rules
B->>D: Save bedarf
D-->>B: Confirmation
B-->>F: Bedarf created
F-->>U: Success message

```bash

```bash

sequenceDiagram

```bash

participant U as User
participant F as Frontend
participant B as Backend
participant D as Database

```bash

```bash

U->>F: Create bedarf
F->>B: POST /api/v1/bedarf
B->>B: Validate business rules
B->>D: Save bedarf
D-->>B: Confirmation
B-->>F: Bedarf created
F-->>U: Success message

```bash

```bash

## Security


### Authentication


- AWS Cognito User Pool for user management
- OAuth 2.0 support for enterprise SSO
- Multi-factor authentication (MFA)
- JWT tokens with automatic validation

### Authorization


- Cognito Groups for role-based access control
- Spring Security integration
- Method-level security annotations

## Related


- [Backend Architecture](backend-architecture.md) - Detailed backend structure
- [Authentication Flow](../06-runtime/authentication-flow.md) - Runtime authentication process
- [Deployment Guide](../deployment.md) - Production deployment

## Security


### Authentication


- AWS Cognito User Pool for user management
- OAuth 2.0 support for enterprise SSO
- Multi-factor authentication (MFA)
- JWT tokens with automatic validation

### Authorization


- Cognito Groups for role-based access control
- Spring Security integration
- Method-level security annotations

## Related


- [Backend Architecture](backend-architecture.md) - Detailed backend structure
- [Authentication Flow](../06-runtime/authentication-flow.md) - Runtime authentication process
- [Deployment Guide](../deployment.md) - Production deployment
