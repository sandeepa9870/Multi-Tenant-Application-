package com.example.multitenant.controller;

import com.example.multitenant.dto.*;
import com.example.multitenant.service.AuthService;
import com.example.multitenant.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with email, password, and tenant code")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for email: {}", loginRequest.getEmail());
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.of("Login successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request");
        authService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok(ApiResponse.of("Logout successful", null));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Refresh the access token using a refresh token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Refresh token request");
        LoginResponse response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.of("Token refreshed successfully", response));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Send password reset email to user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Forgot password request for email: {}", forgotPasswordRequest.getEmail());
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(ApiResponse.of("Password reset email sent to your email address", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset user password using reset token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired reset token")
    })
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Reset password request");
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(ApiResponse.of("Password reset successfully", null));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Send OTP to user's email for verification")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Object>> sendOtp(@Valid @RequestBody SendOtpRequest sendOtpRequest) {
        log.info("Send OTP request for email: {}", sendOtpRequest.getEmail());
        authService.sendOtp(sendOtpRequest);
        return ResponseEntity.ok(ApiResponse.of("OTP sent to your email address", null));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify OTP sent to user's email")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    public ResponseEntity<ApiResponse<Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        log.info("Verify OTP request for email: {}", verifyOtpRequest.getEmail());
        authService.verifyOtp(verifyOtpRequest);
        return ResponseEntity.ok(ApiResponse.of("Email verified successfully", null));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify email using verification token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired verification token")
    })
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
        log.info("Verify email request for email: {}", verifyEmailRequest.getEmail());
        authService.verifyEmail(verifyEmailRequest);
        return ResponseEntity.ok(ApiResponse.of("Email verified successfully", null));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid current password")
    })
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Change password request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        authService.changePassword(changePasswordRequest, email);
        return ResponseEntity.ok(ApiResponse.of("Password changed successfully", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User information retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        log.info("Get current user request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponse response = authService.getCurrentUser(email);
        return ResponseEntity.ok(ApiResponse.of("Current user retrieved", response));
    }
}

