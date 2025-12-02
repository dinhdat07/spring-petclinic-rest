package org.springframework.samples.petclinic.platform.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.samples.petclinic.catalog.domain.Specialty;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.visits.domain.Visit;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheManagerConfiguration {

        @Bean
        @Primary
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

                Jackson2JsonRedisSerializer<Vet> vetSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                                Vet.class);
                Jackson2JsonRedisSerializer<Specialty> specialtySerializer = new Jackson2JsonRedisSerializer<>(
                                objectMapper,
                                Specialty.class);
                Jackson2JsonRedisSerializer<Owner> ownerSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                                Owner.class);
                Jackson2JsonRedisSerializer<Pet> petSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                                Pet.class);
                Jackson2JsonRedisSerializer<Visit> visitSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
                                Visit.class);

                JavaType specialtyJavaType = objectMapper.getTypeFactory().constructCollectionType(Collection.class,
                                Specialty.class);
                Jackson2JsonRedisSerializer<Collection<Specialty>> specialtiesAllSerializer = new Jackson2JsonRedisSerializer<>(
                                objectMapper, specialtyJavaType);

                JavaType vetJavaType = objectMapper.getTypeFactory().constructCollectionType(Collection.class,
                                Vet.class);
                Jackson2JsonRedisSerializer<Collection<Vet>> vetsAllSerializer = new Jackson2JsonRedisSerializer<>(
                                objectMapper, vetJavaType);

                JavaType ownerJavaType = objectMapper.getTypeFactory().constructCollectionType(Collection.class,
                                Owner.class);
                Jackson2JsonRedisSerializer<Collection<Owner>> ownersAllSerializer = new Jackson2JsonRedisSerializer<>(
                                objectMapper, ownerJavaType);

                JavaType visitJavaType = objectMapper.getTypeFactory().constructCollectionType(Collection.class,
                                Visit.class);
                Jackson2JsonRedisSerializer<Collection<Visit>> visitsAllSerializer = new Jackson2JsonRedisSerializer<>(
                                objectMapper, visitJavaType);

                // Cấu hình RedisCacheConfiguration cho mỗi cache riêng biệt với TTL khác nhau
                RedisCacheConfiguration vetCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5)) // TTL cho cache vets
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(vetSerializer));

                RedisCacheConfiguration ownerCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(15)) // TTL cho cache owners
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(ownerSerializer));

                RedisCacheConfiguration petCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5)) // TTL cho cache pets
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(petSerializer));

                RedisCacheConfiguration specialtyCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)) // TTL cho cache specialties
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(specialtySerializer));

                RedisCacheConfiguration petTypeCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)) // TTL cho cache petTypes
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(specialtySerializer));

                RedisCacheConfiguration visitCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5)) // TTL cho cache vets
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(visitSerializer));

                RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .disableCachingNullValues()
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

                RedisCacheConfiguration specialtiesAllCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)) // TTL cho cache specialties_all
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(specialtiesAllSerializer));
                RedisCacheConfiguration vetsAllCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)) // TTL cho cache specialties_all
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(vetsAllSerializer));
                RedisCacheConfiguration ownersAllCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)) // TTL cho cache specialties_all
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(ownersAllSerializer));
                RedisCacheConfiguration visitsAllCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(60)) // TTL cho cache specialties_all
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(visitsAllSerializer));

                Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
                cacheConfigurations.put("vets", vetCacheConfig);
                cacheConfigurations.put("specialties", specialtyCacheConfig);
                cacheConfigurations.put("owners", ownerCacheConfig);
                cacheConfigurations.put("pets", petCacheConfig);
                cacheConfigurations.put("visits", visitCacheConfig);
                cacheConfigurations.put("specialties_all", specialtiesAllCacheConfig);
                cacheConfigurations.put("vets_all", vetsAllCacheConfig);
                cacheConfigurations.put("owners_all", ownersAllCacheConfig);
                cacheConfigurations.put("visits_all", visitsAllCacheConfig);

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaultCacheConfig)
                                .withInitialCacheConfigurations(cacheConfigurations)
                                .build();
        }
}
