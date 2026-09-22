package com.example.kunturtatto.exception;

/**
 * Texto que puede ver una persona. El detalle técnico se queda en el log.
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static String userMessage(String fallback, Exception error) {
        if (error instanceof ResourceNotFoundException
                || error instanceof EmailAlreadyExistsException
                || error instanceof InvalidAppointmentTimeException
                || error instanceof EmailException
                || error instanceof IllegalArgumentException) {
            String message = error.getMessage();
            if (isSafe(message)) {
                return message;
            }
        }
        return fallback;
    }

    private static boolean isSafe(String message) {
        if (message == null || message.isBlank() || message.length() > 180) {
            return false;
        }
        String lower = message.toLowerCase();
        return !lower.contains("exception")
                && !lower.contains("sql")
                && !lower.contains("jdbc")
                && !lower.contains("null")
                && !lower.contains("hibernate")
                && !lower.contains("stack");
    }
}
