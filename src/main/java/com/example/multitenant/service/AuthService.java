package com.example.multitenant.service;

import com.example.multitenant.dto.auth.AuthResponse;
import com.example.multitenant.dto.auth.LoginRequest;
import com.example.multitenant.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
}

