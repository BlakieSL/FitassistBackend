package com.fitassist.backend.integration.test.localization;

import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@ActiveProfiles("test")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
@SpringBootTest
public class LocalizationNotUniqueExceptionTest {

	@Test
	@DisplayName("Test NotUniqueRecordException localization - en")
	public void testNotUniqueRecordExceptionEn() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);

		try {
			throw new NotUniqueRecordException(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("User with identifiers: 1 already exists!", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test NotUniqueRecordException localization - pl")
	public void testNotUniqueRecordExceptionPl() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("pl"));

		try {
			throw new NotUniqueRecordException(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("User z identyfikatorami: 1 już istnieje!", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test NotUniqueRecordException localization - ru")
	public void testNotUniqueRecordExceptionRu() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("ru"));

		try {
			throw new NotUniqueRecordException(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("User с идентификаторами: 1 уже существует!", e.getMessage());
		}
	}

}
