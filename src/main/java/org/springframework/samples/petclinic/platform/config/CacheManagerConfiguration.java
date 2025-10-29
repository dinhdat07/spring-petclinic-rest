package org.springframework.samples.petclinic.platform.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheManagerConfiguration {

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("vets", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("owners", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("pets", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("petTypes", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("specialties", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
