package com.fitassist.backend.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration(exclude = RedisRepositoriesAutoConfiguration.class)
@Configuration
@EnableCaching
public class RedisCachingConfig {

	@ConditionalOnProperty(name = "redis-flag.enabled", havingValue = "true")
	@Bean
	@Primary
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		CaffeineCacheManager caffeineManager = new CaffeineCacheManager();
		caffeineManager
			.setCaffeine(Caffeine.newBuilder().maximumSize(5000).expireAfterWrite(2, TimeUnit.MINUTES).recordStats());

		JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

		RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(15))
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer));

		RedisCacheManager redisManager = RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(redisCacheConfig)
			.build();

		return new CompositeCacheManager(caffeineManager, redisManager);
	}

}
