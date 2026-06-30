package com.example.multitenant.service.impl;

import com.example.multitenant.dto.auth.AuthResponse;
import com.example.multitenant.dto.auth.LoginRequest;
import com.example.multitenant.dto.auth.RegisterRequest;
import com.example.multitenant.entity.RefreshToken;
import com.example.multitenant.entity.Role;
import com.example.multitenant.entity.User;
import com.example.multitenant.exception.ApiException;
import com.example.multitenant.repository.RefreshTokenRepository;
import com.example.multitenant.repository.RoleRepository;
import com.example.multitenant.repository.UserRepository;
import com.example.multitenant.security.JwtUtil;
import com.example.multitenant.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new ApiException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ApiException("User not found"));

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), roles);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user.getEmail());

        Claims refreshClaims = jwtUtil.getClaims(refreshTokenStr);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(refreshClaims.getExpiration().toInstant());
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr, jwtUtil.getClaims(accessToken).getExpiration().getTime());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhoneNumber());

        // Public registration is ALWAYS ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new ApiException("Role not found: ROLE_USER"));
        user.getRoles().add(userRole);
        user.setEnabled(true); // User registration is immediately enabled

        userRepository.save(user);

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), roles);
        String refreshTokenStr = jwtUtil.generateRefreshToken(user.getEmail());

        Claims refreshClaims = jwtUtil.getClaims(refreshTokenStr);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(refreshClaims.getExpiration().toInstant());
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr, jwtUtil.getClaims(accessToken).getExpiration().getTime());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        Optional<RefreshToken> rtOpt = refreshTokenRepository.findByToken(refreshToken);
        if (rtOpt.isEmpty()) throw new ApiException("Invalid refresh token");
        RefreshToken rt = rtOpt.get();
        if (rt.isRevoked() || rt.getExpiryDate().isBefore(Instant.now())) throw new ApiException("Refresh token expired");

        String username = jwtUtil.getUsername(refreshToken);
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ApiException("User not found"));
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), roles);

        return new AuthResponse(accessToken, refreshToken, jwtUtil.getClaims(accessToken).getExpiration().getTime());
    }
}

