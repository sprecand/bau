# Authentication Flow

## Overview

AWS Cognito handles authentication with JWT tokens for API access.

## Flow

```

1. User Login → AWS Cognito → JWT Token
2. Frontend → API Request + JWT Token
3. Backend → Validate JWT → Extract User Info
4. Backend → Process Request → Return Response

```

1. User Login → AWS Cognito → JWT Token
2. Frontend → API Request + JWT Token
3. Backend → Validate JWT → Extract User Info
4. Backend → Process Request → Return Response

```

## Setup

### 1. AWS Cognito Configuration

```bash

## Setup

### 1. AWS Cognito Configuration

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

```

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

```

### 2. Environment Variables

```env

### 2. Environment Variables

```env

## Backend

AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

## Frontend

AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com

```

## Backend

AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_REGION=eu-central-1
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx

## Frontend

AWS_COGNITO_USER_POOL_ID=eu-central-1_xxxxxxxxx
AWS_COGNITO_CLIENT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxx
AWS_COGNITO_DOMAIN=bau-dev.auth.eu-central-1.amazoncognito.com

```

## Implementation

### Frontend (Angular + AWS Amplify)

```typescript

## Implementation

### Frontend (Angular + AWS Amplify)

```typescript

// app.config.ts
import { Amplify } from 'aws-amplify';

Amplify.configure({
  Auth: {

```

Cognito: {
  userPoolId: 'eu-central-1_xxxxxxxxx',
  userPoolClientId: 'xxxxxxxxxxxxxxxxxxxxxxxxxx',
  loginWith: {
    email: true,
    username: false
  }
}

```

  }
});

// auth.service.ts
import { Auth } from 'aws-amplify';

@Injectable({ providedIn: 'root' })
export class AuthService {
  async signIn(email: string, password: string) {

```

const user = await Auth.signIn(email, password);
return user;

```

  }

  async signOut() {

```

await Auth.signOut();

```

  }

  async getCurrentUser() {

```

return await Auth.getCurrentUser();

```

  }

  async getIdToken() {

```

const session = await Auth.currentSession();
return session.getIdToken().getJwtToken();

```

  }
}

// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

```

return from(this.authService.getIdToken()).pipe(
  switchMap(token => {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next.handle(authReq);
  })
);

```

  }
}

```

// app.config.ts
import { Amplify } from 'aws-amplify';

Amplify.configure({
  Auth: {

```

Cognito: {
  userPoolId: 'eu-central-1_xxxxxxxxx',
  userPoolClientId: 'xxxxxxxxxxxxxxxxxxxxxxxxxx',
  loginWith: {
    email: true,
    username: false
  }
}

```

  }
});

// auth.service.ts
import { Auth } from 'aws-amplify';

@Injectable({ providedIn: 'root' })
export class AuthService {
  async signIn(email: string, password: string) {

```

const user = await Auth.signIn(email, password);
return user;

```

  }

  async signOut() {

```

await Auth.signOut();

```

  }

  async getCurrentUser() {

```

return await Auth.getCurrentUser();

```

  }

  async getIdToken() {

```

const session = await Auth.currentSession();
return session.getIdToken().getJwtToken();

```

  }
}

// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

```

return from(this.authService.getIdToken()).pipe(
  switchMap(token => {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next.handle(authReq);
  })
);

```

  }
}

```

### Backend (Spring Boot)

```java

### Backend (Spring Boot)

```java

// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

```

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

```

```

return http.build();
}

```

```

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}

```

}

// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

```

@Override
protected void doFilterInternal(HttpServletRequest request,
                              HttpServletResponse response,
                              FilterChain filterChain) throws ServletException, IOException {

```

```

String token = extractToken(request);

```

```

if (token != null && jwtTokenProvider.validateToken(token)) {
    String username = jwtTokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

```

```

UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

```

```

SecurityContextHolder.getContext().setAuthentication(authentication);
}

```

