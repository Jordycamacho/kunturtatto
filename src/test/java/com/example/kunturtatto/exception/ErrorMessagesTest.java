package com.example.kunturtatto.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ErrorMessagesTest {

    @Test
    void showsAShortKnownMessage() {
        String message = ErrorMessages.userMessage(
                "fallback",
                new IllegalArgumentException("Elige una hora dentro del horario"));

        assertEquals("Elige una hora dentro del horario", message);
    }

    @Test
    void hidesSqlAndUsesTheFallback() {
        String message = ErrorMessages.userMessage(
                "No se pudo guardar.",
                new IllegalArgumentException("could not execute statement SQL"));

        assertEquals("No se pudo guardar.", message);
    }

    @Test
    void hidesUnexpectedExceptions() {
        String message = ErrorMessages.userMessage(
                "No se pudo guardar.",
                new RuntimeException("NullPointerException at User.java"));

        assertEquals("No se pudo guardar.", message);
    }
}
