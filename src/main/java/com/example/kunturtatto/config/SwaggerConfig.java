package com.example.kunturtatto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para documentación de la API.
 */
@Configuration
public class SwaggerConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.name:Kuntur Tattoo Studio}")
    private String appName;

    @Value("${app.description:Sistema de gestión para estudio de tatuajes}")
    private String appDescription;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    /**
     * Configuración principal de OpenAPI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName + " - API Documentation")
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("http://Muthabara.com/terms")
                        .contact(new Contact()
                                .name("Soporte Kuntur Tattoo")
                                .email("soporte@Muthabara.com")
                                .url("http://Muthabara.com/support"))
                        .license(new License()
                                .name("Licencia Propietaria")
                                .url("http://Muthabara.com/license")))
                .servers(List.of(
                        new Server()
                                .url(appUrl)
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.Muthabara.com")
                                .description("Servidor de Producción")
                ));
    }
}