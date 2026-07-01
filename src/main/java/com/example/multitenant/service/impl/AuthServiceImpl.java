package com.example.multitenant.service.impl;

import com.example.multitenant.dto.*;
import com.example.multitenant.entity.*;
import com.example.multitenant.exception.*;
import com.example.multitenant.repository.*;
import com.example.multitenant.security.JwtTokenProvider;
import com.example.multitenant.service.AuthService;
import com.example.multitenant.util.OtpGenerator;
import com.example.multitenant.util.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long ACCOUNT_LOCK_DURATION_MINUTES = 15;
    private static final long OTP_EXPIRY_MINUTES = 10;
    private static final long PASSWORD_RESET_TOKEN_EXPIRY_HOURS = 1;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        // Find tenant
        Tenant tenant = tenantRepository.findByTenantCode(loginRequest.getTenantCode())
                .orElseThrow(() -> new TenantNotFoundException(
                    "Tenant not found with code: " + loginRequest.getTenantCode()));

        // Find user
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify tenant
        if (!loginRequest.getTenantCode().equals("SUPER_ADMIN") &&
            (user.getTenant() == null || !user.getTenant().getId().equals(tenant.getId()))) {
            throw new InvalidCredentialsException("User does not belong to this tenant");
        }

        // Check if account is locked
        if (user.getAccountLocked()) {
            if (user.getAccountLockedUntil() != null &&
                LocalDateTime.now().isBefore(user.getAccountLockedUntil())) {
                throw new AccountLockedException("Account is locked. Please try again later");
            } else {
                user.setAccountLocked(false);
                user.setLoginAttempts(0);
                user.setAccountLockedUntil(null);
            }
        }

        // Check if user is enabled
        if (!user.getEnabled()) {
            throw new InvalidCredentialsException("User account is disabled");
        }

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new EmailNotVerifiedException("Email not verified. Please verify your email first");
        }

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(ACCOUNT_LOCK_DURATION_MINUTES));
                userRepository.save(user);
                throw new AccountLockedException(
                    "Account locked due to multiple failed login attempts. Please try again after " +
                    ACCOUNT_LOCK_DURATION_MINUTES + " minutes");
            }
            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Reset login attempts on successful login
        user.setLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // Save refresh token
        RefreshToken refreshToken = new RefreshToken(
            refreshTokenStr,
            LocalDateTime.now().plusHours(7 * 24),
            user
        );
        refreshTokenRepository.save(refreshToken);

        log.info("User logged in successfully: {}", user.getEmail());

        return new LoginResponse(accessToken, refreshTokenStr, mapToUserResponse(user), "Bearer");
    }

    @Override
    public void logout(String token) {
        log.info("Logout attempt");
        String email = jwtTokenProvider.getUserEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);
        log.info("User logged out successfully: {}", email);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Refresh token attempt");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        if (refreshToken.getRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // Save new refresh token
        RefreshToken newRefreshTokenEntity = new RefreshToken(
            newRefreshToken,
            LocalDateTime.now().plusHours(7 * 24),
            user
        );
        refreshTokenRepository.save(newRefreshTokenEntity);

        // Optionally revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Token refreshed successfully for user: {}", user.getEmail());

        return new LoginResponse(newAccessToken, newRefreshToken, mapToUserResponse(user), "Bearer");
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Forgot password attempt for email: {}", forgotPasswordRequest.getEmail());

        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " +
                    forgotPasswordRequest.getEmail()));

        String resetToken = TokenGenerator.generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
            resetToken,
            user,
            LocalDateTime.now().plusHours(PASSWORD_RESET_TOKEN_EXPIRY_HOURS)
        );
        passwordResetTokenRepository.save(passwordResetToken);

        // TODO: Send reset password email to user
        log.info("Password reset token generated for user: {}", user.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("Reset password attempt");

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        if (passwordResetToken.isExpired()) {
            throw new InvalidTokenException("Password reset token has expired");
        }

        if (passwordResetToken.getUsed()) {
            throw new InvalidTokenException("Password reset token has already been used");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Override
    public void sendOtp(SendOtpRequest sendOtpRequest) {
        log.info("Send OTP attempt for email: {}", sendOtpRequest.getEmail());

        User user = userRepository.findByEmail(sendOtpRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " +
                    sendOtpRequest.getEmail()));

        String otp = OtpGenerator.generateOtp();
        Otp otpEntity = new Otp(
            otp,
            user,
            LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)
        );
        otpRepository.save(otpEntity);

        // TODO: Send OTP via email to user
        log.info("OTP generated and sent to user: {}", user.getEmail());
    }

    @Override
    public void verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        log.info("Verify OTP attempt for email: {}", verifyOtpRequest.getEmail());

        User user = userRepository.findByEmail(verifyOtpRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " +
                    verifyOtpRequest.getEmail()));

        Otp otp = otpRepository.findByOtpAndUser(verifyOtpRequest.getOtp(), user)
                .orElseThrow(() -> new InvalidTokenException("Invalid OTP"));

        if (otp.isExpired()) {
            throw new InvalidTokenException("OTP has expired");
        }

        if (otp.getUsed()) {
            throw new InvalidTokenException("OTP has already been used");
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Override
    public void verifyEmail(VerifyEmailRequest verifyEmailRequest) {
        log.info("Verify email attempt for email: {}", verifyEmailRequest.getEmail());

        User user = userRepository.findByEmail(verifyEmailRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " +
                    verifyEmailRequest.getEmail()));

        PasswordResetToken verificationToken = passwordResetTokenRepository.findByToken(verifyEmailRequest.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid email verification token"));

        if (verificationToken.isExpired()) {
            throw new InvalidTokenException("Email verification token has expired");
        }

        if (verificationToken.getUsed()) {
            throw new InvalidTokenException("Email verification token has already been used");
        }

        verificationToken.setUsed(true);
        passwordResetTokenRepository.save(verificationToken);

        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, String email) {
        log.info("Change password attempt for user: {}", email);

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirm password do not match");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", email);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        log.info("Get current user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setEnabled(user.getEnabled());
        response.setEmailVerified(user.getEmailVerified());
        response.setAccountLocked(user.getAccountLocked());
        response.setRoleName(user.getRole().getRoleName());
        response.setTenantName(user.getTenant() != null ? user.getTenant().getTenantName() : null);
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        return response;
    }
}

