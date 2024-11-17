package com.example.kunturtatto.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @Operation(summary = "Maneja los errores de la aplicación", description = "Redirige a una página de error personalizada según el código de estado (404, 500, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Página no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "200", description = "Página de error personalizada cargada correctamente")
    })
    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "Ha ocurrido un error inesperado";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("statusCode", statusCode);

            switch (statusCode) {
                case 404:
                    logger.warn("Error 404: Página no encontrada - URL solicitada: {}", request.getRequestURI());
                    errorMessage = "Lo sentimos, la página que buscas no existe.";
                    return "error/404";

                case 500:
                    logger.error("Error 500: Error interno del servidor - URL solicitada: {}", request.getRequestURI());
                    errorMessage = "Lo sentimos, algo salió mal en el servidor.";
                    return "error/500";

                default:
                    logger.info("Error {}: URL solicitada: {}", statusCode, request.getRequestURI());
                    errorMessage = "Ha ocurrido un error inesperado. Por favor, intenta nuevamente.";
                    return "error/general";
            }
        }

        logger.error("Error sin código de estado definido: URL solicitada: {}", request.getRequestURI());
        model.addAttribute("errorMessage", errorMessage);
        return "error/general";
    }
}
