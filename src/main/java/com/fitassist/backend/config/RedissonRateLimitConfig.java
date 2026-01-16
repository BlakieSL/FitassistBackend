package com.fitassist.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
@Getter
@Setter
public class RedissonRateLimitConfig {

	private int userRate;

	private int userInterval;

	private int keyRate;

	private int keyInterval;

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Bean
	@ConditionalOnProperty(name = "redis-flag.enabled", havingValue = "true")
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + redisHost + ":" + redisPort)
			.setConnectionMinimumIdleSize(10)
			.setConnectTimeout(10000);
		return Redisson.create(config);
	}

}
