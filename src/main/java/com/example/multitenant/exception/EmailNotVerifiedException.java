package com.example.multitenant.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}

