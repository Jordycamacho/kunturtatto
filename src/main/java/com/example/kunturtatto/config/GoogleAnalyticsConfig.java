package com.example.kunturtatto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GoogleAnalyticsConfig {

    @Value("${google.analytics.tracking-id:G-1RQQVKEJ4T}")
    private String trackingId;

    @Value("${google.analytics.enabled:true}")
    private boolean enabled;

    @Value("${google.analytics.anonymize-ip:true}")
    private boolean anonymizeIp;

    @Value("${google.analytics.respect-dnt:true}")
    private boolean respectDnt;

    public String getTrackingId() {
        return trackingId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAnonymizeIp() {
        return anonymizeIp;
    }

    public boolean isRespectDnt() {
        return respectDnt;
    }

    /**
     * Genera el código JavaScript optimizado para Google Analytics.
     */
    public String generateAnalyticsCode() {
        if (!enabled) {
            return "<!-- Google Analytics disabled -->";
        }

        return String.format("""
            <!-- Google tag (gtag.js) - Optimized -->
            <script async src="https://www.googletagmanager.com/gtag/js?id=%s"></script>
            <script>
                window.dataLayer = window.dataLayer || [];
                function gtag(){dataLayer.push(arguments);}
                gtag('js', new Date());
                
                // Configuración básica
                gtag('config', '%s', {
                    'anonymize_ip': %s,
                    'respect_dnt': %s,
                    'cookie_flags': 'SameSite=None;Secure',
                    'transport_type': 'beacon'
                });
                
                // Eventos personalizados para seguimiento de rendimiento
                document.addEventListener('DOMContentLoaded', function() {
                    // Track page load time
                    const loadTime = performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart;
                    gtag('event', 'timing_complete', {
                        'name': 'page_load',
                        'value': loadTime,
                        'event_category': 'Page Timings'
                    });
                });
            </script>
            """, 
            trackingId, 
            trackingId,
            anonymizeIp,
            respectDnt
        );
    }

    /**
     * Genera código para eventos específicos de la aplicación.
     */
    public String generateEventTracking(String category, String action, String label) {
        if (!enabled) {
            return "";
        }

        return String.format("""
            <script>
                gtag('event', '%s', {
                    'event_category': '%s',
                    'event_label': '%s',
                    'transport_type': 'beacon'
                });
            </script>
            """, action, category, label);
    }
}