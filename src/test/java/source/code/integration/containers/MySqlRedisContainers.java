package source.code.integration.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class MySqlRedisContainers {
    static Network network = Network.newNetwork();

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .withNetwork(network);

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
            .withNetwork(network)
            .withDatabaseName("main-db")
            .withUsername("root")
            .withPassword("root");

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.4"))
            .withNetwork(network)
            .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));

        registry.add("spring.cloud.aws.s3.enabled", () -> "true");
        registry.add("spring.cloud.aws.s3.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("spring.cloud.aws.credentials.access-key", () -> "test");
        registry.add("spring.cloud.aws.credentials.secret-key", () -> "test");
        registry.add("spring.cloud.aws.region.static", () -> localStack.getRegion());
        registry.add("s3.bucket.name", () -> "test-bucket");
    }
}
