# AWS Cognito Setup

## Overview


AWS Cognito provides authentication and user management for the Bau platform.

## Quick Setup


### 1. Create User Pool


```bash
aws cognito-idp create-user-pool \
--pool-name bau-dev-users \
--policies '{"PasswordPolicy":{"MinimumLength":8,"RequireUppercase":true,"RequireLowercase":true,"RequireNumbers":true,"RequireSymbols":true}}' \
--auto-verified-attributes email

```bash

aws cognito-idp create-user-pool \
--pool-name bau-dev-users \
--policies '{"PasswordPolicy":{"MinimumLength":8,"RequireUppercase":true,"RequireLowercase":true,"RequireNumbers":true,"RequireSymbols":true}}' \
--auto-verified-attributes email

```bash

### 2. Create User Pool Client


```bash

### 2. Create User Pool Client


```bash

aws cognito-idp create-user-pool-client \
--user-pool-id YOUR_USER_POOL_ID \
--client-name bau-dev-client \
--no-generate-secret \
--explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH

```bash

aws cognito-idp create-user-pool-client \
--user-pool-id YOUR_USER_POOL_ID \
--client-name bau-dev-client \
--no-generate-secret \
--explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH

```bash

### 3. Environment Variables


```env

### 3. Environment Variables


```env

## Backend


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

## Frontend


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com

```bash

## Backend


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

## Frontend


AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com

```bash

## Frontend Integration


### Angular Configuration


```typescript

## Frontend Integration


### Angular Configuration


```typescript

// app.config.ts
import { Amplify } from 'aws-amplify';

Amplify.configure({
  Auth: {

```bash

Cognito: {
  userPoolId: 'eu-central-1_xxxxxxxxx',
  userPoolClientId: 'xxxxxxxxxxxxxxxxxxxxxxxxxx',
  loginWith: {
    email: true,
    username: false
  }
}

```bash

  }
});

```bash

// app.config.ts
import { Amplify } from 'aws-amplify';

Amplify.configure({
  Auth: {

```bash

Cognito: {
  userPoolId: 'eu-central-1_xxxxxxxxx',
  userPoolClientId: 'xxxxxxxxxxxxxxxxxxxxxxxxxx',
  loginWith: {
    email: true,
    username: false
  }
}

```bash

  }
});

```bash

### Authentication Service


```typescript

### Authentication Service


```typescript

import { Auth } from 'aws-amplify';

@Injectable({ providedIn: 'root' })
export class AuthService {
  async signIn(email: string, password: string) {

```bash

return await Auth.signIn(email, password);

```bash

  }

  async signOut() {

```bash

await Auth.signOut();

```bash

  }

  async getIdToken() {

```bash

const session = await Auth.currentSession();
return session.getIdToken().getJwtToken();

```bash

  }
}

```bash

import { Auth } from 'aws-amplify';

@Injectable({ providedIn: 'root' })
export class AuthService {
  async signIn(email: string, password: string) {

```bash

return await Auth.signIn(email, password);

```bash

  }

  async signOut() {

```bash

await Auth.signOut();

```bash

  }

  async getIdToken() {

```bash

const session = await Auth.currentSession();
return session.getIdToken().getJwtToken();

```bash

  }
}

```bash

## Backend Integration


### Spring Security Configuration


```java

## Backend Integration


### Spring Security Configuration


```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

```bash

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

```bash

```bash

return http.build();
}

```bash

}

```bash

@Configuration
@EnableWebSecurity
public class SecurityConfig {

```bash

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

```bash

```bash

return http.build();
}

```bash

}

```bash

### JWT Validation


```java

### JWT Validation


```java

@Component
public class JwtTokenProvider {

```bash

@Value("${aws.cognito.user-pool-id}")
private String userPoolId;

```bash

```bash

@Value("${aws.cognito.region}")
private String region;

```bash

```bash

