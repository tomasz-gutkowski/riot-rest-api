package com.rra.project.riotrestapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager scm = new SimpleCacheManager();
        scm.setCaches(List.of(buildCache("matches", 1000, 60)));
        return scm;
    }

    private CaffeineCache buildCache(String name, int size, int expiryInMinutes){
        return new CaffeineCache(name, Caffeine.newBuilder()
                .maximumSize(size)
                .expireAfterWrite(Duration.ofMinutes(expiryInMinutes))
                .build());
    }
}
