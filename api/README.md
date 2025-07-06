# Bau Platform API Documentation

This directory contains the OpenAPI specification for the Bau Platform API, organized into multiple files for better maintainability.

## File Structure


```bash
api/
├── main.yaml              # Main OpenAPI file with references
├── paths/                 # API endpoint definitions
│   ├── auth.yaml         # Authentication endpoints
│   ├── bedarfe.yaml      # Bedarf (demand) endpoints - all operations
│   ├── betriebe.yaml     # Betrieb (company) endpoints - all operations
│   ├── users.yaml        # User management endpoints
│   └── index.yaml        # Index/health endpoints
├── schemas/              # Data model definitions
│   ├── auth.yaml         # Authentication schemas
│   ├── bedarfe.yaml      # Bedarf schemas
│   ├── betriebe.yaml     # Betrieb schemas
│   ├── users.yaml        # User schemas
│   └── common.yaml       # Shared/common schemas
└── README.md             # This file

```bash

api/
├── main.yaml              # Main OpenAPI file with references
├── paths/                 # API endpoint definitions
│   ├── auth.yaml         # Authentication endpoints
│   ├── bedarfe.yaml      # Bedarf (demand) endpoints - all operations
│   ├── betriebe.yaml     # Betrieb (company) endpoints - all operations
│   ├── users.yaml        # User management endpoints
│   └── index.yaml        # Index/health endpoints
├── schemas/              # Data model definitions
│   ├── auth.yaml         # Authentication schemas
│   ├── bedarfe.yaml      # Bedarf schemas
│   ├── betriebe.yaml     # Betrieb schemas
│   ├── users.yaml        # User schemas
│   └── common.yaml       # Shared/common schemas
└── README.md             # This file

```bash

## Usage


### Development


1. **Main Entry Point**: Use `main.yaml` as the primary OpenAPI file
2. **Local Development**: The API runs on `http://localhost:8080/api/v1`
3. **Swagger UI**: Available at `http://localhost:8080/swagger-ui.html`

### Tools


- **Swagger Editor**: Use for editing and validation
- **OpenAPI Generator**: Generate client code
- **Postman**: Import `main.yaml` for API testing

## API Organization


### Authentication (`/auth`)


- **Profile**: Get current user profile
- **Logout**: User logout (handled by AWS Cognito)

### Bedarf Management (`/bedarf`)


- **CRUD Operations**: Create, read, update, delete bedarf
- **Status Management**: Update bedarf status
- **Filtering**: By status, company, date range
- **Pagination**: Standard pagination support

### Company Management (`/betrieb`)


- **CRUD Operations**: Create, read, update, delete companies
- **Profile**: Get current user's company profile
- **Filtering**: By status

### User Management (`/users`)


- **CRUD Operations**: Create, read, update, delete users
- **Profile Management**: Get/update current user profile
- **Password Management**: Change password
- **Filtering**: By company, role

## Schema Organization


### Common Schemas (`common.yaml`)


- `ErrorResponse`: Standard error response format
- `PaginationParameters`: Common pagination parameters
- `PaginationResponse`: Standard pagination response

### Domain-Specific Schemas


Each domain has its own schema file with:
- **Response schemas**: Data returned by endpoints
- **Request schemas**: Data accepted by endpoints
- **List schemas**: Paginated list responses

## Benefits of Split Structure


### 1. **Maintainability**- Easier to find and modify specific endpoints

- Reduced merge conflicts in team development
- Clear separation of concerns

### 2.**Reusability**- Common schemas shared across endpoints

- Consistent patterns for similar operations
- DRY principle applied

### 3.**Scalability**- Easy to add new domains without cluttering main file

- Modular structure supports team development
- Clear boundaries between different API areas

### 4.**Tooling Support**- Better IDE support with smaller files

- Faster parsing and validation
- Easier version control

## Best Practices


### 1.**File Naming**- Use descriptive, domain-specific names

- Follow consistent naming conventions
- Group related endpoints together

### 2.**Schema References**- Use relative paths for references

- Keep schemas close to their usage
- Avoid circular dependencies

### 3.**Documentation**- Include examples in schemas

- Provide clear descriptions
- Use consistent formatting

### 4.**Versioning**- All files should use the same OpenAPI version

- Maintain backward compatibility
- Document breaking changes

## Development Workflow


### Adding New Endpoints


1.**Create/Update Path File**: Add endpoint to appropriate `paths/` file
1. **Create/Update Schemas**: Add schemas to appropriate `schemas/` file
2. **Update Main File**: Ensure references are correct in `main.yaml`
3. **Test**: Validate with Swagger tools

### Adding New Domains


1. **Create Path File**: `paths/newdomain.yaml`
2. **Create Schema File**: `schemas/newdomain.yaml`
3. **Update Main File**: Add references to `main.yaml`
4. **Document**: Update this README

## Validation


### Local Validation


```bash

## Usage


### Development


1. **Main Entry Point**: Use `main.yaml` as the primary OpenAPI file
2. **Local Development**: The API runs on `http://localhost:8080/api/v1`
3. **Swagger UI**: Available at `http://localhost:8080/swagger-ui.html`

### Tools


- **Swagger Editor**: Use for editing and validation
- **OpenAPI Generator**: Generate client code
- **Postman**: Import `main.yaml` for API testing

## API Organization


### Authentication (`/auth`)


- **Profile**: Get current user profile
- **Logout**: User logout (handled by AWS Cognito)

