# Authentication Flow

## Overview


AWS Cognito handles authentication with JWT tokens for API access.

## Flow


```bash

1. User Login → AWS Cognito → JWT Token
2. Frontend → API Request + JWT Token
3. Backend → Validate JWT → Extract User Info
4. Backend → Process Request → Return Response

```bash

1. User Login → AWS Cognito → JWT Token
2. Frontend → API Request + JWT Token
3. Backend → Validate JWT → Extract User Info
4. Backend → Process Request → Return Response

```bash

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

```bash

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

// auth.service.ts
import { Auth } from 'aws-amplify';

@Injectable({ providedIn: 'root' })
export class AuthService {
  async signIn(email: string, password: string) {

```bash

const user = await Auth.signIn(email, password);
return user;

```bash

  }

  async signOut() {

```bash

await Auth.signOut();

```bash

  }

  async getCurrentUser() {

```bash

return await Auth.getCurrentUser();

```bash

  }

  async getIdToken() {

```bash

const session = await Auth.currentSession();
return session.getIdToken().getJwtToken();

```bash

  }
}

// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

```bash

return from(this.authService.getIdToken()).pipe(
  switchMap(token => {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next.handle(authReq);
  })
);

```bash

  }
}

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

// auth.service.ts
import { Auth } from 'aws-amplify';

@Injectable({ providedIn: 'root' })
export class AuthService {
  async signIn(email: string, password: string) {

```bash

const user = await Auth.signIn(email, password);
return user;

```bash

  }

  async signOut() {

```bash

await Auth.signOut();

```bash

  }

  async getCurrentUser() {

```bash

return await Auth.getCurrentUser();

```bash

  }

  async getIdToken() {

```bash

const session = await Auth.currentSession();
return session.getIdToken().getJwtToken();

```bash

  }
}

// auth.interceptor.ts
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

```bash

return from(this.authService.getIdToken()).pipe(
  switchMap(token => {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next.handle(authReq);
  })
);

```bash

  }
}

```bash

### Backend (Spring Boot)


```java

### Backend (Spring Boot)


```java

// SecurityConfig.java
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

```bash

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}

```bash

}

// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

```bash

@Override
protected void doFilterInternal(HttpServletRequest request,
                              HttpServletResponse response,
                              FilterChain filterChain) throws ServletException, IOException {

```bash

```bash

String token = extractToken(request);

```bash

```bash

if (token != null && jwtTokenProvider.validateToken(token)) {
    String username = jwtTokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

```bash

```bash

UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

```bash

```bash

SecurityContextHolder.getContext().setAuthentication(authentication);
}

```bash

```bash

filterChain.doFilter(request, response);
}

```bash

```bash

private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
}

```bash

}

// JwtTokenProvider.java
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

private final String issuerUrl;
private final JWKSource<SecurityContext> jwkSource;

```bash

```bash

public JwtTokenProvider() {
    this.issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
    this.jwkSource = new AwsCognitoJwkSource(issuerUrl);
}

```bash

```bash

public boolean validateToken(String token) {
    try {
        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
        Jwt jwt = jwtDecoder.decode(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

```bash

```bash

public String getUsernameFromToken(String token) {
    JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
}

```bash

}

```bash

// SecurityConfig.java
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

```bash

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}

```bash

}

// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

```bash

@Override
protected void doFilterInternal(HttpServletRequest request,
                              HttpServletResponse response,
                              FilterChain filterChain) throws ServletException, IOException {

```bash

```bash

String token = extractToken(request);

```bash

```bash

if (token != null && jwtTokenProvider.validateToken(token)) {
    String username = jwtTokenProvider.getUsernameFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

```bash

```bash

UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

```bash

```bash

SecurityContextHolder.getContext().setAuthentication(authentication);
}

```bash

```bash

filterChain.doFilter(request, response);
}

```bash

```bash

private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7);
    }
    return null;
}

```bash

}

// JwtTokenProvider.java
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

private final String issuerUrl;
private final JWKSource<SecurityContext> jwkSource;

```bash

```bash

public JwtTokenProvider() {
    this.issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
    this.jwkSource = new AwsCognitoJwkSource(issuerUrl);
}

```bash

```bash

public boolean validateToken(String token) {
    try {
        JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
        Jwt jwt = jwtDecoder.decode(token);
        return true;
    } catch (Exception e) {
        return false;
    }
}

```bash

```bash

public String getUsernameFromToken(String token) {
    JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUrl);
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
}

```bash

}

```bash

## User Profile Endpoint


```java

## User Profile Endpoint


```java

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

```bash

@GetMapping("/profile")
public ResponseEntity<UserProfileResponse> getProfile() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

```bash

```bash

// Get user details from Cognito or database
UserProfileResponse profile = userService.getUserProfile(username);

```bash

```bash

return ResponseEntity.ok(profile);
}

```bash

}

```bash

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

```bash

@GetMapping("/profile")
public ResponseEntity<UserProfileResponse> getProfile() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

```bash

```bash

// Get user details from Cognito or database
UserProfileResponse profile = userService.getUserProfile(username);

```bash

```bash

return ResponseEntity.ok(profile);
}

```bash

}

```bash

## Testing


### Frontend Testing


```typescript

## Testing


### Frontend Testing


```typescript

describe('AuthService', () => {
  it('should sign in user', async () => {

```bash

const result = await authService.signIn('test@example.com', 'password123');
expect(result.username).toBe('test@example.com');

```bash

  });
});

```bash

describe('AuthService', () => {
  it('should sign in user', async () => {

```bash

const result = await authService.signIn('test@example.com', 'password123');
expect(result.username).toBe('test@example.com');

```bash

  });
});

```bash

### Backend Testing


```java

### Backend Testing


```java

@SpringBootTest
class AuthControllerTest {

```bash

@Test
void shouldGetProfile() throws Exception {
    // Given
    String token = generateValidJwtToken();

```bash

```bash

// When
mockMvc.perform(get("/api/v1/auth/profile")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@example.com"));
}

```bash

}

```bash

@SpringBootTest
class AuthControllerTest {

```bash

@Test
void shouldGetProfile() throws Exception {
    // Given
    String token = generateValidJwtToken();

```bash

```bash

// When
mockMvc.perform(get("/api/v1/auth/profile")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("test@example.com"));
}

```bash

}

```bash

## Related


- [Development Guide](../development.md) - Setup instructions
- [Deployment Guide](../deployment.md) - Production deployment
- [ADR-002: AWS Cognito](../09-architecture-decisions/adr-002-aws-cognito-authentication.md) - Architecture decision
