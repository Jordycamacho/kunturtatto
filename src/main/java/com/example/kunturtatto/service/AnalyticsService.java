package com.example.kunturtatto.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class AnalyticsService {
    
    // Mapa para almacenar eventos
    private final Map<String, AnalyticsEvent> eventStore = new ConcurrentHashMap<>();
    private final AtomicLong totalEvents = new AtomicLong(0);
    
    /**
     * Registra un evento de página vista.
     */
    public void trackPageView(String pageTitle, String pagePath, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            
            log.info("[ANALYTICS] Page View: {} | Path: {} | IP: {}", 
                    pageTitle, pagePath, clientIp);
            
            // Crear evento de forma SEGURA (sin builder)
            AnalyticsEvent event = new AnalyticsEvent();
            event.setId(UUID.randomUUID().toString());
            event.setType("PAGE_VIEW");
            event.setCategory("Navigation");
            event.setAction("view");
            event.setLabel(pageTitle);
            event.setValue(1);
            event.setIp(clientIp);
            event.setUserAgent(userAgent != null ? userAgent : "unknown");
            event.setTimestamp(System.currentTimeMillis());
            
            eventStore.put(event.getId(), event);
            totalEvents.incrementAndGet();
            
            log.debug("[ANALYTICS] Event stored. Total events: {}", eventStore.size());
            
        } catch (Exception e) {
            log.error("[ANALYTICS] Error tracking page view: {}", e.getMessage());
        }
    }
    
    /**
     * Registra un evento personalizado.
     */
    public void trackEvent(String category, String action, String label, Integer value, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);
            
            log.info("[ANALYTICS] Event: {}/{}/{} | Value: {} | IP: {}", 
                    category, action, label, value, clientIp);
            
            AnalyticsEvent event = new AnalyticsEvent();
            event.setId(UUID.randomUUID().toString());
            event.setType("CUSTOM_EVENT");
            event.setCategory(category);
            event.setAction(action);
            event.setLabel(label);
            event.setValue(value != null ? value : 1);
            event.setIp(clientIp);
            event.setTimestamp(System.currentTimeMillis());
            
            eventStore.put(event.getId(), event);
            totalEvents.incrementAndGet();
            
            log.debug("[ANALYTICS] Custom event stored. Total events: {}", eventStore.size());
            
        } catch (Exception e) {
            log.error("[ANALYTICS] Error tracking event: {}", e.getMessage());
        }
    }
    
    /**
     * Registra conversión de formulario.
     */
    public void trackFormSubmission(String formId, boolean success, HttpServletRequest request) {
        trackEvent("form", "submit", formId + (success ? "_success" : "_error"), 1, request);
    }
    
    /**
     * Obtiene estadísticas internas.
     */
    public Map<String, Object> getAnalyticsStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        try {
            long pageViews = eventStore.values().stream()
                    .filter(e -> "PAGE_VIEW".equals(e.getType()))
                    .count();
            
            long uniqueIPs = eventStore.values().stream()
                    .map(AnalyticsEvent::getIp)
                    .distinct()
                    .count();
            
            // Eventos por categoría
            Map<String, Long> eventsByCategory = new ConcurrentHashMap<>();
            for (AnalyticsEvent event : eventStore.values()) {
                String category = event.getCategory();
                eventsByCategory.put(category, eventsByCategory.getOrDefault(category, 0L) + 1);
            }
            
            stats.put("totalEvents", eventStore.size());
            stats.put("pageViews", pageViews);
            stats.put("uniqueVisitors", uniqueIPs);
            stats.put("eventsByCategory", eventsByCategory);
            
            log.debug("[ANALYTICS] Stats generated: {}", stats);
            
        } catch (Exception e) {
            log.error("[ANALYTICS] Error generating stats: {}", e.getMessage());
            stats.put("error", "Error generating analytics stats");
        }
        
        return stats;
    }
    
    private String getClientIp(HttpServletRequest request) {
        try {
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null) {
                return xfHeader.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    // Clase interna para eventos - SIN BUILDER, solo getters/setters
    public static class AnalyticsEvent {
        private String id;
        private String type;
        private String category;
        private String action;
        private String label;
        private Integer value;
        private String ip;
        private String userAgent;
        private Long timestamp;
        
        // Getters y setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
}