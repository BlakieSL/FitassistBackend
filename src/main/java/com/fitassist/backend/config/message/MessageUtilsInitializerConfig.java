package com.fitassist.backend.config.message;

import jakarta.annotation.PostConstruct;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageUtilsInitializerConfig {

	private final MessageSource messageSource;

	public MessageUtilsInitializerConfig(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@PostConstruct
	public void init() {
		MessageUtils.setMessageSource(messageSource);
	}

}
