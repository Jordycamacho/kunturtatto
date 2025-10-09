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
            Disallow: /Muthabara/ingresar
            Disallow: /Muthabara/logout
            
            Sitemap: https://www.Muthabara.com/sitemap.xml
            """;
        
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(content);
    }
}