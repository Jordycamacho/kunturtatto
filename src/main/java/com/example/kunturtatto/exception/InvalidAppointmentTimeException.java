package com.example.kunturtatto.exception;

public class InvalidAppointmentTimeException extends Exception {
    public InvalidAppointmentTimeException(String message) {
        super(message);
    }

    public InvalidAppointmentTimeException(String message, Throwable cause) {
        super();
    }

    public InvalidAppointmentTimeException(Throwable cause) {
        super();
    }
}
