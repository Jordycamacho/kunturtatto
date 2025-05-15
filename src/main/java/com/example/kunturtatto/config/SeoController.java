package com.example.kunturtatto.config;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SeoController {
    
    @GetMapping("/robots.txt")
    public ResponseEntity<String> robotsTxt() {
        String content = """
            User-agent: *
            Disallow: /admin/
            Disallow: /admin/appointments/
            Disallow: /KunturTattoo/ingresar
            Disallow: /KunturTattoo/logout
            
            Sitemap: https://www.kunturtattoo.com/sitemap.xml
            """;
        
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(content);
    }
}