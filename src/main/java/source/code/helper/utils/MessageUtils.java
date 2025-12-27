package source.code.helper.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public final class MessageUtils {

	private static MessageSource messageSource;

	private MessageUtils() {
		throw new AssertionError("Utility class should not be instantiated");
	}

	public static void setMessageSource(MessageSource messageSource) {
		MessageUtils.messageSource = messageSource;
	}

	public static String getMessage(String code, Object... args) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(code, args, locale);
	}

}
