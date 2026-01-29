package com.example.kunturtatto.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class SeoController {

    @Value("${app.url:https://muthabara.cloud}")
    private String appUrl;

    @Value("${app.name:Muthabara Tattoo Studio}")
    private String appName;

    @GetMapping("/robots.txt")
    public ResponseEntity<String> robotsTxt() {
        String content = """
                User-agent: *
                Allow: /

                # Rutas administrativas bloqueadas
                Disallow: /admin/
                Disallow: /Muthabara/ingresar
                Disallow: /Muthabara/logout
                Disallow: /api/

                # Rutas de API internas
                Disallow: /api-docs
                Disallow: /swagger-ui/
                Disallow: /v3/api-docs/

                # Sitemap
                Sitemap: %s/sitemap.xml

                # Información del sitio
                # %s
                # Contacto: luisa_ftc112@outlook.com
                """
                .formatted(appUrl, appName);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping("/sitemap.xml")
    public ResponseEntity<String> sitemapXml() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9
                        http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">

                    <!-- Página principal -->
                    <url>
                        <loc>%s/</loc>
                        <lastmod>%s</lastmod>
                        <changefreq>daily</changefreq>
                        <priority>1.0</priority>
                    </url>

                    <!-- Página de inicio -->
                    <url>
                        <loc>%s/Muthabara</loc>
                        <lastmod>%s</lastmod>
                        <changefreq>daily</changefreq>
                        <priority>0.9</priority>
                    </url>

                    <!-- Galería de diseños -->
                    <url>
                        <loc>%s/Muthabara/diseños</loc>
                        <lastmod>%s</lastmod>
                        <changefreq>weekly</changefreq>
                        <priority>0.8</priority>
                    </url>

                    <!-- Contacto -->
                    <url>
                        <loc>%s/Muthabara/contacto</loc>
                        <lastmod>%s</lastmod>
                        <changefreq>monthly</changefreq>
                        <priority>0.7</priority>
                    </url>

                    <!-- Política de privacidad -->
                    <url>
                        <loc>%s/Muthabara/politica-privacidad</loc>
                        <lastmod>%s</lastmod>
                        <changefreq>yearly</changefreq>
                        <priority>0.3</priority>
                    </url>

                    <!-- Términos y condiciones -->
                    <url>
                        <loc>%s/Muthabara/terminos-condiciones</loc>
                        <lastmod>%s</lastmod>
                        <changefreq>yearly</changefreq>
                        <priority>0.3</priority>
                    </url>

                </urlset>
                """.formatted(
                appUrl, currentDate,
                appUrl, currentDate,
                appUrl, currentDate,
                appUrl, currentDate,
                appUrl, currentDate,
                appUrl, currentDate);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    @GetMapping("/humans.txt")
    public ResponseEntity<String> humansTxt() {
        String content = """
                /* TEAM */
                Titulo: %s
                Sitio: %s
                Email: info@muthabara.com

                /* SITE */
                Last update: %s
                Language: Spanish
                Doctype: HTML5
                Components: Spring Boot, Thymeleaf, Bootstrap
                Software: IntelliJ IDEA, Git, Maven

                /* THANKS */
                Gracias por visitar nuestro estudio de tatuajes.
                """
                .formatted(appName, appUrl, LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }
}