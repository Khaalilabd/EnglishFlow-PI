package com.englishflow.sponsors.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CacheConfigTest {

    @InjectMocks
    private CacheConfig cacheConfig;

    @Test
    void restTemplate_ShouldCreateBean() {
        RestTemplate restTemplate = cacheConfig.restTemplate();

        assertThat(restTemplate).isNotNull();
        assertThat(restTemplate).isInstanceOf(RestTemplate.class);
    }

    @Test
    void cacheManager_ShouldCreateCaffeineCacheManager() {
        CacheManager cacheManager = cacheConfig.cacheManager();

        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager.class);
    }

    @Test
    void cacheManager_ShouldHaveConfiguredCaches() {
        CacheManager cacheManager = cacheConfig.cacheManager();

        assertThat(cacheManager.getCache("sponsors")).isNotNull();
        assertThat(cacheManager.getCache("sponsorsByLevel")).isNotNull();
        assertThat(cacheManager.getCache("sponsorById")).isNotNull();
        assertThat(cacheManager.getCache("sponsorsByStatus")).isNotNull();
    }

    @Test
    void cacheManager_ShouldReturnNullForNonExistentCache() {
        CacheManager cacheManager = cacheConfig.cacheManager();

        assertThat(cacheManager.getCache("nonExistentCache")).isNull();
    }

    @Test
    void cacheManager_ShouldBeCaffeineCacheManager() {
        CacheManager cacheManager = cacheConfig.cacheManager();

        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager.class);
        CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;
        assertThat(caffeineCacheManager.getCacheNames()).contains(
                "sponsors", "sponsorsByLevel", "sponsorById", "sponsorsByStatus"
        );
    }
}
