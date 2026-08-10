package com.rra.project.riotrestapi.service.ratelimiters;

import com.rra.project.riotrestapi.exceptions.code4xx.RateLimitException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ControllerRateLimiter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket buildBucket(){
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(360)
                        .refillGreedy(220, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    public void consumeTokens(HttpServletRequest request, int tokens) {
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, key -> buildBucket());
        if(!bucket.tryConsume(tokens)) throw new RateLimitException("Rate limit exceeded, try again later");
    }

}
