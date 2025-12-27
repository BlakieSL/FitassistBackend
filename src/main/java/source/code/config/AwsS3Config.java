package source.code.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "true")
public class AwsS3Config {

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Value("${spring.cloud.aws.s3.endpoint:#{null}}")
	private String endpoint;

	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		var builder = S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));

		if (endpoint != null && !endpoint.isEmpty()) {
			builder.endpointOverride(URI.create(endpoint));
		}

		return builder.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		var builder = S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));

		if (endpoint != null && !endpoint.isEmpty()) {
			builder.endpointOverride(URI.create(endpoint));
		}

		return builder.build();
	}

}
