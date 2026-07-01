package com.example.multitenant.service;

import com.example.multitenant.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void logout(String token);
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    void sendOtp(SendOtpRequest sendOtpRequest);
    void verifyOtp(VerifyOtpRequest verifyOtpRequest);
    void verifyEmail(VerifyEmailRequest verifyEmailRequest);
    void changePassword(ChangePasswordRequest changePasswordRequest, String email);
    UserResponse getCurrentUser(String email);
}

