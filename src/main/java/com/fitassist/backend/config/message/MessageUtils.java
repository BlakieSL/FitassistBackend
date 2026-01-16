package com.fitassist.backend.config.message;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public final class MessageUtils {

	private static MessageSource messageSource;

	public static void setMessageSource(MessageSource messageSource) {
		MessageUtils.messageSource = messageSource;
	}

	public static String getMessage(String code, Object... args) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(code, args, locale);
	}

}
