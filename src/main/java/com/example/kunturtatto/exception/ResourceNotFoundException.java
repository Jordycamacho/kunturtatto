package com.example.kunturtatto.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message, String string, Long id) {
        super(message);
    }
}
