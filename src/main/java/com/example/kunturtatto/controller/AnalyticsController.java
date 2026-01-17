package com.example.kunturtatto.controller;

import com.example.kunturtatto.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Estadísticas y métricas del sitio")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Dashboard de Analytics", 
               description = "Muestra estadísticas de uso del sitio web")
    @GetMapping("/analytics")
    public String analyticsDashboard(Model model) {
        log.info("[ANALYTICS_CONTROLLER] Accediendo al dashboard de analytics");
        
        Map<String, Object> stats = analyticsService.getAnalyticsStats();
        model.addAttribute("analyticsStats", stats);
        model.addAttribute("googleAnalyticsId", "G-1RQQVKEJ4T");
        
        return "admin/analytics/dashboard";
    }
}