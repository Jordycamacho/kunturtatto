package com.example.kunturtatto.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;

public class CustomErrorController implements ErrorController{
    /**
     * Muestra la página personalizada de error 404.
     *
     * @return El nombre de la vista para la página de error 404.
     */

     @GetMapping("/error")
    public String handleError() {
        return "error/404";
    }
}
