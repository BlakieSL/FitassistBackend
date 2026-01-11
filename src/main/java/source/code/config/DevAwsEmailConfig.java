package source.code.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import source.code.dto.request.email.EmailRequestDto;
import source.code.service.declaration.email.EmailService;

@Slf4j
@Configuration
public class DevAwsEmailConfig {

	@ConditionalOnProperty(name = "spring.cloud.aws.ses.enabled", havingValue = "false", matchIfMissing = true)
	@Bean
	public EmailService emailServiceStub() {
		return new EmailService() {
			@Override
			public void sendEmail(EmailRequestDto request) {
				log.info("=== Stub Email Service ===");
				log.info("To : {}", request.getToEmails());
				log.info("Subject : {}", request.getSubject());
				log.info("Content : {}", request.getContent());
			}
		};
	}

}
