# SonarQube Setup Guide

## Overview

This project uses SonarQube for continuous code quality analysis, covering both backend (Java/Spring Boot) and frontend (TypeScript/Angular) code.

## Features Analyzed

- **Code Quality**: Bugs, vulnerabilities, code smells
- **Code Coverage**: Test coverage for both backend and frontend
- **Security**: Security hotspots and vulnerabilities
- **Maintainability**: Technical debt and code complexity
- **Reliability**: Bug detection and error-prone patterns

## Code Coverage Strategy

### Focus on Business Logic

Our coverage strategy prioritizes meaningful tests over percentage targets:

- **Core Business Logic**: 80%+ coverage (Services, Domain objects)
- **Generated Code**: Excluded from coverage metrics
- **Configuration**: Excluded from coverage metrics
- **Data Transfer Objects**: Excluded from coverage metrics

### Exclusion Strategy

The following code is excluded from SonarQube analysis and coverage:

#### Generated Code
- OpenAPI generated DTOs (`com.bau.adapter.in.web.dto.*`)
- OpenAPI generated API interfaces (`com.bau.adapter.in.web.api.*`)
- Maven generated sources (`target/generated-sources/**`)

#### Infrastructure Code
- Spring Boot application main class (`*Application.java`)
- Configuration classes (`*Config.java`, `*Configuration.java`)
- JPA entity classes (`*Entity.java`)

#### Test and Build Artifacts
- Test files (`**/*.spec.ts`, `**/*Test.java`)
- Build directories (`target/**`, `node_modules/**`, `dist/**`)

### Current Coverage Results

As of the latest build:
- **Application Services**: 83% coverage ✅
- **Domain Objects**: 65-100% coverage ✅  
- **Repository Interfaces**: 100% coverage ✅
- **Use Case Interfaces**: 84% coverage ✅

This focused approach ensures high-quality tests for business-critical code while excluding noise from generated and infrastructure code.

## GitHub Actions Integration

The SonarQube analysis runs automatically in CI/CD pipeline after tests complete.

### Required GitHub Secrets

Add these secrets to your GitHub repository settings:

1. `SONAR_HOST_URL` - Your SonarQube server URL (e.g., `https://sonarcloud.io`)
2. `SONAR_TOKEN` - Your SonarQube authentication token

### Setting up SonarCloud (Recommended)

1. Go to [SonarCloud.io](https://sonarcloud.io)
2. Sign in with your GitHub account
3. Import your repository
4. Get your project key and token
5. Add the secrets to GitHub repository settings

## Local Development

### Running SonarQube Locally

1. **Start SonarQube with Docker:**
   ```bash
   docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
   ```

2. **Access SonarQube:**
   - URL: http://localhost:9000
   - Default credentials: admin/admin

3. **Create a project and get token:**
   - Create a new project manually
   - Generate a user token
   - Note the project key

4. **Run analysis:**
   ```bash
   # Backend analysis
   cd backend
   mvn clean verify sonar:sonar \
     -Dsonar.projectKey=your-project-key \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.token=your-token

   # Frontend coverage (run first)
   cd frontend
   npm run test:ci
   ```

## Configuration Files

### Maven Configuration (backend/pom.xml)
- JaCoCo plugin for Java code coverage with exclusions
- SonarQube Maven plugin for analysis

### Project Configuration (sonar-project.properties)
- Multi-language project setup
- Coverage report paths  
- Comprehensive exclusion patterns for generated code
- Quality gate settings
- Issue ignore rules for generated code

### Key Configuration Features

#### Coverage Exclusions
```properties
sonar.coverage.exclusions=\
  **/target/generated-sources/**,\
  backend/src/main/java/com/bau/adapter/in/web/dto/**,\
  backend/src/main/java/com/bau/adapter/in/web/api/**,\
  **/*Application.java,\
  **/*Config.java,\
  **/*Configuration.java
```

#### Issue Ignoring for Generated Code
- Cognitive complexity issues ignored in generated code
- "Too many parameters" warnings suppressed
- Unused import warnings excluded

## Quality Gates

The project is configured with quality gates that must pass:
- **Coverage**: Minimum test coverage thresholds for non-excluded code
- **Reliability**: No bugs in new code
- **Security**: No vulnerabilities in new code
- **Maintainability**: Technical debt ratio limits

## Troubleshooting

### Common Issues

1. **Missing coverage reports:**
   - Ensure tests run before SonarQube analysis
   - Check coverage report paths in configuration

2. **Authentication errors:**
   - Verify SONAR_TOKEN is valid and has project permissions
   - Check SONAR_HOST_URL format

3. **Analysis fails:**
   - Check SonarQube server connectivity
   - Verify project key exists
   - Check logs for specific error messages

4. **Unexpected low coverage:**
   - Verify exclusions are working properly
   - Check that generated code is being excluded
   - Review which packages are included in analysis

### Debug Commands

```bash
# Test SonarQube connectivity
curl -u your-token: https://sonarcloud.io/api/authentication/validate

# Check Maven SonarQube plugin
mvn help:describe -Dplugin=org.sonarsource.scanner.maven:sonar-maven-plugin

# Verbose SonarQube analysis
mvn sonar:sonar -X -Dsonar.verbose=true

# Check JaCoCo exclusions
mvn clean test jacoco:report
```

## Integration with IDEs

### IntelliJ IDEA
1. Install SonarLint plugin
2. Connect to SonarQube server
3. Bind project to SonarQube project

### VS Code
1. Install SonarLint extension
2. Configure connected mode
3. Set project binding

## Related Documentation
- [Coding Standards](coding-standards.md)
- [Development Guide](development.md)
- [Testing Strategy](../06-runtime/testing-strategy.md) 