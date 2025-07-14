package source.code.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import source.code.service.declaration.aws.AwsS3Service;

@TestConfiguration
public class TestAwsConfig {
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
