package com.example.multitenant.exception;

import com.example.multitenant.util.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleTenantNotFound(TenantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtAuthentication(JwtAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountLocked(AccountLockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailNotVerified(EmailNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error"));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraint(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

}

