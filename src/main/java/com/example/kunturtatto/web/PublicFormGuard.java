package com.example.kunturtatto.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Filtro del formulario público. Un envío automático no se guarda ni manda correo.
 */
@Component
public class PublicFormGuard {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = TimeUnit.MINUTES.toMillis(60);
    private static final long MIN_FILL_MS = 4_000;
    private static final long MAX_AGE_MS = TimeUnit.HOURS.toMillis(6);

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean looksAutomated(HttpServletRequest request) {
        String honeypot = request.getParameter("website");
        if (honeypot != null && !honeypot.isBlank()) {
            return true;
        }
        String started = request.getParameter("formStartedAt");
        if (started == null || started.isBlank()) {
            return true;
        }
        try {
            long elapsed = System.currentTimeMillis() - Long.parseLong(started.trim());
            return elapsed < MIN_FILL_MS || elapsed > MAX_AGE_MS;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    public boolean isRateLimited(HttpServletRequest request, String action) {
        Attempt attempt = attempts.get(key(request, action));
        if (attempt == null) {
            return false;
        }
        if (System.currentTimeMillis() - attempt.startedAt >= WINDOW_MS) {
            attempts.remove(key(request, action));
            return false;
        }
        return attempt.count >= MAX_ATTEMPTS;
    }

    public void record(HttpServletRequest request, String action) {
        long now = System.currentTimeMillis();
        attempts.compute(key(request, action), (ignored, existing) -> {
            if (existing == null || now - existing.startedAt >= WINDOW_MS) {
                Attempt fresh = new Attempt();
                fresh.startedAt = now;
                fresh.count = 1;
                return fresh;
            }
            existing.count++;
            return existing;
        });
    }

    private String key(HttpServletRequest request, String action) {
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isBlank()) {
            ip = "desconocida";
        }
        return action + "|" + ip;
    }

    private static final class Attempt {
        private int count;
        private long startedAt;
    }
}
