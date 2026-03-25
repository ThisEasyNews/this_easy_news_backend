package com.thiseasynews.server.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableCaching
@Configuration
public class RedisConfig implements CachingConfigurer {

    @Value("${app.cache.hot-keywords-ttl:600}")
    private long hotKeywordsTtl;

    @Value("${app.cache.briefing-ttl:1800}")
    private long briefingTtl;

    @Value("${app.cache.article-list-ttl:300}")
    private long articleListTtl;

    @Value("${app.cache.code-list-ttl:3600}")
    private long codeListTtl;

    // ── 캐시 이름 상수 ───────────────────────────────
    public static final String CACHE_HOT_KEYWORDS    = "hotKeywords";
    public static final String CACHE_TODAY_BRIEFING  = "todayBriefing";
    public static final String CACHE_BRIEFING_DETAIL = "briefingDetail";
    public static final String CACHE_ARTICLE_LIST    = "articleList";
    public static final String CACHE_CODE_LIST       = "codeList";

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put(CACHE_HOT_KEYWORDS,    base.entryTtl(Duration.ofSeconds(hotKeywordsTtl)));
        configs.put(CACHE_TODAY_BRIEFING,  base.entryTtl(Duration.ofSeconds(briefingTtl)));
        configs.put(CACHE_BRIEFING_DETAIL, base.entryTtl(Duration.ofSeconds(briefingTtl)));
        configs.put(CACHE_ARTICLE_LIST,    base.entryTtl(Duration.ofSeconds(articleListTtl)));
        configs.put(CACHE_CODE_LIST,       base.entryTtl(Duration.ofSeconds(codeListTtl)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(base)
                .withInitialCacheConfigurations(configs)
                .build();
    }

    // ── Redis 연결 실패 시 예외 대신 캐시 우회 ────────
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
                log.warn("[Cache] GET 실패 (우회) cache={} key={} error={}", cache.getName(), key, e.getMessage());
            }
            @Override
            public void handleCachePutError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key, @Nullable Object value) {
                log.warn("[Cache] PUT 실패 (우회) cache={} key={} error={}", cache.getName(), key, e.getMessage());
            }
            @Override
            public void handleCacheEvictError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
                log.warn("[Cache] EVICT 실패 (우회) cache={} key={} error={}", cache.getName(), key, e.getMessage());
            }
            @Override
            public void handleCacheClearError(@NonNull RuntimeException e, @NonNull Cache cache) {
                log.warn("[Cache] CLEAR 실패 (우회) cache={} error={}", cache.getName(), e.getMessage());
            }
        };
    }
}
