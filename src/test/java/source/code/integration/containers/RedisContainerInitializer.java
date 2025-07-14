package source.code.integration.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

public class RedisContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);

    static {
        redisContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.data.redis.host=" + redisContainer.getHost(),
                "spring.data.redis.port=" + redisContainer.getMappedPort(6379)
        ).applyTo(applicationContext.getEnvironment());
    }
}
