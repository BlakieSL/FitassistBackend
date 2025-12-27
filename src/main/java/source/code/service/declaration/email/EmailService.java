package source.code.service.declaration.email;

import source.code.dto.request.email.EmailRequestDto;

public interface EmailService {

	void sendEmail(EmailRequestDto request);

}
