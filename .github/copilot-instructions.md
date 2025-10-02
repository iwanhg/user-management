## Quick context for AI coding agents

This is a Spring Boot 3.2 / Java 21 microservice that implements user management with JWT authentication, role-based authorization and Flyway migrations.

- Entrypoint: `com.example.usermanagement.UserManagementApplication` (standard Spring Boot app).
- Layers: `controller` -> `service` -> `repository` -> `entity`. Mappings are handled by MapStruct (`mapper.ApplicationMapper`).
- DB migrations live in `src/main/resources/db/migration` (Flyway SQL files: `V1__...`, `V2__...`, ...).

## Important files to read before editing or adding features

- Security & auth
  - `config/SecurityConfig.java` — stateless JWT security config; permits `/api/auth/**` and Swagger endpoints.
  - `config/JwtAuthenticationFilter.java` — extracts Bearer token and sets SecurityContext.
  - `service/JwtService.java` — JWT generation/validation using a Base64 `jwt.secret.key` (see warnings below).
  - `service/AuthService.java` — sign-in, refresh (token rotation), logout flows; persists refresh tokens on `User`.
  - `config/DataInitializer.java` — creates default ADMIN user on startup using `app.default-admin.*` properties.

- User & authorization
  - `controller/AuthController.java`, `controller/UserController.java`, `controller/RoleAdminController.java` — public API surface.
  - `service/UserService.java`, `service/RolePermissionService.java` — business rules for user, role and permission operations.
  - `entity/User.java`, `entity/Role.java`, `entity/Permission.java` — JPA models. Note: roles eagerly fetch permissions; users eagerly fetch roles.

## Project-specific conventions & patterns

- Authorization uses permission names (e.g. `MANAGE_AUTHORIZATION`, `READ_USERS`) as GrantedAuthority values. In `User.getAuthorities()` permissions are derived from roles' permissions (permission names must match authority checks used in `@PreAuthorize`).
- Role/permission naming: role names are stored as `ROLE_ADMIN` / `ROLE_USER`, while permission names are plain strings used as authorities.
- Refresh token strategy: refresh tokens are stored on the `users.refresh_token` column and rotated on use (AuthService.refreshToken).
- DTOs are immutable records (check `src/main/java/.../dto`) and MapStruct is used for mapping.
- Tests (unit + integration) are present under `src/main/resources/db/migration` in this repository snapshot — verify test locations before adding new tests.

## Build / run / test (developer workflows)

- Maven (wrapper included). App was documented to run via the wrapper. On Windows use `mvnw.cmd spring-boot:run`.
- Run tests: `mvnw.cmd test` (Windows) or `./mvnw test` (POSIX).
- Flyway migrations run automatically at startup (check `application.properties`). Migration scripts located at `src/main/resources/db/migration`.

## Security, performance and correctness notes (evidence + action items)

- JWT implementation
  - `JwtService` decodes a Base64 `jwt.secret.key` and signs tokens with HMAC. Ensure `jwt.secret.key` is a high-entropy Base64 string and is not checked into source.
  - extractAllClaims uses JJWT’s `parser().verifyWith(...).build().parseSignedClaims(token).getPayload()` style — be careful: this code assumes signed JWT; validate exceptions are handled by the filter implicitly. When modifying, avoid swallowing parsing exceptions silently.

- Refresh tokens
  - Refresh tokens are persisted on the `users` table and rotated on use (good). However, `findByRefreshToken` is used directly without additional index hints — ensure DB has an index on `refresh_token` if you expect many users.
  - The `refreshToken` column is stored as plain text in DB. Consider encrypting at rest (application-level encryption) for sensitive environments.

- Eager fetching & N+1 risk
  - `User.roles` is EAGER and `Role.permissions` is EAGER. Mapping `User.getAuthorities()` enumerates through permissions which may cause N+1 queries when fetching pages of users or roles.
  - For paginated endpoints (e.g. `GET /api/users`), the service uses `userRepository.findAll(pageable)`; because roles are EAGER, JPA may issue joins — monitor queries and consider using DTO projection queries or changing fetch strategy to LAZY + fetch-joins in repository methods for large datasets.

- Passwords & logging
  - Passwords are hashed using `BCryptPasswordEncoder` in `SecurityConfig` and `UserService.createUser` (good). Ensure logging never prints raw passwords — existing code logs only usernames in `DataInitializer`.

- Exception handling
  - `GlobalExceptionHandler` exists (see `exception/GlobalExceptionHandler.java`) — prefer adding specific exception types rather than RuntimeException in services so handlers can return appropriate HTTP statuses.

## Concrete patterns & examples to follow when contributing

- Adding an endpoint that requires permissions:
  - Add `@PreAuthorize("hasAuthority('PERMISSION_NAME')")` to the controller method.
  - Implement business logic in a `service/*` class, use transactions (`@Transactional`) and map entities to DTOs with `ApplicationMapper`.

- Creating a role with permissions (example flow):
  - Controller: call `RolePermissionService.createRole` -> `roleRepository.save`.
  - Assign permission names in `UpdateRolePermissionsRequest.permissionNames()` and the service resolves them via `permissionRepository.findByName`.

## Files & locations worth scanning for deeper context

- `src/main/resources/application.properties` and `application.properties.sample.properties` — secrets, DB config, JWT properties.
- `src/main/resources/logback-spring.xml` — logging levels and appenders (log file rotation).
- `src/main/resources/db/migration/*.sql` — schema and seed data; important to keep forward/backward migration compatibility.
- `src/main/java/com/example/usermanagement/exception/` — how errors are represented to clients.

## When to ask a human reviewer

- Changing JWT algorithms, token format, or secret storage strategy.
- Changing fetch strategies on entities (EAGER -> LAZY) because it may affect many queries and tests.
- Adding long-running or blocking operations to synchronous controller methods — consider async endpoints.

If something here is unclear or you want me to expand sections (examples, tests to add, or code patches for hardening), tell me which area to iterate on and I'll update the file.