public boolean validateToken(String token) {
    try {
        String issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
        Jwt jwt = jwtDecoder.decode(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

```bash

}

```bash

@Component
public class JwtTokenProvider {

```bash

@Value("${aws.cognito.user-pool-id}")
private String userPoolId;

```bash

```bash

@Value("${aws.cognito.region}")
private String region;

```bash

```bash

public boolean validateToken(String token) {
    try {
        String issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
        Jwt jwt = jwtDecoder.decode(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

```bash

}

```bash

## User Management


### Create User


```bash

## User Management


### Create User


```bash

aws cognito-idp admin-create-user \
--user-pool-id YOUR_USER_POOL_ID \
--username user@example.com \
--user-attributes Name=email,Value=user@example.com \
--temporary-password TempPass123!

```bash

aws cognito-idp admin-create-user \
--user-pool-id YOUR_USER_POOL_ID \
--username user@example.com \
--user-attributes Name=email,Value=user@example.com \
--temporary-password TempPass123!

```bash

### Set Password


```bash

### Set Password


```bash

aws cognito-idp admin-set-user-password \
--user-pool-id YOUR_USER_POOL_ID \
--username user@example.com \
--password NewPassword123! \
--permanent

```bash

aws cognito-idp admin-set-user-password \
--user-pool-id YOUR_USER_POOL_ID \
--username user@example.com \
--password NewPassword123! \
--permanent

```bash

## Groups and Roles


The system supports two user roles:
- **ADMIN**: System administrators with full platform access
- **BETRIEB**: Construction companies with limited access to their own data

### Create Groups


```bash

## Groups and Roles


The system supports two user roles:
- **ADMIN**: System administrators with full platform access
- **BETRIEB**: Construction companies with limited access to their own data

### Create Groups


```bash

## Create ADMIN group


aws cognito-idp create-group \
--user-pool-id YOUR_USER_POOL_ID \
--group-name ADMIN \
--description "System administrators"

## Create BETRIEB group


aws cognito-idp create-group \
--user-pool-id YOUR_USER_POOL_ID \
--group-name BETRIEB \
--description "Construction companies"

```bash

## Create ADMIN group


aws cognito-idp create-group \
--user-pool-id YOUR_USER_POOL_ID \
--group-name ADMIN \
--description "System administrators"

## Create BETRIEB group


aws cognito-idp create-group \
--user-pool-id YOUR_USER_POOL_ID \
--group-name BETRIEB \
--description "Construction companies"

```bash

### Add User to Group


```bash

### Add User to Group


```bash

## Add user to ADMIN group


aws cognito-idp admin-add-user-to-group \
--user-pool-id YOUR_USER_POOL_ID \
--username admin@example.com \
--group-name ADMIN

## Add user to BETRIEB group


aws cognito-idp admin-add-user-to-group \
--user-pool-id YOUR_USER_POOL_ID \
--username company@example.com \
--group-name BETRIEB

```bash

## Add user to ADMIN group


aws cognito-idp admin-add-user-to-group \
--user-pool-id YOUR_USER_POOL_ID \
--username admin@example.com \
--group-name ADMIN

## Add user to BETRIEB group


aws cognito-idp admin-add-user-to-group \
--user-pool-id YOUR_USER_POOL_ID \
--username company@example.com \
--group-name BETRIEB

```bash

## Testing


### Local Testing


```bash

## Testing


### Local Testing


```bash

## Create test user


aws cognito-idp admin-create-user \
--user-pool-id YOUR_USER_POOL_ID \
--username test@example.com \
--user-attributes Name=email,Value=test@example.com \
--temporary-password TestPass123!

## Set permanent password


aws cognito-idp admin-set-user-password \
--user-pool-id YOUR_USER_POOL_ID \
--username test@example.com \
--password TestPass123! \
--permanent

```bash

## Create test user


aws cognito-idp admin-create-user \
--user-pool-id YOUR_USER_POOL_ID \
--username test@example.com \
--user-attributes Name=email,Value=test@example.com \
--temporary-password TestPass123!

## Set permanent password


aws cognito-idp admin-set-user-password \
--user-pool-id YOUR_USER_POOL_ID \
--username test@example.com \
--password TestPass123! \
--permanent

```bash

## Related


- [Authentication Flow](06-runtime/authentication-flow.md) - Detailed flow
- [Development Guide](development.md) - Local setup
- [Deployment Guide](deployment.md) - Production deployment

## Related


- [Authentication Flow](06-runtime/authentication-flow.md) - Detailed flow
- [Development Guide](development.md) - Local setup
- [Deployment Guide](deployment.md) - Production deployment
