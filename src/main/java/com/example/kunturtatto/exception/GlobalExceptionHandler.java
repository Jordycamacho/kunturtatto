package com.example.kunturtatto.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException error, HttpServletResponse response, Model model) {
        log.warn("Recurso no encontrado: {}", error.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("title", "No está");
        model.addAttribute("message",
                ErrorMessages.userMessage("Eso que buscas ya no está. Vuelve al listado e inténtalo de nuevo.", error));
        return "error/simple";
    }

    @ExceptionHandler({
            EmailException.class,
            InvalidAppointmentTimeException.class,
            EmailAlreadyExistsException.class,
            IllegalArgumentException.class
    })
    public String known(Exception error, HttpServletResponse response, Model model) {
        log.warn("Petición rechazada: {}", error.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "No se pudo hacer");
        model.addAttribute("message",
                ErrorMessages.userMessage("Revisa los datos e inténtalo otra vez.", error));
        return "error/simple";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileTooBig(MaxUploadSizeExceededException error, HttpServletResponse response, Model model) {
        log.warn("Archivo demasiado grande");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("title", "La imagen pesa demasiado");
        model.addAttribute("message", "La imagen no puede pasar de 10 MB. Elige una más pequeña.");
        return "error/simple";
    }

    @ExceptionHandler(Exception.class)
    public String unexpected(Exception error, HttpServletResponse response, Model model) throws Exception {
        if (error instanceof AccessDeniedException
                || error instanceof AuthenticationException
                || error instanceof NoResourceFoundException) {
            throw error;
        }
        log.error("Error no controlado", error);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("title", "Algo ha fallado");
        model.addAttribute("message",
                "No se ha podido completar. Vuelve a intentarlo. Si sigue igual, recarga la página.");
        return "error/simple";
    }
}
