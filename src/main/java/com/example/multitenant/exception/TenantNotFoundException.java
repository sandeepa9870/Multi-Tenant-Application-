package com.example.multitenant.exception;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}

