package com.fitassist.backend.integration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.fitassist.backend.dto.request.email.EmailRequestDto;
import com.fitassist.backend.service.declaration.email.EmailService;

@TestConfiguration
public class MockAwsSesConfig {

	@ConditionalOnProperty(name = "spring.cloud.aws.ses.enabled", havingValue = "false", matchIfMissing = true)
	@Bean
	public EmailService emailServiceTestStub() {
		return new EmailService() {
			@Override
			public void sendEmail(EmailRequestDto request) {
			}
		};
	}

}
