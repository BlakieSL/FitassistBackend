package source.code.service.implementation.email;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import source.code.dto.request.email.EmailRequestDto;
import source.code.service.declaration.email.EmailService;

@ConditionalOnProperty(name = "spring.cloud.aws.ses.enabled", havingValue = "true")
@Service
public class EmailServiceImpl implements EmailService {

	private final SesClient sesClient;

	public EmailServiceImpl(SesClient sesClient) {
		this.sesClient = sesClient;
	}

	@Override
	public void sendEmail(EmailRequestDto emailRequest) {
		SendEmailRequest request = SendEmailRequest.builder()
			.source(emailRequest.getFromEmail())
			.destination(Destination.builder().toAddresses(emailRequest.getToEmails()).build())
			.message(Message.builder()
				.subject(Content.builder().data(emailRequest.getSubject()).build())
				.body(Body.builder()
					.html(emailRequest.isHtml() ? Content.builder().data(emailRequest.getContent()).build() : null)
					.text(!emailRequest.isHtml() ? Content.builder().data(emailRequest.getContent()).build() : null)
					.build())
				.build())
			.build();

		sesClient.sendEmail(request);
	}

}
