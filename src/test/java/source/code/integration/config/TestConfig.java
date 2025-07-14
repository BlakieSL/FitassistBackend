package source.code.integration.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import source.code.auth.RateLimitingFilter;
import source.code.service.declaration.aws.AwsS3Service;

import java.io.IOException;


@TestConfiguration
public class TestConfig {
    @Bean
    public RateLimitingFilter rateLimitingFilter() {
        return new RateLimitingFilter(null, null) {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                filterChain.doFilter(request, response);
            }
        };
    }

    @ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "false", matchIfMissing = true)
    @Bean
    public AwsS3Service awsS3ServiceStub() {
        return new AwsS3Service() {
            @Override
            public String uploadImage(byte[] imageBytes) {
                return "";
            }

            @Override
            public String getImage(String imageName) {
                return "";
            }

            @Override
            public void deleteImage(String imageName) {

            }
        };
    }
}