```

filterChain.doFilter(request, response);
}

```

```

private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
}

```

}

// JwtTokenProvider.java
@Component
public class JwtTokenProvider {

```

@Value("${aws.cognito.user-pool-id}")
private String userPoolId;

```

```

@Value("${aws.cognito.region}")
private String region;

```

```

private final String issuerUrl;
private final JWKSource<SecurityContext> jwkSource;

```

```

public JwtTokenProvider() {
    this.issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
    this.jwkSource = new AwsCognitoJwkSource(issuerUrl);
}

```

```

public boolean validateToken(String token) {
    try {
        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
        Jwt jwt = jwtDecoder.decode(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

```

```

public String getUsernameFromToken(String token) {
    JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
}

```

}

```

// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

```

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

```

```

return http.build();
}

```

```

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}

```

}

// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

```

@Override
protected void doFilterInternal(HttpServletRequest request,
                              HttpServletResponse response,
                              FilterChain filterChain) throws ServletException, IOException {

```

```

String token = extractToken(request);

```

```

if (token != null && jwtTokenProvider.validateToken(token)) {
    String username = jwtTokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

```

```

UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

```

```

SecurityContextHolder.getContext().setAuthentication(authentication);
}

```

```

filterChain.doFilter(request, response);
}

```

```

private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
}

```

}

// JwtTokenProvider.java
@Component
public class JwtTokenProvider {

```

@Value("${aws.cognito.user-pool-id}")
private String userPoolId;

```

```

@Value("${aws.cognito.region}")
private String region;

```

```

private final String issuerUrl;
private final JWKSource<SecurityContext> jwkSource;

```

```

public JwtTokenProvider() {
    this.issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
    this.jwkSource = new AwsCognitoJwkSource(issuerUrl);
}

```

```

public boolean validateToken(String token) {
    try {
        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
        Jwt jwt = jwtDecoder.decode(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

```

```

public String getUsernameFromToken(String token) {
    JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
}

```

}

```

## User Profile Endpoint

```java

## User Profile Endpoint

```java

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

```

@GetMapping("/profile")
public ResponseEntity<UserProfileResponse> getProfile() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

```

```

// Get user details from Cognito or database
UserProfileResponse profile = userService.getUserProfile(username);

```

```

return ResponseEntity.ok(profile);
}

```

}

```

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

```

@GetMapping("/profile")
public ResponseEntity<UserProfileResponse> getProfile() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

```

```

// Get user details from Cognito or database
UserProfileResponse profile = userService.getUserProfile(username);

```

```

return ResponseEntity.ok(profile);
}

```

}

```

## Testing

### Frontend Testing

```typescript

## Testing

### Frontend Testing

```typescript

describe('AuthService', () => {
  it('should sign in user', async () => {

```

const result = await authService.signIn('test@example.com', 'password123');
expect(result.username).toBe('test@example.com');

```

  });
});

```

describe('AuthService', () => {
  it('should sign in user', async () => {

```

const result = await authService.signIn('test@example.com', 'password123');
expect(result.username).toBe('test@example.com');

```

  });
});

```

### Backend Testing

```java

### Backend Testing

```java

@SpringBootTest
class AuthControllerTest {

```

@Test
void shouldGetProfile() throws Exception {
    // Given
    String token = generateValidJwtToken();

```

```

// When
mockMvc.perform(get("/api/v1/auth/profile")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@example.com"));
}

```

}

```

@SpringBootTest
class AuthControllerTest {

```

@Test
void shouldGetProfile() throws Exception {
    // Given
    String token = generateValidJwtToken();

```

```

// When
mockMvc.perform(get("/api/v1/auth/profile")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@example.com"));
}

```

}

```

## Related

- [Development Guide](../development.md) - Setup instructions
- [Deployment Guide](../deployment.md) - Production deployment
- [ADR-002: AWS Cognito](../09-architecture-decisions/adr-002-aws-cognito-authentication.md) - Architecture decision
