package source.code.integration.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MySqlContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.43")
            .withDatabaseName("main-db")
            .withUsername("root")
            .withPassword("root");

    static {
        mySQLContainer.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + mySQLContainer.getUsername(),
                "spring.datasource.password=" + mySQLContainer.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
