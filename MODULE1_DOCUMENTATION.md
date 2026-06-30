Module 1 - Project Setup and Auth

Checklist:
- [x] Project setup: Gradle, Java 21
- [x] application.yml with MySQL and JWT properties
- [x] Entities: User, Role, RefreshToken, BaseEntity (auditing + soft delete)
- [x] Repositories: UserRepository, RoleRepository, RefreshTokenRepository
- [x] DTOs: LoginRequest, RegisterRequest, AuthResponse
- [x] Services: AuthService (+impl), RoleService (+impl)
- [x] Controllers: AuthController, SuperAdminRoleController
- [x] Security: JwtUtil, JwtAuthenticationFilter, SecurityConfig, CustomUserDetailsService
- [x] Seeder: SuperAdminSeeder
- [x] OpenAPI config
- [x] Global exception handling
- [x] Postman collection

Sample JSON Requests

Register (User):
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "Password123!",
  "role": "ROLE_USER"
}

Login:
{
  "email": "john@example.com",
  "password": "Password123!"
}

Refresh Token:
<raw refresh token string in request body>

Sample JSON Response (wrapped in ApiResponse):
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "ey...",
    "refreshToken": "ey...",
    "expiresIn": 169... 
  }
}

SQL Table Structures (simplified)

users:
- id CHAR(36) PRIMARY KEY
- first_name VARCHAR
- last_name VARCHAR
- email VARCHAR UNIQUE
- password VARCHAR
- phone VARCHAR
- enabled BOOLEAN
- locked BOOLEAN
- created_at DATETIME
- updated_at DATETIME
- created_by VARCHAR
- updated_by VARCHAR
- is_deleted BOOLEAN

roles:
- id CHAR(36) PRIMARY KEY
- name VARCHAR UNIQUE
- description VARCHAR
- created_at/updated_at/... (auditing)

user_roles (join table):
- user_id
- role_id

refresh_tokens:
- id CHAR(36)
- token VARCHAR UNIQUE
- user_id
- expiry_date DATETIME
- revoked BOOLEAN

Folder Structure (main)

src/main/java/com/example/multitenant
  config
  controller
  entity
  exception
  repository
  security
  service
  service.impl
  seeder
  dto
  util

How to run

1) Update database credentials in application.yml
2) Build and run:

Windows PowerShell:

./gradlew bootRun

Swagger UI: http://localhost:8080/swagger-ui.html

Notes & Next Steps
- Module 1 implements authentication, roles, security and a super-admin seeder. Admin approval flows, full role management, and all business entities will be implemented in Module 2+ upon your confirmation.

