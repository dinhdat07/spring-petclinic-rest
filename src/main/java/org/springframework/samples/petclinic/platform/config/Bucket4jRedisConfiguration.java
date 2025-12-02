package org.springframework.samples.petclinic.platform.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.giffing.bucket4j.spring.boot.starter.config.cache.AsyncCacheResolver;
import com.giffing.bucket4j.spring.boot.starter.config.cache.CacheManager;
import com.giffing.bucket4j.spring.boot.starter.config.cache.redis.lettuce.LettuceCacheManager;
import com.giffing.bucket4j.spring.boot.starter.config.cache.redis.lettuce.LettuceCacheResolver;
import com.giffing.bucket4j.spring.boot.starter.context.properties.Bucket4JBootProperties;
import com.giffing.bucket4j.spring.boot.starter.context.properties.Bucket4JConfiguration;
import com.giffing.bucket4j.spring.boot.starter.config.cache.SyncCacheResolver;

import io.github.bucket4j.distributed.proxy.AbstractProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

/**
 * Provides the Redis client and Bucket4j cache resolver for rate-limiting.
 */
@Configuration
class Bucket4jRedisConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedisClient.class)
    RedisClient bucket4jRedisClient(RedisProperties properties) {
        RedisURI.Builder builder = StringUtils.hasText(properties.getUrl())
            ? RedisURI.builder(RedisURI.create(properties.getUrl()))
            : RedisURI.builder()
                .withHost(properties.getHost())
                .withPort(properties.getPort());

        String password = properties.getPassword();
        String username = properties.getUsername();
        if (StringUtils.hasText(username)) {
            builder.withAuthentication(username, password != null ? password.toCharArray() : new char[0]);
        } else if (StringUtils.hasText(password)) {
            builder.withPassword(password);
        }

        boolean sslEnabled = properties.getSsl() != null && properties.getSsl().isEnabled();
        builder.withSsl(sslEnabled);

        return RedisClient.create(builder.build());
    }

    @Bean
    @ConditionalOnMissingBean(AsyncCacheResolver.class)
    AsyncCacheResolver bucket4jAsyncCacheResolver(RedisClient redisClient) {
        return new LettuceCacheResolver(redisClient);
    }

    @Bean
    @ConditionalOnMissingBean(SyncCacheResolver.class)
    SyncCacheResolver bucket4jSyncCacheResolver(RedisClient redisClient) {
        return new LettuceSyncResolver(redisClient);
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    CacheManager<String, Bucket4JConfiguration> bucket4jConfigCacheManager(
        RedisClient redisClient,
        Bucket4JBootProperties properties
    ) {
        String cacheName = StringUtils.hasText(properties.getFilterConfigCacheName())
            ? properties.getFilterConfigCacheName()
            : "bucket4j-config";
        return new ExposedLettuceCacheManager<>(redisClient, cacheName, Bucket4JConfiguration.class);
    }

    /**
     * Bucket4j's Lettuce cache manager has a protected constructor; we expose it via a simple subclass.
     */
    static class ExposedLettuceCacheManager<K, V> extends LettuceCacheManager<K, V> {
        ExposedLettuceCacheManager(RedisClient client, String cacheName, Class<V> valueType) {
            super(client, cacheName, valueType);
        }
    }

    /**
     * Simple sync resolver that mirrors LettuceCacheResolver behaviour but reports sync.
     */
    static class LettuceSyncResolver extends com.giffing.bucket4j.spring.boot.starter.config.cache.AbstractCacheResolverTemplate<byte[]>
        implements SyncCacheResolver {

        private final RedisClient redisClient;

        LettuceSyncResolver(RedisClient redisClient) {
            this.redisClient = redisClient;
        }

        @Override
        public byte[] castStringToCacheKey(String key) {
            return key.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }

        @Override
        public boolean isAsync() {
            return false;
        }

        @Override
        public AbstractProxyManager<byte[]> getProxyManager(String cacheName) {
            return LettuceBasedProxyManager.builderFor(redisClient)
                .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(java.time.Duration.ofSeconds(10)))
                .build();
        }
    }
}
