package source.code.integration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import source.code.dto.request.email.EmailRequestDto;
import source.code.service.declaration.email.EmailService;

@TestConfiguration
public class MockAwsSesConfig {

	@ConditionalOnProperty(name = "spring.cloud.aws.ses.enabled", havingValue = "false", matchIfMissing = true)
	@Bean
	public EmailService emailServiceStub() {
		return new EmailService() {
			@Override
			public void sendEmail(EmailRequestDto request) {
			}
		};
	}

}
