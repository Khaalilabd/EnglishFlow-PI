package com.englishflow.messaging.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitConfig {

    @Value("${rate-limit.messages-per-minute:60}")
    private int messagesPerMinute;

    @Bean
    public LoadingCache<Long, RateLimiter> rateLimiterCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build(new CacheLoader<Long, RateLimiter>() {
                    @Override
                    public RateLimiter load(Long userId) {
                        return RateLimiter.create(messagesPerMinute / 60.0);
                    }
                });
    }
}
