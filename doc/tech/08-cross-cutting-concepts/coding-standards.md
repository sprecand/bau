# Coding Standards & Patterns

## Overview


This document defines all coding standards, patterns, and best practices for the Bau platform development.

## Table of Contents


1. [Backend Standards (Java 21 + Spring Boot 3)](#backend-standards)
2. [Frontend Standards (Angular 20+)](#frontend-standards)
3. [Architecture Patterns](#architecture-patterns)
4. [Testing Standards](#testing-standards)
5. [Code Quality Tools](#code-quality-tools)
6. [Business Domain Rules](#business-domain-rules)

---

## Backend Standards (Java 21 + Spring Boot 3)


### Code Quality Principles


- **Use Optionals**: Never return null, use `Optional<T>` for nullable results
- **Minimal Javadoc**: Only add Javadoc for public methods that need explanation
- **SLF4J Logging**: Use proper log levels (debug, info, warn, error)
- **No Inline Comments**: Avoid inline comments, prefer Javadoc when needed
- **Exception Handling**: Minimize exception throwing, use return values

### Package Structure


```bash

backend/src/main/java/com/bau/
├── adapter/
│   ├── in/                    # Driving adapters
│   │   ├── web/              # REST controllers + DTOs
│   │   └── event/            # Event listeners
│   └── out/                  # Driven adapters
│       ├── persistence/      # Database repositories + entities
│       ├── external/         # External API clients
│       └── messaging/        # Message brokers
├── application/
│   ├── domain/              # Domain entities + business logic
│   ├── usecase/             # Use case implementations
│   └── port/                # Port interfaces
│       ├── in/              # Input ports (use case interfaces)
│       └── out/             # Output ports (repository interfaces)
└── shared/

```bash

├── config/              # Configuration classes
├── util/                # Utility classes
└── exception/           # Custom exceptions

```bash

```bash

backend/src/main/java/com/bau/
├── adapter/
│   ├── in/                    # Driving adapters
│   │   ├── web/              # REST controllers + DTOs
│   │   └── event/            # Event listeners
│   └── out/                  # Driven adapters
│       ├── persistence/      # Database repositories + entities
│       ├── external/         # External API clients
│       └── messaging/        # Message brokers
├── application/
│   ├── domain/              # Domain entities + business logic
│   ├── usecase/             # Use case implementations
│   └── port/                # Port interfaces
│       ├── in/              # Input ports (use case interfaces)
│       └── out/             # Output ports (repository interfaces)
└── shared/

```bash

├── config/              # Configuration classes
├── util/                # Utility classes
└── exception/           # Custom exceptions

```bash

```bash

### Code Patterns


#### Domain Entity Template


```java

### Code Patterns


#### Domain Entity Template


```java

public class {EntityName} {

```bash

private UUID id;
private UUID betriebId;
// other fields

```bash

```bash

// Business logic methods
public boolean isValid{BusinessRule}() {
    // validation logic
}

```bash

```bash

// No getters/setters in domain entities
// Use constructor or builder pattern

```bash

}

```bash

public class {EntityName} {

```bash

private UUID id;
private UUID betriebId;
// other fields

```bash

```bash

// Business logic methods
public boolean isValid{BusinessRule}() {
    // validation logic
}

```bash

```bash

// No getters/setters in domain entities
// Use constructor or builder pattern

```bash

}

```bash

#### Use Case Template


```java

#### Use Case Template


```java

@Service
@Slf4j
public class {Action}{EntityName}UseCase {

```bash

private static final Logger log = LoggerFactory.getLogger({Action}{EntityName}UseCase.class);

```bash

```bash

private final {EntityName}Repository {entityName}Repository;
private final {EntityName}Mapper {entityName}Mapper;

```bash

```bash

public Optional<{EntityName}> execute({Action}{EntityName}Request request) {
    log.debug("Processing {action} for {entityName}: {}", request.getId());

```bash

```bash

{EntityName} {entityName} = {entityName}Mapper.toDomain(request);

```bash

```bash

// Business validation
if (!{entityName}.isValid{BusinessRule}()) {
    log.warn("Invalid {businessRule} for {entityName}: {}", request.getId());
    return Optional.empty();
}

```bash

```bash

{EntityName} saved{EntityName} = {entityName}Repository.save({entityName});
log.info("Successfully {action} {entityName} with id: {}", saved{EntityName}.getId());

```bash

```bash

return Optional.of(saved{EntityName});
}

```bash

}

```bash

@Service
@Slf4j
public class {Action}{EntityName}UseCase {

```bash

private static final Logger log = LoggerFactory.getLogger({Action}{EntityName}UseCase.class);

```bash

```bash

private final {EntityName}Repository {entityName}Repository;
private final {EntityName}Mapper {entityName}Mapper;

```bash

```bash

public Optional<{EntityName}> execute({Action}{EntityName}Request request) {
    log.debug("Processing {action} for {entityName}: {}", request.getId());

```bash

```bash

{EntityName} {entityName} = {entityName}Mapper.toDomain(request);

```bash

```bash

// Business validation
if (!{entityName}.isValid{BusinessRule}()) {
    log.warn("Invalid {businessRule} for {entityName}: {}", request.getId());
    return Optional.empty();
}

```bash

```bash

{EntityName} saved{EntityName} = {entityName}Repository.save({entityName});
log.info("Successfully {action} {entityName} with id: {}", saved{EntityName}.getId());

```bash

```bash

return Optional.of(saved{EntityName});
}

```bash

}

```bash

#### Controller Template


```java

#### Controller Template


```java

@RestController
@RequiredArgsConstructor
@Slf4j
public class {EntityName}ApiController implements {EntityName}Api {

```bash

private final {EntityName}UseCase {entityName}UseCase;
private final {EntityName}WebMapper {entityName}WebMapper;

```bash

```bash

@Override
public ResponseEntity<{EntityName}ResponseDto> create{EntityName}({EntityName}CreateRequestDto request) {
    log.debug("Creating {entityName}: {}", request);

```bash

```bash

Optional<{EntityName}> {entityName}Opt = {entityName}UseCase.create{EntityName}(request);

```bash

```bash

if ({entityName}Opt.isEmpty()) {
    log.warn("{EntityName} creation failed");
    return ResponseEntity.badRequest().build();
}

```bash

```bash

{EntityName}ResponseDto response = {entityName}WebMapper.toResponseDto({entityName}Opt.get());
log.info("Successfully created {entityName} with id: {}", {entityName}Opt.get().getId());

```bash

```bash

return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

```bash

```bash

@Override
public ResponseEntity<List<{EntityName}ResponseDto>> getAll{EntityName}s(/*pagination params*/) {
    List<{EntityName}> {entityName}s = {entityName}UseCase.getAll{EntityName}s();
    List<{EntityName}ResponseDto> response = {entityName}s.stream()
        .map({entityName}WebMapper::toResponseDto)
        .toList();
    return ResponseEntity.ok(response);
}

```bash

```bash

// ... other CRUD operations from generated {EntityName}Api interface

```bash

}

```bash

@RestController
@RequiredArgsConstructor
@Slf4j
public class {EntityName}ApiController implements {EntityName}Api {

```bash

private final {EntityName}UseCase {entityName}UseCase;
private final {EntityName}WebMapper {entityName}WebMapper;

```bash

```bash

@Override
public ResponseEntity<{EntityName}ResponseDto> create{EntityName}({EntityName}CreateRequestDto request) {
    log.debug("Creating {entityName}: {}", request);

```bash

```bash

Optional<{EntityName}> {entityName}Opt = {entityName}UseCase.create{EntityName}(request);

```bash

```bash

if ({entityName}Opt.isEmpty()) {
    log.warn("{EntityName} creation failed");
    return ResponseEntity.badRequest().build();
}

```bash

```bash

{EntityName}ResponseDto response = {entityName}WebMapper.toResponseDto({entityName}Opt.get());
log.info("Successfully created {entityName} with id: {}", {entityName}Opt.get().getId());

```bash

```bash

return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

```bash

```bash

@Override
public ResponseEntity<List<{EntityName}ResponseDto>> getAll{EntityName}s(/*pagination params*/) {
    List<{EntityName}> {entityName}s = {entityName}UseCase.getAll{EntityName}s();
    List<{EntityName}ResponseDto> response = {entityName}s.stream()
        .map({entityName}WebMapper::toResponseDto)
        .toList();
    return ResponseEntity.ok(response);
}

```bash

```bash

// ... other CRUD operations from generated {EntityName}Api interface

```bash

}

```bash

#### Repository Interface Template


```java

#### Repository Interface Template


```java

public interface {EntityName}Repository {

```bash

{EntityName} save({EntityName} {entityName});
Optional<{EntityName}> findById(UUID id);
List<{EntityName}> findByBetriebId(UUID betriebId);
// other query methods

```bash

}

```bash

public interface {EntityName}Repository {

```bash

{EntityName} save({EntityName} {entityName});
Optional<{EntityName}> findById(UUID id);
List<{EntityName}> findByBetriebId(UUID betriebId);
// other query methods

```bash

}

```bash

#### JPA Entity Template


```java

#### JPA Entity Template


```java

@Entity
@Table(name = "{tableName}")
public class {EntityName}Entity {

```bash

@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;

```bash

```bash

@Column(name = "betrieb_id")
private UUID betriebId;

```bash

```bash

// other fields with proper column mappings

```bash

```bash

@CreatedDate
@Column(name = "created_at")
private LocalDateTime createdAt;

```bash

```bash

@LastModifiedDate
@Column(name = "updated_at")
private LocalDateTime updatedAt;

```bash

}

```bash

@Entity
@Table(name = "{tableName}")
public class {EntityName}Entity {

```bash

@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;

```bash

```bash

@Column(name = "betrieb_id")
private UUID betriebId;

```bash

```bash

// other fields with proper column mappings

```bash

```bash

@CreatedDate
@Column(name = "created_at")
private LocalDateTime createdAt;

```bash

```bash

@LastModifiedDate
@Column(name = "updated_at")
private LocalDateTime updatedAt;

```bash

}

```bash

#### Mapper Template


```java

#### Mapper Template


```java

@Component
public class {EntityName}Mapper {

```bash

public {EntityName} toDomain(Create{EntityName}Request request) {
    {EntityName} {entityName} = new {EntityName}();
    {entityName}.setBetriebId(request.getBetriebId());
    // map other fields
    return {entityName};
}

```bash

```bash

public {EntityName}Response toResponse({EntityName} {entityName}) {
    {EntityName}Response response = new {EntityName}Response();
    response.setId({entityName}.getId());
    response.setBetriebId({entityName}.getBetriebId());
    // map other fields
    return response;
}

```bash

}

```bash

@Component
public class {EntityName}Mapper {

```bash

public {EntityName} toDomain(Create{EntityName}Request request) {
    {EntityName} {entityName} = new {EntityName}();
    {entityName}.setBetriebId(request.getBetriebId());
    // map other fields
    return {entityName};
}

```bash

```bash

public {EntityName}Response toResponse({EntityName} {entityName}) {
    {EntityName}Response response = new {EntityName}Response();
    response.setId({entityName}.getId());
    response.setBetriebId({entityName}.getBetriebId());
    // map other fields
    return response;
}

```bash

}

```bash

### Java 21 Best Practices


#### Language Features


- Use **records**for immutable data transfer objects
- Use**pattern matching**for switch expressions and instanceof
- Use**text blocks**for multi-line strings
- Use**sealed classes**for type hierarchies
- Use**virtual threads**for I/O operations (Spring Boot 3.2+)

#### Null Safety


- Prefer `Optional<T>` over nullable references
- Use `Optional.ofNullable()` for potentially null values
- Use `Optional.orElse()` or `Optional.orElseGet()` for defaults
- Avoid `Optional.get()` without checking `isPresent()`

#### Logging (SLF4J)


```java

### Java 21 Best Practices


#### Language Features


- Use**records**for immutable data transfer objects
- Use**pattern matching**for switch expressions and instanceof
- Use**text blocks**for multi-line strings
- Use**sealed classes**for type hierarchies
- Use**virtual threads**for I/O operations (Spring Boot 3.2+)

#### Null Safety


- Prefer `Optional<T>` over nullable references
- Use `Optional.ofNullable()` for potentially null values
- Use `Optional.orElse()` or `Optional.orElseGet()` for defaults
- Avoid `Optional.get()` without checking `isPresent()`

#### Logging (SLF4J)


```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j  // Lombok annotation
public class ExampleService {

```bash

private static final Logger log = LoggerFactory.getLogger(ExampleService.class);

```bash

```bash

public void processData(String data) {
    log.debug("Processing data: {}", data);

```bash

```bash

try {
    // processing logic
    log.info("Successfully processed data: {}", data);
} catch (Exception e) {
    log.error("Failed to process data: {}", data, e);
    throw e;
}
}

```bash

}

```bash

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j  // Lombok annotation
public class ExampleService {

```bash

private static final Logger log = LoggerFactory.getLogger(ExampleService.class);

```bash

```bash

public void processData(String data) {
    log.debug("Processing data: {}", data);

```bash

```bash

try {
    // processing logic
    log.info("Successfully processed data: {}", data);
} catch (Exception e) {
    log.error("Failed to process data: {}", data, e);
    throw e;
}
}

```bash

}

```bash

### Spring Boot 3.x Patterns


#### Configuration


- Use `@ConfigurationProperties` for externalized configuration
- Use `@ConditionalOnProperty` for conditional beans
- Use `@Profile` for environment-specific configurations
- Use `application.yml` for hierarchical configuration

#### Dependency Injection


- Use constructor injection for required dependencies
- Use `@Autowired` sparingly (constructor injection is preferred)
- Use `@Qualifier` when multiple beans of same type exist
- Use `@Lazy` for expensive beans that aren't always needed

#### Validation


- Use Bean Validation annotations on DTOs
- Use `@Valid` in controller methods
- Create custom validators for complex business rules
- Use `@Validated` for method-level validation

#### Security


- Use Spring Security with JWT tokens
- Use `@PreAuthorize` for method-level security
- Use `@Secured` for role-based access control
- Use `@EnableMethodSecurity` for method security

### Database Patterns


#### JPA Best Practices


- Use `@Entity` for database entities only
- Use `@Table` to specify table names
- Use `@Column` for column mappings
- Use `@Id` and `@GeneratedValue` for primary keys
- Use `@CreatedDate` and `@LastModifiedDate` for audit fields

#### Repository Pattern


- Extend `JpaRepository` for basic CRUD operations
- Use `@Query` for complex queries
- Use `@Modifying` for update/delete operations
- Use `@Transactional` for multi-step operations

#### Transaction Management


- Use `@Transactional` at service layer
- Use `REQUIRED` propagation (default)
- Use `READ_COMMITTED` isolation level (default)
- Use `@Transactional(readOnly = true)` for read operations

### API Design


#### REST Controllers


- Use `@RestController` for REST APIs
- Use `@RequestMapping` for base paths
- Use `@GetMapping`, `@PostMapping`, etc. for HTTP methods
- Use `@PathVariable` for path parameters
- Use `@RequestParam` for query parameters
- Use `@RequestBody` for request bodies

#### Response Handling


- Use `ResponseEntity<T>` for custom responses
- Use appropriate HTTP status codes
- Use consistent error response format
- Use `@ResponseStatus` for default status codes

#### Error Handling


- Use `@ControllerAdvice` for global exception handling
- Use `@ExceptionHandler` for specific exceptions
- Return consistent error response format
- Log errors appropriately

---

## Frontend Standards (Angular 20+)


### Component Patterns


#### Standalone Component Template


```typescript

### Spring Boot 3.x Patterns


#### Configuration


- Use `@ConfigurationProperties` for externalized configuration
- Use `@ConditionalOnProperty` for conditional beans
- Use `@Profile` for environment-specific configurations
- Use `application.yml` for hierarchical configuration

#### Dependency Injection


- Use constructor injection for required dependencies
- Use `@Autowired` sparingly (constructor injection is preferred)
- Use `@Qualifier` when multiple beans of same type exist
- Use `@Lazy` for expensive beans that aren't always needed

#### Validation


- Use Bean Validation annotations on DTOs
- Use `@Valid` in controller methods
- Create custom validators for complex business rules
- Use `@Validated` for method-level validation

#### Security


- Use Spring Security with JWT tokens
- Use `@PreAuthorize` for method-level security
- Use `@Secured` for role-based access control
- Use `@EnableMethodSecurity` for method security

### Database Patterns


#### JPA Best Practices


- Use `@Entity` for database entities only
- Use `@Table` to specify table names
- Use `@Column` for column mappings
- Use `@Id` and `@GeneratedValue` for primary keys
- Use `@CreatedDate` and `@LastModifiedDate` for audit fields

#### Repository Pattern


- Extend `JpaRepository` for basic CRUD operations
- Use `@Query` for complex queries
- Use `@Modifying` for update/delete operations
- Use `@Transactional` for multi-step operations

#### Transaction Management


- Use `@Transactional` at service layer
- Use `REQUIRED` propagation (default)
- Use `READ_COMMITTED` isolation level (default)
- Use `@Transactional(readOnly = true)` for read operations

### API Design


#### REST Controllers


- Use `@RestController` for REST APIs
- Use `@RequestMapping` for base paths
- Use `@GetMapping`, `@PostMapping`, etc. for HTTP methods
- Use `@PathVariable` for path parameters
- Use `@RequestParam` for query parameters
- Use `@RequestBody` for request bodies

#### Response Handling


- Use `ResponseEntity<T>` for custom responses
- Use appropriate HTTP status codes
- Use consistent error response format
- Use `@ResponseStatus` for default status codes

#### Error Handling


- Use `@ControllerAdvice` for global exception handling
- Use `@ExceptionHandler` for specific exceptions
- Return consistent error response format
- Log errors appropriately

---

## Frontend Standards (Angular 20+)


### Component Patterns


#### Standalone Component Template


```typescript
@Component({
  selector: 'app-{component-name}',
  standalone: true,
  imports: [

```bash

CommonModule,
MatCardModule,
MatButtonModule,
// other Material modules

```bash

  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `

```bash

<mat-card class="p-6 bg-white shadow-lg rounded-lg">
  <mat-card-header class="mb-4">
    <mat-card-title class="text-2xl font-bold text-gray-800">
      {{ title }}
    </mat-card-title>
  </mat-card-header>

```bash

```bash

<mat-card-content class="space-y-4">
  <!-- content -->
</mat-card-content>

```bash

```bash

<mat-card-actions class="flex justify-end space-x-2">
  <button mat-button class="px-4 py-2 text-gray-600 hover:text-gray-800">
    Cancel
  </button>
  <button mat-raised-button color="primary" class="px-6 py-2">
    Save
  </button>
</mat-card-actions>
mat-card>

```bash

  `
})
export class {ComponentName}Component {
  private readonly {serviceName} = inject({ServiceName});

  // Use signals for reactive state
  protected readonly {data} = signal<{DataType}[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  constructor() {

```bash

this.load{Data}();

```bash

  }

  private async load{Data}(): Promise<void> {

```bash

this.loading.set(true);
this.error.set(null);

```bash

```bash

try {
  const result = await this.{serviceName}.get{Data}();
  this.{data}.set(result);
} catch (err) {
  this.error.set('Failed to load data');
  console.error('Error loading data:', err);
} finally {
  this.loading.set(false);
}

```bash

  }
}

```bash

@Component({
  selector: 'app-{component-name}',
  standalone: true,
  imports: [

```bash

CommonModule,
MatCardModule,
MatButtonModule,
// other Material modules

```bash

  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `

```bash

<mat-card class="p-6 bg-white shadow-lg rounded-lg">
  <mat-card-header class="mb-4">
    <mat-card-title class="text-2xl font-bold text-gray-800">
      {{ title }}
    </mat-card-title>
  </mat-card-header>

```bash

```bash

<mat-card-content class="space-y-4">
  <!-- content -->
</mat-card-content>

```bash

```bash

<mat-card-actions class="flex justify-end space-x-2">
  <button mat-button class="px-4 py-2 text-gray-600 hover:text-gray-800">
    Cancel
  </button>
  <button mat-raised-button color="primary" class="px-6 py-2">
    Save
  </button>
</mat-card-actions>
mat-card>

```bash

  `
})
export class {ComponentName}Component {
  private readonly {serviceName} = inject({ServiceName});

  // Use signals for reactive state
  protected readonly {data} = signal<{DataType}[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  constructor() {

```bash

this.load{Data}();

```bash

  }

  private async load{Data}(): Promise<void> {

```bash

this.loading.set(true);
this.error.set(null);

```bash

```bash

try {
  const result = await this.{serviceName}.get{Data}();
  this.{data}.set(result);
} catch (err) {
  this.error.set('Failed to load data');
  console.error('Error loading data:', err);
} finally {
  this.loading.set(false);
}

```bash

  }
}

```bash

#### Service Template


```typescript

#### Service Template


```typescript

@Injectable({ providedIn: 'root' })
export class {EntityName}Service {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl = '/api/v1/{entityName}';

  async get{EntityName}s(): Promise<{EntityName}[]> {

```bash

const token = await this.authService.getIdToken();
const headers = { Authorization: `Bearer ${token}` };

```bash

```bash

return firstValueFrom(
  this.http.get<{EntityName}[]>(this.apiUrl, { headers })
);

```bash

  }

  async create{EntityName}(request: Create{EntityName}Request): Promise<{EntityName}> {

```bash

const token = await this.authService.getIdToken();
const headers = { Authorization: `Bearer ${token}` };

```bash

```bash

return firstValueFrom(
  this.http.post<{EntityName}>(this.apiUrl, request, { headers })
);

```bash

  }

  async get{EntityName}ById(id: string): Promise<{EntityName}> {

```bash

const token = await this.authService.getIdToken();
const headers = { Authorization: `Bearer ${token}` };

```bash

```bash

return firstValueFrom(
  this.http.get<{EntityName}>(`${this.apiUrl}/${id}`, { headers })
);

```bash

  }
}

```bash

@Injectable({ providedIn: 'root' })
export class {EntityName}Service {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  private readonly apiUrl = '/api/v1/{entityName}';

  async get{EntityName}s(): Promise<{EntityName}[]> {

```bash

const token = await this.authService.getIdToken();
const headers = { Authorization: `Bearer ${token}` };

```bash

```bash

return firstValueFrom(
  this.http.get<{EntityName}[]>(this.apiUrl, { headers })
);

```bash

  }

  async create{EntityName}(request: Create{EntityName}Request): Promise<{EntityName}> {

```bash

const token = await this.authService.getIdToken();
const headers = { Authorization: `Bearer ${token}` };

```bash

```bash

return firstValueFrom(
  this.http.post<{EntityName}>(this.apiUrl, request, { headers })
);

```bash

  }

  async get{EntityName}ById(id: string): Promise<{EntityName}> {

```bash

const token = await this.authService.getIdToken();
const headers = { Authorization: `Bearer ${token}` };

```bash

```bash

return firstValueFrom(
  this.http.get<{EntityName}>(`${this.apiUrl}/${id}`, { headers })
);

```bash

  }
}

```bash

#### Form Component Template


```typescript

#### Form Component Template


```typescript

@Component({
  selector: 'app-{entity-name}-form',
  standalone: true,
  imports: [

```bash

CommonModule,
ReactiveFormsModule,
MatFormFieldModule,
MatInputModule,
MatButtonModule,
MatCardModule,

```bash

  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `

```bash

<mat-card class="p-6 bg-white shadow-lg rounded-lg">
  <mat-card-header class="mb-4">
    <mat-card-title class="text-2xl font-bold text-gray-800">
      {{ isEditMode ? 'Edit' : 'Create' }} {EntityName}
    </mat-card-title>
  </mat-card-header>

```bash

```bash

<mat-card-content>
  <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
    <mat-form-field class="w-full">
      <mat-label>Name</mat-label>
      <input matInput formControlName="name" class="text-lg">
      <mat-error*ngIf="form.get('name')?.hasError('required')">
        Name is required
      </mat-error>
    </mat-form-field>

```bash

```bash

<!-- other form fields -->

```bash

```bash

<div class="flex justify-end space-x-2">
  <button type="button" mat-button
          class="px-4 py-2 text-gray-600 hover:text-gray-800"
          (click)="onCancel()">
    Cancel
  </button>
  <button type="submit" mat-raised-button color="primary"
          class="px-6 py-2"
          [disabled]="form.invalid || loading()">
    {{ isEditMode ? 'Update' : 'Create' }}
  </button>
</div>
form>
t-card-content>
card>

```bash

  `
})
export class {EntityName}FormComponent {
  private readonly {serviceName} = inject({ServiceName});
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = new FormGroup({

```bash

name: new FormControl('', [Validators.required]),
// other form controls

```bash

  });

  protected readonly loading = signal(false);
  protected readonly isEditMode = signal(false);

  constructor() {

```bash

this.initializeForm();

```bash

  }

  private initializeForm(): void {

```bash

const id = this.route.snapshot.paramMap.get('id');
if (id) {
  this.isEditMode.set(true);
  this.load{EntityName}(id);
}

```bash

  }

  private async load{EntityName}(id: string): Promise<void> {

```bash

try {
  const {entityName} = await this.{serviceName}.get{EntityName}ById(id);
  this.form.patchValue({entityName});
} catch (err) {
  console.error('Error loading {entityName}:', err);
}

```bash

  }

  protected async onSubmit(): Promise<void> {

```bash

if (this.form.invalid) return;

```bash

```bash

this.loading.set(true);

```bash

```bash

try {
  const request = this.form.value as Create{EntityName}Request;

```bash

```bash

if (this.isEditMode()) {
  const id = this.route.snapshot.paramMap.get('id')!;
  await this.{serviceName}.update{EntityName}(id, request);
} else {
  await this.{serviceName}.create{EntityName}(request);
}

```bash

```bash

this.router.navigate(['/{entityName}s']);
catch (err) {
console.error('Error saving {entityName}:', err);
finally {
this.loading.set(false);
}

```bash

  }

  protected onCancel(): void {

```bash

this.router.navigate(['/{entityName}s']);

```bash

  }
}

```bash

@Component({
  selector: 'app-{entity-name}-form',
  standalone: true,
  imports: [

```bash

CommonModule,
ReactiveFormsModule,
MatFormFieldModule,
MatInputModule,
MatButtonModule,
MatCardModule,

```bash

  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `

```bash

<mat-card class="p-6 bg-white shadow-lg rounded-lg">
  <mat-card-header class="mb-4">
    <mat-card-title class="text-2xl font-bold text-gray-800">
      {{ isEditMode ? 'Edit' : 'Create' }} {EntityName}
    </mat-card-title>
  </mat-card-header>

```bash

```bash

<mat-card-content>
  <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
    <mat-form-field class="w-full">
      <mat-label>Name</mat-label>
      <input matInput formControlName="name" class="text-lg">
      <mat-error *ngIf="form.get('name')?.hasError('required')">
        Name is required
      </mat-error>
    </mat-form-field>

```bash

```bash

<!-- other form fields -->

```bash

```bash

<div class="flex justify-end space-x-2">
  <button type="button" mat-button
          class="px-4 py-2 text-gray-600 hover:text-gray-800"
          (click)="onCancel()">
    Cancel
  </button>
  <button type="submit" mat-raised-button color="primary"
          class="px-6 py-2"
          [disabled]="form.invalid || loading()">
    {{ isEditMode ? 'Update' : 'Create' }}
  </button>
</div>
form>
t-card-content>
card>

```bash

  `
})
export class {EntityName}FormComponent {
  private readonly {serviceName} = inject({ServiceName});
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = new FormGroup({

```bash

name: new FormControl('', [Validators.required]),
// other form controls

```bash

  });

  protected readonly loading = signal(false);
  protected readonly isEditMode = signal(false);

  constructor() {

```bash

this.initializeForm();

```bash

  }

  private initializeForm(): void {

```bash

const id = this.route.snapshot.paramMap.get('id');
if (id) {
  this.isEditMode.set(true);
  this.load{EntityName}(id);
}

```bash

  }

  private async load{EntityName}(id: string): Promise<void> {

```bash

try {
  const {entityName} = await this.{serviceName}.get{EntityName}ById(id);
  this.form.patchValue({entityName});
} catch (err) {
  console.error('Error loading {entityName}:', err);
}

```bash

  }

  protected async onSubmit(): Promise<void> {

```bash

if (this.form.invalid) return;

```bash

```bash

this.loading.set(true);

```bash

```bash

try {
  const request = this.form.value as Create{EntityName}Request;

```bash

```bash

if (this.isEditMode()) {
  const id = this.route.snapshot.paramMap.get('id')!;
  await this.{serviceName}.update{EntityName}(id, request);
} else {
  await this.{serviceName}.create{EntityName}(request);
}

```bash

```bash

this.router.navigate(['/{entityName}s']);
catch (err) {
console.error('Error saving {entityName}:', err);
finally {
this.loading.set(false);
}

```bash

  }

  protected onCancel(): void {

```bash

this.router.navigate(['/{entityName}s']);

```bash

  }
}

```bash

### Angular 20+ Best Practices


#### Standalone Components


- Use standalone components instead of NgModules
- Import only required dependencies
- Use `changeDetection: ChangeDetectionStrategy.OnPush` for performance
- Use `inject()` function for dependency injection

#### Signals (Modern State Management)


- Use signals for reactive state management
- Use `signal()` for mutable state
- Use `computed()` for derived state
- Use `effect()` for side effects
- Prefer signals over RxJS for simple state

#### Dependency Injection


- Use `inject()` function instead of constructor injection
- Use `providedIn: 'root'` for singleton services
- Use `providedIn: 'any'` for component-scoped services
- Use `providedIn: 'platform'` for platform-wide services

#### TypeScript Best Practices


- Use strict TypeScript configuration
- Use interfaces for object shapes
- Use type aliases for complex types
- Use generics for reusable components
- Use union types for discriminated unions

### Styling Strategy


- **Angular Material**: Pre-built components (buttons, forms, dialogs)
- **Tailwind CSS**: Custom styling, layout, responsive design
- **Design System**: OKLCH-based color tokens with dark mode support
- **Combination**: Material components with Tailwind utilities and design tokens
- **Example**: `<mat-card class="p-6 bg-card shadow-md border border-border">`

### Design System


#### Color System (OKLCH)


We use a modern OKLCH-based color system that provides:
- **Perceptual Uniformity**: Consistent lightness across all hues
- **Better Accessibility**: Improved contrast ratios
- **Dark Mode Support**: Complete theme switching capability
- **Semantic Tokens**: Meaningful color names for maintainability

#### CSS Custom Properties


```css

### Angular 20+ Best Practices


#### Standalone Components


- Use standalone components instead of NgModules
- Import only required dependencies
- Use `changeDetection: ChangeDetectionStrategy.OnPush` for performance
- Use `inject()` function for dependency injection

#### Signals (Modern State Management)


- Use signals for reactive state management
- Use `signal()` for mutable state
- Use `computed()` for derived state
- Use `effect()` for side effects
- Prefer signals over RxJS for simple state

#### Dependency Injection


- Use `inject()` function instead of constructor injection
- Use `providedIn: 'root'` for singleton services
- Use `providedIn: 'any'` for component-scoped services
- Use `providedIn: 'platform'` for platform-wide services

#### TypeScript Best Practices


- Use strict TypeScript configuration
- Use interfaces for object shapes
- Use type aliases for complex types
- Use generics for reusable components
- Use union types for discriminated unions

### Styling Strategy


- **Angular Material**: Pre-built components (buttons, forms, dialogs)
- **Tailwind CSS**: Custom styling, layout, responsive design
- **Design System**: OKLCH-based color tokens with dark mode support
- **Combination**: Material components with Tailwind utilities and design tokens
- **Example**: `<mat-card class="p-6 bg-card shadow-md border border-border">`

### Design System


#### Color System (OKLCH)


We use a modern OKLCH-based color system that provides:
- **Perceptual Uniformity**: Consistent lightness across all hues
- **Better Accessibility**: Improved contrast ratios
- **Dark Mode Support**: Complete theme switching capability
- **Semantic Tokens**: Meaningful color names for maintainability

#### CSS Custom Properties


```css
:root {
  /*Core Colors*/
--background: oklch(1 0 0);
--foreground: oklch(0.1450 0 0);
--card: oklch(1 0 0);
--card-foreground: oklch(0.1450 0 0);

  /*Brand Colors*/
--primary: oklch(0.6324 0.1363 157.8607);
--primary-foreground: oklch(0.9850 0 0);
--secondary: oklch(0.9700 0 0);
--secondary-foreground: oklch(0.2050 0 0);

  /*State Colors*/
--destructive: oklch(0.5770 0.2450 27.3250);
--destructive-foreground: oklch(1 0 0);
--muted: oklch(0.9700 0 0);
--muted-foreground: oklch(0.5560 0 0);

  /*UI Elements*/
--border: oklch(0.9220 0 0);
--input: oklch(0.9220 0 0);
--ring: oklch(0.7080 0 0);

  /*Sidebar*/
--sidebar: oklch(0.9850 0 0);
--sidebar-foreground: oklch(0.1450 0 0);
--sidebar-primary: oklch(0.2050 0 0);
--sidebar-primary-foreground: oklch(0.9850 0 0);

  /*Typography*/
--font-sans:
    ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
--font-serif: ui-serif, Georgia, Cambria, "Times New Roman", Times, serif;
--font-mono: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;

  /*Spacing & Borders*/
--radius: 0.625rem;

  /*Shadows*/
--shadow-sm: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 1px 2px -1px hsl(0 0% 0% / 0.10);
--shadow-md: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 2px 4px -1px hsl(0 0% 0% / 0.10);
--shadow-lg: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 4px 6px -1px hsl(0 0% 0% / 0.10);
}

.dark {
--background: oklch(0.1450 0 0);
--foreground: oklch(0.9850 0 0);
--card: oklch(0.2050 0 0);
--card-foreground: oklch(0.9850 0 0);
--primary: oklch(0.6324 0.1363 157.8607);
--primary-foreground: oklch(0.2535 0.0341 296.6556);
  /*... other dark mode colors*/
}

```bash

:root {
  /*Core Colors*/
--background: oklch(1 0 0);
--foreground: oklch(0.1450 0 0);
--card: oklch(1 0 0);
--card-foreground: oklch(0.1450 0 0);

  /*Brand Colors*/
--primary: oklch(0.6324 0.1363 157.8607);
--primary-foreground: oklch(0.9850 0 0);
--secondary: oklch(0.9700 0 0);
--secondary-foreground: oklch(0.2050 0 0);

  /*State Colors*/
--destructive: oklch(0.5770 0.2450 27.3250);
--destructive-foreground: oklch(1 0 0);
--muted: oklch(0.9700 0 0);
--muted-foreground: oklch(0.5560 0 0);

  /*UI Elements*/
--border: oklch(0.9220 0 0);
--input: oklch(0.9220 0 0);
--ring: oklch(0.7080 0 0);

  /*Sidebar*/
--sidebar: oklch(0.9850 0 0);
--sidebar-foreground: oklch(0.1450 0 0);
--sidebar-primary: oklch(0.2050 0 0);
--sidebar-primary-foreground: oklch(0.9850 0 0);

  /*Typography*/
--font-sans:
    ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
--font-serif: ui-serif, Georgia, Cambria, "Times New Roman", Times, serif;
--font-mono: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;

  /*Spacing & Borders*/
--radius: 0.625rem;

  /*Shadows*/
--shadow-sm: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 1px 2px -1px hsl(0 0% 0% / 0.10);
--shadow-md: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 2px 4px -1px hsl(0 0% 0% / 0.10);
--shadow-lg: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 4px 6px -1px hsl(0 0% 0% / 0.10);
}

.dark {
--background: oklch(0.1450 0 0);
--foreground: oklch(0.9850 0 0);
--card: oklch(0.2050 0 0);
--card-foreground: oklch(0.9850 0 0);
--primary: oklch(0.6324 0.1363 157.8607);
--primary-foreground: oklch(0.2535 0.0341 296.6556);
  /*... other dark mode colors*/
}

```bash

#### Tailwind Integration


```javascript

#### Tailwind Integration


```javascript

// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  darkMode: 'class',
  theme: {

```bash

extend: {
  colors: {
    background: 'oklch(var(--background) / <alpha-value>)',
    foreground: 'oklch(var(--foreground) / <alpha-value>)',
    card: {
      DEFAULT: 'oklch(var(--card) / <alpha-value>)',
      foreground: 'oklch(var(--card-foreground) / <alpha-value>)'
    },
    primary: {
      DEFAULT: 'oklch(var(--primary) / <alpha-value>)',
      foreground: 'oklch(var(--primary-foreground) / <alpha-value>)'
    },
    secondary: {
      DEFAULT: 'oklch(var(--secondary) / <alpha-value>)',
      foreground: 'oklch(var(--secondary-foreground) / <alpha-value>)'
    },
    destructive: {
      DEFAULT: 'oklch(var(--destructive) / <alpha-value>)',
      foreground: 'oklch(var(--destructive-foreground) / <alpha-value>)'
    },
    muted: {
      DEFAULT: 'oklch(var(--muted) / <alpha-value>)',
      foreground: 'oklch(var(--muted-foreground) / <alpha-value>)'
    },
    border: 'oklch(var(--border) / <alpha-value>)',
    input: 'oklch(var(--input) / <alpha-value>)',
    ring: 'oklch(var(--ring) / <alpha-value>)',
    sidebar: {
      DEFAULT: 'oklch(var(--sidebar) / <alpha-value>)',
      foreground: 'oklch(var(--sidebar-foreground) / <alpha-value>)',
      primary: 'oklch(var(--sidebar-primary) / <alpha-value>)',
      'primary-foreground': 'oklch(var(--sidebar-primary-foreground) / <alpha-value>)',
      accent: 'oklch(var(--sidebar-accent) / <alpha-value>)',
      'accent-foreground': 'oklch(var(--sidebar-accent-foreground) / <alpha-value>)',
      border: 'oklch(var(--sidebar-border) / <alpha-value>)'
    }
  },
  fontFamily: {
    sans: ['var(--font-sans)'],
    serif: ['var(--font-serif)'],
    mono: ['var(--font-mono)']
  },
  borderRadius: {
    lg: 'var(--radius)',
    md: 'calc(var(--radius) - 2px)',
    sm: 'calc(var(--radius) - 4px)'
  },
  boxShadow: {
    sm: 'var(--shadow-sm)',
    md: 'var(--shadow-md)',
    lg: 'var(--shadow-lg)'
  }
}

```bash

  },
  plugins: []
}

```bash

// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  darkMode: 'class',
  theme: {

```bash

extend: {
  colors: {
    background: 'oklch(var(--background) / <alpha-value>)',
    foreground: 'oklch(var(--foreground) / <alpha-value>)',
    card: {
      DEFAULT: 'oklch(var(--card) / <alpha-value>)',
      foreground: 'oklch(var(--card-foreground) / <alpha-value>)'
    },
    primary: {
      DEFAULT: 'oklch(var(--primary) / <alpha-value>)',
      foreground: 'oklch(var(--primary-foreground) / <alpha-value>)'
    },
    secondary: {
      DEFAULT: 'oklch(var(--secondary) / <alpha-value>)',
      foreground: 'oklch(var(--secondary-foreground) / <alpha-value>)'
    },
    destructive: {
      DEFAULT: 'oklch(var(--destructive) / <alpha-value>)',
      foreground: 'oklch(var(--destructive-foreground) / <alpha-value>)'
    },
    muted: {
      DEFAULT: 'oklch(var(--muted) / <alpha-value>)',
      foreground: 'oklch(var(--muted-foreground) / <alpha-value>)'
    },
    border: 'oklch(var(--border) / <alpha-value>)',
    input: 'oklch(var(--input) / <alpha-value>)',
    ring: 'oklch(var(--ring) / <alpha-value>)',
    sidebar: {
      DEFAULT: 'oklch(var(--sidebar) / <alpha-value>)',
      foreground: 'oklch(var(--sidebar-foreground) / <alpha-value>)',
      primary: 'oklch(var(--sidebar-primary) / <alpha-value>)',
      'primary-foreground': 'oklch(var(--sidebar-primary-foreground) / <alpha-value>)',
      accent: 'oklch(var(--sidebar-accent) / <alpha-value>)',
      'accent-foreground': 'oklch(var(--sidebar-accent-foreground) / <alpha-value>)',
      border: 'oklch(var(--sidebar-border) / <alpha-value>)'
    }
  },
  fontFamily: {
    sans: ['var(--font-sans)'],
    serif: ['var(--font-serif)'],
    mono: ['var(--font-mono)']
  },
  borderRadius: {
    lg: 'var(--radius)',
    md: 'calc(var(--radius) - 2px)',
    sm: 'calc(var(--radius) - 4px)'
  },
  boxShadow: {
    sm: 'var(--shadow-sm)',
    md: 'var(--shadow-md)',
    lg: 'var(--shadow-lg)'
  }
}

```bash

  },
  plugins: []
}

```bash

#### Angular Material Theme Integration


```scss

#### Angular Material Theme Integration


```scss

// styles/material-theme.scss
@use '@angular/material' as mat;

// Define custom palettes using our design tokens
$custom-primary: mat.define-palette((
  50: oklch(var(--primary) / 0.1),
  100: oklch(var(--primary) / 0.2),
  200: oklch(var(--primary) / 0.3),
  300: oklch(var(--primary) / 0.4),
  400: oklch(var(--primary) / 0.6),
  500: oklch(var(--primary)),
  600: oklch(var(--primary) / 0.8),
  700: oklch(var(--primary) / 0.7),
  800: oklch(var(--primary) / 0.6),
  900: oklch(var(--primary) / 0.5),
  A100: oklch(var(--primary) / 0.2),
  A200: oklch(var(--primary) / 0.4),
  A400: oklch(var(--primary) / 0.6),
  A700: oklch(var(--primary) / 0.7),
  contrast: (

```bash

50: oklch(var(--primary-foreground)),
100: oklch(var(--primary-foreground)),
200: oklch(var(--primary-foreground)),
300: oklch(var(--primary-foreground)),
400: oklch(var(--primary-foreground)),
500: oklch(var(--primary-foreground)),
600: oklch(var(--primary-foreground)),
700: oklch(var(--primary-foreground)),
800: oklch(var(--primary-foreground)),
900: oklch(var(--primary-foreground)),
A100: oklch(var(--primary-foreground)),
A200: oklch(var(--primary-foreground)),
A400: oklch(var(--primary-foreground)),
A700: oklch(var(--primary-foreground))

```bash

  )
));

$theme: mat.define-light-theme((
  color: (

```bash

primary: $custom-primary,
accent: $custom-primary,
warn: mat.define-palette(mat.$red-palette)

```bash

  ),
  typography: mat.define-typography-config(),
  density: 0
));

@include mat.all-component-themes($theme);

```bash

// styles/material-theme.scss
@use '@angular/material' as mat;

// Define custom palettes using our design tokens
$custom-primary: mat.define-palette((
  50: oklch(var(--primary) / 0.1),
  100: oklch(var(--primary) / 0.2),
  200: oklch(var(--primary) / 0.3),
  300: oklch(var(--primary) / 0.4),
  400: oklch(var(--primary) / 0.6),
  500: oklch(var(--primary)),
  600: oklch(var(--primary) / 0.8),
  700: oklch(var(--primary) / 0.7),
  800: oklch(var(--primary) / 0.6),
  900: oklch(var(--primary) / 0.5),
  A100: oklch(var(--primary) / 0.2),
  A200: oklch(var(--primary) / 0.4),
  A400: oklch(var(--primary) / 0.6),
  A700: oklch(var(--primary) / 0.7),
  contrast: (

```bash

50: oklch(var(--primary-foreground)),
100: oklch(var(--primary-foreground)),
200: oklch(var(--primary-foreground)),
300: oklch(var(--primary-foreground)),
400: oklch(var(--primary-foreground)),
500: oklch(var(--primary-foreground)),
600: oklch(var(--primary-foreground)),
700: oklch(var(--primary-foreground)),
800: oklch(var(--primary-foreground)),
900: oklch(var(--primary-foreground)),
A100: oklch(var(--primary-foreground)),
A200: oklch(var(--primary-foreground)),
A400: oklch(var(--primary-foreground)),
A700: oklch(var(--primary-foreground))

```bash

  )
));

$theme: mat.define-light-theme((
  color: (

```bash

primary: $custom-primary,
accent: $custom-primary,
warn: mat.define-palette(mat.$red-palette)

```bash

  ),
  typography: mat.define-typography-config(),
  density: 0
));

@include mat.all-component-themes($theme);

```bash

### State Management


- Use Angular Signals for reactive state
- Use computed signals for derived state
- Keep state management simple - avoid complex libraries initially
- Use signals over RxJS for simple state

### Authentication Patterns


- Use AWS Amplify for Cognito integration
- HTTP interceptor for automatic token injection
- Auth service with proper error handling
- Protected routes with guards

### Styling Patterns


#### Design System + Material + Tailwind Combination


```html

### State Management


- Use Angular Signals for reactive state
- Use computed signals for derived state
- Keep state management simple - avoid complex libraries initially
- Use signals over RxJS for simple state

### Authentication Patterns


- Use AWS Amplify for Cognito integration
- HTTP interceptor for automatic token injection
- Auth service with proper error handling
- Protected routes with guards

### Styling Patterns


#### Design System + Material + Tailwind Combination


```html
<!-- Card with Design System tokens -->
<mat-card class="p-6 bg-card border border-border shadow-md rounded-lg hover:shadow-lg transition-shadow">
  <mat-card-header class="mb-4">

```bash

<mat-card-title class="text-2xl font-bold text-card-foreground">
  {{ title }}
</mat-card-title>
<mat-card-subtitle class="text-muted-foreground">
  {{ subtitle }}
</mat-card-subtitle>

```bash

  </mat-card-header>
</mat-card>

<!-- Form with design system colors -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  <mat-form-field class="w-full">

```bash

<mat-label>Field</mat-label>
<input matInput class="text-lg bg-input border-border">

```bash

  </mat-form-field>
</div>

<!-- Buttons with semantic colors -->
<div class="flex gap-3">
  <button mat-raised-button

```bash

class="bg-primary text-primary-foreground hover:bg-primary/90 px-6 py-2 rounded-md transition-colors">
y Action

```bash

  </button>
  <button mat-button

```bash

class="text-muted-foreground hover:text-foreground px-4 py-2 rounded-md transition-colors">

```bash

  </button>
  <button mat-raised-button

```bash

class="bg-destructive text-destructive-foreground hover:bg-destructive/90 px-4 py-2 rounded-md transition-colors">

```bash

  </button>
</div>

<!-- Sidebar with design system -->
<aside class="bg-sidebar border-r border-sidebar-border w-64 h-screen">
  <nav class="p-4 space-y-2">

```bash

<a class="flex items-center px-3 py-2 rounded-md bg-sidebar-accent text-sidebar-accent-foreground hover:bg-sidebar-accent/80">
  <mat-icon class="mr-3">dashboard</mat-icon>
  Dashboard
</a>
<a class="flex items-center px-3 py-2 rounded-md text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
  <mat-icon class="mr-3">business</mat-icon>
  Bedarfs
</a>

```bash

  </nav>
</aside>

```bash

<!-- Card with Design System tokens -->
<mat-card class="p-6 bg-card border border-border shadow-md rounded-lg hover:shadow-lg transition-shadow">
  <mat-card-header class="mb-4">

```bash

<mat-card-title class="text-2xl font-bold text-card-foreground">
  {{ title }}
</mat-card-title>
<mat-card-subtitle class="text-muted-foreground">
  {{ subtitle }}
</mat-card-subtitle>

```bash

  </mat-card-header>
</mat-card>

<!-- Form with design system colors -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  <mat-form-field class="w-full">

```bash

<mat-label>Field</mat-label>
<input matInput class="text-lg bg-input border-border">

```bash

  </mat-form-field>
</div>

<!-- Buttons with semantic colors -->
<div class="flex gap-3">
  <button mat-raised-button

```bash

class="bg-primary text-primary-foreground hover:bg-primary/90 px-6 py-2 rounded-md transition-colors">
y Action

```bash

  </button>
  <button mat-button

```bash

class="text-muted-foreground hover:text-foreground px-4 py-2 rounded-md transition-colors">

```bash

  </button>
  <button mat-raised-button

```bash

class="bg-destructive text-destructive-foreground hover:bg-destructive/90 px-4 py-2 rounded-md transition-colors">

```bash

  </button>
</div>

<!-- Sidebar with design system -->
<aside class="bg-sidebar border-r border-sidebar-border w-64 h-screen">
  <nav class="p-4 space-y-2">

```bash

<a class="flex items-center px-3 py-2 rounded-md bg-sidebar-accent text-sidebar-accent-foreground hover:bg-sidebar-accent/80">
  <mat-icon class="mr-3">dashboard</mat-icon>
  Dashboard
</a>
<a class="flex items-center px-3 py-2 rounded-md text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
  <mat-icon class="mr-3">business</mat-icon>
  Bedarfs
</a>

```bash

  </nav>
</aside>

```bash

### Responsive Design Patterns


```html

### Responsive Design Patterns


```html

<!-- Mobile-first responsive layout -->
<div class="container mx-auto px-4 py-6">
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">

```bash

<!-- Cards -->

```bash

  </div>
</div>

<!-- Responsive navigation -->
<nav class="bg-white shadow-lg">
  <div class="max-w-7xl mx-auto px-4">

```bash

<div class="flex justify-between h-16">
  <div class="flex items-center">
    <span class="text-xl font-bold text-gray-800">Bau</span>
  </div>

```bash

```bash

<!-- Mobile menu button -->
<div class="md:hidden flex items-center">
  <button mat-icon-button (click)="toggleMobileMenu()">
    <mat-icon>menu</mat-icon>
  </button>
</div>

```bash

```bash

<!-- Desktop menu -->
<div class="hidden md:flex items-center space-x-4">
  <a mat-button routerLink="/bedarf" class="text-gray-700 hover:text-gray-900">
    Bedarf
  </a>
  <a mat-button routerLink="/betrieb" class="text-gray-700 hover:text-gray-900">
    Betrieb
  </a>
</div>
div>

```bash

  </div>
</nav>

```bash

<!-- Mobile-first responsive layout -->
<div class="container mx-auto px-4 py-6">
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">

```bash

<!-- Cards -->

```bash

  </div>
</div>

<!-- Responsive navigation -->
<nav class="bg-white shadow-lg">
  <div class="max-w-7xl mx-auto px-4">

```bash

<div class="flex justify-between h-16">
  <div class="flex items-center">
    <span class="text-xl font-bold text-gray-800">Bau</span>
  </div>

```bash

```bash

<!-- Mobile menu button -->
<div class="md:hidden flex items-center">
  <button mat-icon-button (click)="toggleMobileMenu()">
    <mat-icon>menu</mat-icon>
  </button>
</div>

```bash

```bash

<!-- Desktop menu -->
<div class="hidden md:flex items-center space-x-4">
  <a mat-button routerLink="/bedarf" class="text-gray-700 hover:text-gray-900">
    Bedarf
  </a>
  <a mat-button routerLink="/betrieb" class="text-gray-700 hover:text-gray-900">
    Betrieb
  </a>
</div>
div>

```bash

  </div>
</nav>

```bash

---

## Architecture Patterns


### Hexagonal Architecture


- **Domain**: Business entities and logic (no dependencies)
- **Application**: Use cases and orchestration
- **Adapters**: External interfaces (REST, database, external APIs)
- **Ports**: Interfaces defining contracts

### Package Structure


```bash

---

## Architecture Patterns


### Hexagonal Architecture


- **Domain**: Business entities and logic (no dependencies)
- **Application**: Use cases and orchestration
- **Adapters**: External interfaces (REST, database, external APIs)
- **Ports**: Interfaces defining contracts

### Package Structure


```bash

backend/src/main/java/com/bau/
├── adapter/
│   ├── in/                    # Driving adapters
│   │   ├── web/              # REST controllers + DTOs
│   │   └── event/            # Event listeners
│   └── out/                  # Driven adapters
│       ├── persistence/      # Database repositories + entities
│       ├── external/         # External API clients
│       └── messaging/        # Message brokers
├── application/
│   ├── domain/              # Domain entities + business logic
│   ├── usecase/             # Use case implementations
│   └── port/                # Port interfaces
│       ├── in/              # Input ports (use case interfaces)
│       └── out/             # Output ports (repository interfaces)
└── shared/

```bash

├── config/              # Configuration classes
├── util/                # Utility classes
└── exception/           # Custom exceptions

```bash

```bash

backend/src/main/java/com/bau/
├── adapter/
│   ├── in/                    # Driving adapters
│   │   ├── web/              # REST controllers + DTOs
│   │   └── event/            # Event listeners
│   └── out/                  # Driven adapters
│       ├── persistence/      # Database repositories + entities
│       ├── external/         # External API clients
│       └── messaging/        # Message brokers
├── application/
│   ├── domain/              # Domain entities + business logic
│   ├── usecase/             # Use case implementations
│   └── port/                # Port interfaces
│       ├── in/              # Input ports (use case interfaces)
│       └── out/             # Output ports (repository interfaces)
└── shared/

```bash

├── config/              # Configuration classes
├── util/                # Utility classes
└── exception/           # Custom exceptions

```bash

```bash

### Entity & DTO Separation


- **Domain Entities**: Business logic, no JPA annotations
- **JPA Entities**: Database representation only
- **DTOs**: API contracts (request/response)
- **Mappers**: Convert between layers

### Naming Conventions


- **Domain Objects**: No suffix (e.g., `Bedarf`, `Betrieb`)
- **JPA Entities**: `Entity` suffix (e.g., `BedarfEntity`)
- **DTOs**: No suffix (e.g., `CreateBedarfRequest`, `BedarfResponse`)
- **Mappers**: Located in respective adapters
- **Use Cases**: `{Action}{Entity}UseCase` (e.g., `CreateBedarfUseCase`)

---

## Testing Standards


### Backend Testing


#### Unit Test Template


```java

### Entity & DTO Separation


- **Domain Entities**: Business logic, no JPA annotations
- **JPA Entities**: Database representation only
- **DTOs**: API contracts (request/response)
- **Mappers**: Convert between layers

### Naming Conventions


- **Domain Objects**: No suffix (e.g., `Bedarf`, `Betrieb`)
- **JPA Entities**: `Entity` suffix (e.g., `BedarfEntity`)
- **DTOs**: No suffix (e.g., `CreateBedarfRequest`, `BedarfResponse`)
- **Mappers**: Located in respective adapters
- **Use Cases**: `{Action}{Entity}UseCase` (e.g., `CreateBedarfUseCase`)

---

## Testing Standards


### Backend Testing


#### Unit Test Template


```java
@ExtendWith(MockitoExtension.class)
class {Action}{EntityName}UseCaseTest {

```bash

@Mock
private {EntityName}Repository {entityName}Repository;

```bash

```bash

@Mock
private {EntityName}Mapper {entityName}Mapper;

```bash

```bash

@InjectMocks
private {Action}{EntityName}UseCase useCase;

```bash

```bash

@Test
void should{Action}{EntityName}_whenValidRequest() {
    // Given
    Create{EntityName}Request request = new Create{EntityName}Request();
    // setup request

```bash

```bash

{EntityName} {entityName} = new {EntityName}();
// setup domain object

```bash

```bash

{EntityName} saved{EntityName} = new {EntityName}();
saved{EntityName}.setId(UUID.randomUUID());

```bash

```bash

when({entityName}Mapper.toDomain(request)).thenReturn({entityName});
when({entityName}Repository.save({entityName})).thenReturn(saved{EntityName});

```bash

```bash

// When
Optional<{EntityName}> result = useCase.execute(request);

```bash

```bash

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(saved{EntityName}.getId());
verify({entityName}Repository).save({entityName});
}

```bash

}

```bash

@ExtendWith(MockitoExtension.class)
class {Action}{EntityName}UseCaseTest {

```bash

@Mock
private {EntityName}Repository {entityName}Repository;

```bash

```bash

@Mock
private {EntityName}Mapper {entityName}Mapper;

```bash

```bash

@InjectMocks
private {Action}{EntityName}UseCase useCase;

```bash

```bash

@Test
void should{Action}{EntityName}_whenValidRequest() {
    // Given
    Create{EntityName}Request request = new Create{EntityName}Request();
    // setup request

```bash

```bash

{EntityName} {entityName} = new {EntityName}();
// setup domain object

```bash

```bash

{EntityName} saved{EntityName} = new {EntityName}();
saved{EntityName}.setId(UUID.randomUUID());

```bash

```bash

when({entityName}Mapper.toDomain(request)).thenReturn({entityName});
when({entityName}Repository.save({entityName})).thenReturn(saved{EntityName});

```bash

```bash

// When
Optional<{EntityName}> result = useCase.execute(request);

```bash

```bash

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(saved{EntityName}.getId());
verify({entityName}Repository).save({entityName});
}

```bash

}

```bash

#### Testing Guidelines


- Unit tests for all business logic
- Integration tests for APIs
- Mock external dependencies
- Test both success and failure scenarios
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)

### Frontend Testing


#### Component Test Template


```typescript

#### Testing Guidelines


- Unit tests for all business logic
- Integration tests for APIs
- Mock external dependencies
- Test both success and failure scenarios
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)

### Frontend Testing


#### Component Test Template


```typescript
describe('{ComponentName}Component', () => {
  let component: {ComponentName}Component;
  let fixture: ComponentFixture<{ComponentName}Component>;
  let {serviceName}: jasmine.SpyObj<{ServiceName}>;

  beforeEach(async () => {

```bash

const spy = jasmine.createSpyObj('{ServiceName}', ['get{Data}']);

```bash

```bash

await TestBed.configureTestingModule({
  imports: [{ComponentName}Component],
  providers: [
    { provide: {ServiceName}, useValue: spy }
  ]
}).compileComponents();

```bash

```bash

fixture = TestBed.createComponent({ComponentName}Component);
component = fixture.componentInstance;
{serviceName} = TestBed.inject({ServiceName}) as jasmine.SpyObj<{ServiceName}>;

```bash

  });

  it('should create', () => {

```bash

expect(component).toBeTruthy();

```bash

  });

  it('should load data on init', async () => {

```bash

// Given
const mockData = [{ id: '1', name: 'Test' }];
{serviceName}.get{Data}.and.returnValue(Promise.resolve(mockData));

```bash

```bash

// When
fixture.detectChanges();
await fixture.whenStable();

```bash

```bash

// Then
expect({serviceName}.get{Data}).toHaveBeenCalled();
expect(component.{data}()).toEqual(mockData);

```bash

  });
});

```bash

describe('{ComponentName}Component', () => {
  let component: {ComponentName}Component;
  let fixture: ComponentFixture<{ComponentName}Component>;
  let {serviceName}: jasmine.SpyObj<{ServiceName}>;

  beforeEach(async () => {

```bash

const spy = jasmine.createSpyObj('{ServiceName}', ['get{Data}']);

```bash

```bash

await TestBed.configureTestingModule({
  imports: [{ComponentName}Component],
  providers: [
    { provide: {ServiceName}, useValue: spy }
  ]
}).compileComponents();

```bash

```bash

fixture = TestBed.createComponent({ComponentName}Component);
component = fixture.componentInstance;
{serviceName} = TestBed.inject({ServiceName}) as jasmine.SpyObj<{ServiceName}>;

```bash

  });

  it('should create', () => {

```bash

expect(component).toBeTruthy();

```bash

  });

  it('should load data on init', async () => {

```bash

// Given
const mockData = [{ id: '1', name: 'Test' }];
{serviceName}.get{Data}.and.returnValue(Promise.resolve(mockData));

```bash

```bash

// When
fixture.detectChanges();
await fixture.whenStable();

```bash

```bash

// Then
expect({serviceName}.get{Data}).toHaveBeenCalled();
expect(component.{data}()).toEqual(mockData);

```bash

  });
});

```bash

#### Testing Guidelines


- Unit tests for components and services
- Integration tests for user workflows
- E2E tests for critical paths
- Test responsive design and accessibility
- Mock HTTP requests
- Test error scenarios

---

## Code Quality Tools


### Backend


- **Maven**: Build, test, dependency management
- **Spotless**: Code formatting
- **Checkstyle**: Code style enforcement
- **JUnit 5**: Unit testing
- **Mockito**: Mocking framework

### Frontend


- **ESLint**: Code linting
- **Prettier**: Code formatting
- **Jasmine**: Unit testing
- **Karma**: Test runner
- **Playwright**: E2E testing

---

## Business Domain Rules


### Bedarf Validation


- Date range must be valid (start < end)
- At least one worker type must be specified
- Address is required
- Betrieb ID must be valid

### Betrieb Validation


- Name is required
- Address is required
- Email must be valid format
- Phone number must be valid format

### User Management


- Email must be unique
- Password must meet security requirements
- User roles must be valid
- Account status must be tracked

#### User Roles


The system supports two user roles:
- **ADMIN**: System administrators with full access to manage the platform
- **BETRIEB**: Construction companies that can create/manage Bedarf and view other companies' Bedarf

#### Role-Based Access Control


- **ADMIN role**: Full system access, user management, system oversight
- **BETRIEB role**: Limited to own company data, can create/update/delete own Bedarf, view all Bedarf for application

---

## Related Documentation


- [Architecture Decisions](../09-architecture-decisions/)
- [Backend Architecture](../05-building-blocks/backend-architecture.md)
- [System Overview](../05-building-blocks/system-overview.md)
- [Development Guide](development.md)

#### Testing Guidelines


- Unit tests for components and services
- Integration tests for user workflows
- E2E tests for critical paths
- Test responsive design and accessibility
- Mock HTTP requests
- Test error scenarios

---

## Code Quality Tools


### Backend


- **Maven**: Build, test, dependency management
- **Spotless**: Code formatting
- **Checkstyle**: Code style enforcement
- **JUnit 5**: Unit testing
- **Mockito**: Mocking framework

### Frontend


- **ESLint**: Code linting
- **Prettier**: Code formatting
- **Jasmine**: Unit testing
- **Karma**: Test runner
- **Playwright**: E2E testing

---

## Business Domain Rules


### Bedarf Validation


- Date range must be valid (start < end)
- At least one worker type must be specified
- Address is required
- Betrieb ID must be valid

### Betrieb Validation


- Name is required
- Address is required
- Email must be valid format
- Phone number must be valid format

### User Management


- Email must be unique
- Password must meet security requirements
- User roles must be valid
- Account status must be tracked

#### User Roles


The system supports two user roles:
- **ADMIN**: System administrators with full access to manage the platform
- **BETRIEB**: Construction companies that can create/manage Bedarf and view other companies' Bedarf

#### Role-Based Access Control


- **ADMIN role**: Full system access, user management, system oversight
- **BETRIEB role**: Limited to own company data, can create/update/delete own Bedarf, view all Bedarf for application

---

## Related Documentation


- [Architecture Decisions](../09-architecture-decisions/)
- [Backend Architecture](../05-building-blocks/backend-architecture.md)
- [System Overview](../05-building-blocks/system-overview.md)
- [Development Guide](development.md)
