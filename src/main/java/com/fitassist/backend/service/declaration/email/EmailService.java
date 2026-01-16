package com.fitassist.backend.service.declaration.email;

import com.fitassist.backend.dto.request.email.EmailRequestDto;

public interface EmailService {

	void sendEmail(EmailRequestDto request);

}
