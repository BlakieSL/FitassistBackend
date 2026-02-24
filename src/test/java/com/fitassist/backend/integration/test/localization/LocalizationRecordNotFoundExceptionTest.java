package com.fitassist.backend.integration.test.localization;

import com.fitassist.backend.exception.RecordNotFoundException;
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
public class LocalizationRecordNotFoundExceptionTest {

	@Test
	@DisplayName("Test manual localization setup en")
	public void testManualLocalizationSetupEn() throws Exception {
		LocaleContextHolder.setLocale(Locale.ENGLISH);

		try {
			throw RecordNotFoundException.of(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("User not found for identifiers: 1!", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test manual localization setup pl")
	public void testManualLocalizationSetupPl() throws Exception {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("pl"));

		try {
			throw RecordNotFoundException.of(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("User nie znaleziono dla identyfikatorów: 1!", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test manual localization setup ru")
	public void testManualLocalizationSetupRu() throws Exception {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("ru"));

		try {
			throw RecordNotFoundException.of(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("User не найден для идентификаторов: 1!", e.getMessage());
		}
	}

}
