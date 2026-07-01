# API Testing Instructions

## Overview

This document provides detailed instructions for testing the Multi-Tenant E-Commerce SaaS Authentication Module API.

## Prerequisites

1. PostgreSQL database running on localhost:5432
2. Spring Boot application running on http://localhost:8080
3. Postman or any REST client installed
4. The application should have been started at least once to seed the default roles and super admin user

## Default Credentials

After the application starts, the following default user is created:

- **Email**: superadmin@multitenant.com
- **Password**: Admin@123
- **Role**: SUPER_ADMIN
- **Tenant Code**: SUPER_ADMIN

## Base URL

```
http://localhost:8080
```

## API Endpoints

### 1. Login Endpoint

**Endpoint**: `POST /api/auth/login`

**Description**: Authenticate user with email, password, and tenant code

**Request Body**:
```json
{
  "email": "superadmin@multitenant.com",
  "password": "Admin@123",
  "tenantCode": "SUPER_ADMIN"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "firstName": "Super",
      "lastName": "Admin",
      "email": "superadmin@multitenant.com",
      "phone": "+1-800-000-0000",
      "enabled": true,
      "emailVerified": true,
      "accountLocked": false,
      "roleName": "SUPER_ADMIN",
      "tenantName": null,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00",
      "lastLoginAt": "2024-01-01T10:05:00"
    }
  }
}
```

**Error Response** (Unauthorized - 401):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

### 2. Get Current User Endpoint

**Endpoint**: `GET /api/auth/me`

**Description**: Get information about the authenticated user

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Current user retrieved",
  "data": {
    "id": 1,
    "firstName": "Super",
    "lastName": "Admin",
    "email": "superadmin@multitenant.com",
    "phone": "+1-800-000-0000",
    "enabled": true,
    "emailVerified": true,
    "accountLocked": false,
    "roleName": "SUPER_ADMIN",
    "tenantName": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00",
    "lastLoginAt": "2024-01-01T10:05:00"
  }
}
```

### 3. Refresh Token Endpoint

**Endpoint**: `POST /api/auth/refresh-token`

**Description**: Generate a new access token using a refresh token

**Request Body**:
```json
{
  "refreshToken": "<refresh_token_from_login>"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": { ... }
  }
}
```

### 4. Change Password Endpoint

**Endpoint**: `POST /api/auth/change-password`

**Description**: Change password for the authenticated user

**Headers**:
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "currentPassword": "Admin@123",
  "newPassword": "NewAdmin@456",
  "confirmPassword": "NewAdmin@456"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null
}
```

**Password Requirements**:
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character (@$!%*?&)

### 5. Logout Endpoint

**Endpoint**: `POST /api/auth/logout`

**Description**: Logout the authenticated user

**Headers**:
```
Authorization: Bearer <access_token>
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

### 6. Send OTP Endpoint

**Endpoint**: `POST /api/auth/send-otp`

**Description**: Send OTP to user's email for verification

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "OTP sent to your email address",
  "data": null
}
```

**Note**: The OTP is valid for 10 minutes

### 7. Verify OTP Endpoint

**Endpoint**: `POST /api/auth/verify-otp`

**Description**: Verify OTP sent to user's email

**Request Body**:
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": null
}
```

### 8. Forgot Password Endpoint

**Endpoint**: `POST /api/auth/forgot-password`

**Description**: Request password reset email

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Password reset email sent to your email address",
  "data": null
}
```

**Note**: Reset token is valid for 1 hour

### 9. Reset Password Endpoint

**Endpoint**: `POST /api/auth/reset-password`

**Description**: Reset password using reset token

**Request Body**:
```json
{
  "token": "<reset_token_from_email>",
  "newPassword": "NewPass@789"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null
}
```

### 10. Verify Email Endpoint

**Endpoint**: `POST /api/auth/verify-email`

**Description**: Verify email using verification token

**Request Body**:
```json
{
  "email": "user@example.com",
  "token": "<verification_token>"
}
```

**Response** (Success - 200 OK):
```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": null
}
```

## Testing in Postman

### Step 1: Import the Collection

1. Open Postman
2. Click on "Import"
3. Upload the `postman_collection.json` file
4. The collection will be imported with all endpoints

### Step 2: Set Environment Variables

After logging in, extract the tokens and set them as variables:

1. From the login response, copy the `accessToken`
2. In Postman, click on "Environment" (top right)
3. Select or create an environment
4. Add the following variables:
   - `access_token`: <paste access token>
   - `refresh_token`: <paste refresh token>

### Step 3: Run Requests

1. Select the environment
2. Click on each request and click "Send"
3. Verify the response status and body

## Common Error Responses

### 400 Bad Request - Validation Error
```json
{
  "success": false,
  "message": "email: Email must be a valid email address",
  "data": null
}
```

### 401 Unauthorized - Invalid Credentials
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

### 403 Forbidden - Account Locked
```json
{
  "success": false,
  "message": "Account is locked. Please try again later",
  "data": null
}
```

### 404 Not Found - User Not Found
```json
{
  "success": false,
  "message": "User not found with email: user@example.com",
  "data": null
}
```

### 409 Conflict - Email Already Exists
```json
{
  "success": false,
  "message": "Email already exists",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Internal server error",
  "data": null
}
```

## Testing with cURL

### Login Example
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "superadmin@multitenant.com",
    "password": "Admin@123",
    "tenantCode": "SUPER_ADMIN"
  }'
```

### Get Current User Example
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <access_token>"
```

## Testing with cURL - PowerShell

### Login Example
```powershell
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -Headers @{ "Content-Type" = "application/json" } `
  -Body '{
    "email": "superadmin@multitenant.com",
    "password": "Admin@123",
    "tenantCode": "SUPER_ADMIN"
  }'

Write-Host $loginResponse | ConvertTo-Json
```

### Get Current User Example
```powershell
$accessToken = $loginResponse.data.accessToken

$userResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" `
  -Method GET `
  -Headers @{ "Authorization" = "Bearer $accessToken" }

Write-Host $userResponse | ConvertTo-Json
```

## Security Considerations

1. **JWT Secret**: Change the `app.jwt.secret` in `application.properties` to a strong, random key
2. **HTTPS**: Always use HTTPS in production
3. **Token Expiration**: Access tokens expire in 15 minutes, refresh tokens in 7 days
4. **Account Lockout**: After 5 failed login attempts, the account is locked for 15 minutes
5. **Password**: Always use strong passwords matching the validation requirements

## Troubleshooting

### Issue: "User not found" on login
- Ensure the database is running
- Verify the application has started and seeded the default data
- Check if the email is correct

### Issue: "Invalid JWT token"
- Ensure the JWT secret in `application.properties` matches the one used to generate the token
- Check if the token has expired
- Verify the Authorization header format: `Bearer <token>`

### Issue: "Connection refused"
- Ensure PostgreSQL is running on localhost:5432
- Verify the database credentials in `application.properties`
- Ensure the application is running on port 8080

## Swagger UI

The Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

or

```
http://localhost:8080/swagger-ui/index.html
```

You can test all endpoints directly from the Swagger UI with the "Try it out" feature.

## API Documentation

The OpenAPI specification is available at:
```
http://localhost:8080/v3/api-docs
```

## Notes

- All timestamps are in UTC
- All passwords are encrypted using BCrypt
- Email verification is required for login (except SUPER_ADMIN which is pre-verified)
- The application uses stateless JWT-based authentication
- All authentication endpoints except login, refresh-token, and Swagger are protected

