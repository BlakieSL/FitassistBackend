package source.code.integration.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Testcontainers
public class AwsS3ContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.4"))
            .withServices(LocalStackContainer.Service.S3);

    static {
        localStack.start();
        createS3Bucket();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String endpoint = localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString();

        TestPropertyValues.of(
                "spring.cloud.aws.s3.enabled=true",
                "spring.cloud.aws.s3.endpoint=" + endpoint,
                "spring.cloud.aws.credentials.access-key=test",
                "spring.cloud.aws.credentials.secret-key=test",
                "spring.cloud.aws.region.static=" + localStack.getRegion(),
                "s3.bucket.name=test-bucket"
        ).applyTo(applicationContext.getEnvironment());
    }

    private static void createS3Bucket() {
        try {
            S3Client s3Client = S3Client.builder()
                    .endpointOverride(URI.create(localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString()))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("text", "text")))
                    .region(Region.of(localStack.getRegion()))
                    .build();

            s3Client.createBucket(builder -> builder.bucket("test-bucket"));
        } catch (Exception e) {
            System.err.println("S3 bucket creation failed: " + e.getMessage());
        }
    }
}