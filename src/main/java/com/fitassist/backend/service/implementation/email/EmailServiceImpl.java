package com.fitassist.backend.service.implementation.email;

import com.fitassist.backend.dto.request.email.EmailRequestDto;
import com.fitassist.backend.service.declaration.email.EmailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

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
			.destination(Destination.builder().toAddresses(emailRequest.getToEmail()).build())
			.message(Message.builder()
				.subject(Content.builder().data(emailRequest.getSubject()).build())
				.body(Body.builder()
					.html(Content.builder().data(emailRequest.getContent()).build())
					.text(Content.builder().data(emailRequest.getContent()).build())
					.build())
				.build())
			.build();

		sesClient.sendEmail(request);
	}

}
