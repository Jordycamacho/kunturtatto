package com.example.kunturtatto.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "subcategoriesAll",
            "subcategoriesByCategory", 
            "subcategoryById",
            "subcategoriesByCategoryRepo",
            "subcategoryWithDesigns",
            "categoriesList",
            "categoriesWithSubcategories",
            "categoryById"
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .recordStats()
            .weakKeys()
            .removalListener((key, value, cause) -> 
                System.out.printf("Cache entry removed: Key=%s, Cause=%s%n", key, cause))
        );
        
        return cacheManager;
    }
    
    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(1000);
    }
}