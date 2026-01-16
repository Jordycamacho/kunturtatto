package com.example.kunturtatto.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cache")
public class CacheMonitorController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                Object nativeCache = cache.getNativeCache();
                cacheInfo.put("nativeCacheType", nativeCache.getClass().getSimpleName());

                if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                    com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                    cacheInfo.put("estimatedSize", caffeineCache.estimatedSize());
                    cacheInfo.put("stats", caffeineCache.stats());
                }

                stats.put(cacheName, cacheInfo);
            }
        });

        return stats;
    }

    @GetMapping("/users/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        String[] userCaches = { "usersAll", "userById", "userByEmail", "authenticatedUser" };

        for (String cacheName : userCaches) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                Object nativeCache = cache.getNativeCache();

                if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                    com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                    cacheInfo.put("estimatedSize", caffeineCache.estimatedSize());
                    cacheInfo.put("stats", caffeineCache.stats().toString());
                    cacheInfo.put("hitRate", caffeineCache.stats().hitRate());
                    cacheInfo.put("missRate", caffeineCache.stats().missRate());
                    cacheInfo.put("loadSuccessCount", caffeineCache.stats().loadSuccessCount());
                    cacheInfo.put("loadFailureCount", caffeineCache.stats().loadFailureCount());
                }

                stats.put(cacheName, cacheInfo);
            }
        }

        log.info("[CACHE_CONTROLLER] Obteniendo estadísticas de cache de usuarios");
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/clear/{cacheName}")
    public ResponseEntity<Void> clearCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("[CACHE_CONTROLLER] Cache limpiado: {}", cacheName);
            return ResponseEntity.ok().build();
        }
        log.warn("[CACHE_CONTROLLER] Cache no encontrado: {}", cacheName);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/clear-all")
    public ResponseEntity<Void> clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        log.info("[CACHE_CONTROLLER] Todos los caches han sido limpiados");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email-audit/stats")
    public ResponseEntity<Map<String, Object>> getEmailAuditCacheStats() {
        Cache emailAuditCache = cacheManager.getCache("emailAuditStats");
        Map<String, Object> stats = new HashMap<>();

        if (emailAuditCache != null) {
            Object nativeCache = emailAuditCache.getNativeCache();
            if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                stats.put("estimatedSize", caffeineCache.estimatedSize());
                stats.put("stats", caffeineCache.stats().toString());
                stats.put("hitRate", caffeineCache.stats().hitRate());
            }
        }

        return ResponseEntity.ok(stats);
    }
}