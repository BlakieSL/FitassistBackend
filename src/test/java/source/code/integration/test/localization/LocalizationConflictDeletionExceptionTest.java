package source.code.integration.test.localization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import source.code.exception.ConflictDeletionException;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.model.user.User;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@ActiveProfiles("test")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
@SpringBootTest
public class LocalizationConflictDeletionExceptionTest {

	@Test
	@DisplayName("Test ConflictDeletionException localization - en")
	public void testConflictDeletionExceptionEn() throws Exception {
		LocaleContextHolder.setLocale(Locale.ENGLISH);

		try {
			throw new ConflictDeletionException(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("Cannot delete User with ID: 1 because it is referenced by other entities!", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test ConflictDeletionException localization - pl")
	public void testConflictDeletionExceptionPl() throws Exception {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("pl"));

		try {
			throw new ConflictDeletionException(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("Nie można usunąć User z ID: 1, ponieważ jest ona używana przez inne encje!", e.getMessage());
		}
	}

	@Test
	@DisplayName("Test ConflictDeletionException localization - ru")
	public void testConflictDeletionExceptionRu() throws Exception {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("ru"));

		try {
			throw new ConflictDeletionException(User.class, 1);
		}
		catch (Exception e) {
			assertEquals("Невозможно удалить User с ID: 1, потому что она используется другими сущностями!",
					e.getMessage());
		}
	}

}
