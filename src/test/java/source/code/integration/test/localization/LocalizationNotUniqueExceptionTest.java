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
import source.code.exception.NotUniqueRecordException;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.model.user.User;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@Import({MockAwsS3Config.class, MockRedisConfig.class})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
@SpringBootTest
public class LocalizationNotUniqueExceptionTest {
    @Test
    @DisplayName("Test NotUniqueRecordException localization - en")
    public void testNotUniqueRecordExceptionEn() throws Exception {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        try {
            throw new NotUniqueRecordException(User.class, 1);
        } catch (Exception e) {
            assertEquals("User with identifiers: 1 already exists!", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test NotUniqueRecordException localization - pl")
    public void testNotUniqueRecordExceptionPl() throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("pl"));

        try {
            throw new NotUniqueRecordException(User.class, 1);
        } catch (Exception e) {
            assertEquals("User z identyfikatorami: 1 już istnieje!", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test NotUniqueRecordException localization - ru")
    public void testNotUniqueRecordExceptionRu() throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("ru"));

        try {
            throw new NotUniqueRecordException(User.class, 1);
        } catch (Exception e) {
            assertEquals("User с идентификаторами: 1 уже существует!", e.getMessage());
        }
    }
}