### Bedarf Management (`/bedarf`)


- **CRUD Operations**: Create, read, update, delete bedarf
- **Status Management**: Update bedarf status
- **Filtering**: By status, company, date range
- **Pagination**: Standard pagination support

### Company Management (`/betrieb`)


- **CRUD Operations**: Create, read, update, delete companies
- **Profile**: Get current user's company profile
- **Filtering**: By status

### User Management (`/users`)


- **CRUD Operations**: Create, read, update, delete users
- **Profile Management**: Get/update current user profile
- **Password Management**: Change password
- **Filtering**: By company, role

## Schema Organization


### Common Schemas (`common.yaml`)


- `ErrorResponse`: Standard error response format
- `PaginationParameters`: Common pagination parameters
- `PaginationResponse`: Standard pagination response

### Domain-Specific Schemas


Each domain has its own schema file with:
- **Response schemas**: Data returned by endpoints
- **Request schemas**: Data accepted by endpoints
- **List schemas**: Paginated list responses

## Benefits of Split Structure


### 1. **Maintainability**- Easier to find and modify specific endpoints

- Reduced merge conflicts in team development
- Clear separation of concerns

### 2.**Reusability**- Common schemas shared across endpoints

- Consistent patterns for similar operations
- DRY principle applied

### 3.**Scalability**- Easy to add new domains without cluttering main file

- Modular structure supports team development
- Clear boundaries between different API areas

### 4.**Tooling Support**- Better IDE support with smaller files

- Faster parsing and validation
- Easier version control

## Best Practices


### 1.**File Naming**- Use descriptive, domain-specific names

- Follow consistent naming conventions
- Group related endpoints together

### 2.**Schema References**- Use relative paths for references

- Keep schemas close to their usage
- Avoid circular dependencies

### 3.**Documentation**- Include examples in schemas

- Provide clear descriptions
- Use consistent formatting

### 4.**Versioning**- All files should use the same OpenAPI version

- Maintain backward compatibility
- Document breaking changes

## Development Workflow


### Adding New Endpoints


1.**Create/Update Path File**: Add endpoint to appropriate `paths/` file
1. **Create/Update Schemas**: Add schemas to appropriate `schemas/` file
2. **Update Main File**: Ensure references are correct in `main.yaml`
3. **Test**: Validate with Swagger tools

### Adding New Domains


1. **Create Path File**: `paths/newdomain.yaml`
2. **Create Schema File**: `schemas/newdomain.yaml`
3. **Update Main File**: Add references to `main.yaml`
4. **Document**: Update this README

## Validation


### Local Validation


```bash

## Using swagger-cli


swagger-cli validate api/main.yaml

## Using openapi-validator


openapi-validator api/main.yaml

```bash

## Using swagger-cli


swagger-cli validate api/main.yaml

## Using openapi-validator


openapi-validator api/main.yaml

```bash

### IDE Integration


- **VS Code**: Install OpenAPI extension
- **IntelliJ**: Built-in OpenAPI support
- **Eclipse**: OpenAPI plugins available

## Security


- **Authentication**: AWS Cognito JWT tokens
- **Authorization**: Role-based access control
- **HTTPS**: Required for production
- **Rate Limiting**: Implemented at API Gateway level

## Error Handling


All endpoints follow consistent error response format:
- **400**: Bad Request (validation errors)
- **401**: Unauthorized (missing/invalid token)
- **403**: Forbidden (insufficient permissions)
- **404**: Not Found
- **500**: Internal Server Error

## Examples


### Creating a Bedarf


```bash

### IDE Integration


- **VS Code**: Install OpenAPI extension
- **IntelliJ**: Built-in OpenAPI support
- **Eclipse**: OpenAPI plugins available

## Security


- **Authentication**: AWS Cognito JWT tokens
- **Authorization**: Role-based access control
- **HTTPS**: Required for production
- **Rate Limiting**: Implemented at API Gateway level

## Error Handling


All endpoints follow consistent error response format:
- **400**: Bad Request (validation errors)
- **401**: Unauthorized (missing/invalid token)
- **403**: Forbidden (insufficient permissions)
- **404**: Not Found
- **500**: Internal Server Error

## Examples


### Creating a Bedarf


```bash
curl -X POST "http://localhost:8080/api/v1/bedarf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{

```bash

"holzbauAnzahl": 2,
"zimmermannAnzahl": 1,
"datumVon": "2024-02-01",
"datumBis": "2024-02-15",
"adresse": "Dorfstrasse 1234, 9472 Grabs",
"mitWerkzeug": true,
"mitFahrzeug": false

```bash

  }'

```bash

curl -X POST "http://localhost:8080/api/v1/bedarf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{

```bash

"holzbauAnzahl": 2,
"zimmermannAnzahl": 1,
"datumVon": "2024-02-01",
"datumBis": "2024-02-15",
"adresse": "Dorfstrasse 1234, 9472 Grabs",
"mitWerkzeug": true,
"mitFahrzeug": false

```bash

  }'

```bash

### Getting User Profile


```bash

### Getting User Profile


```bash

curl -X GET "http://localhost:8080/api/v1/auth/profile" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

```bash

curl -X GET "http://localhost:8080/api/v1/auth/profile" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

```bash

## Support


For questions or issues with the API specification:
- Check the main project documentation
- Review the technical architecture docs
- Contact the development team

## Support


For questions or issues with the API specification:
- Check the main project documentation
- Review the technical architecture docs
- Contact the development team
