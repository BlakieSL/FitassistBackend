package source.code.integration.test.localization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import source.code.exception.RecordNotFoundException;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.model.user.User;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureMockMvc
@Import({MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {MySqlContainerInitializer.class})
@SpringBootTest
public class LocalizationRecordNotFoundExceptionTest {
    @Test
    @DisplayName("Test manual localization setup en")
    public void testManualLocalizationSetupEn() throws Exception {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        try {
            throw RecordNotFoundException.of(User.class, 1);
        } catch (Exception e) {
            assertEquals("User not found for identifiers: 1!", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test manual localization setup pl")
    public void testManualLocalizationSetupPl() throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("pl"));

        try {
            throw RecordNotFoundException.of(User.class, 1);
        } catch (Exception e) {
            assertEquals("User nie znaleziono dla identyfikatorów: 1!", e.getMessage());
        }
    }

    @Test
    @DisplayName("Test manual localization setup ru")
    public void testManualLocalizationSetupRu() throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("ru"));

        try {
            throw RecordNotFoundException.of(User.class, 1);
        } catch (Exception e) {
            assertEquals("User не найден для идентификаторов: 1!", e.getMessage());
        }
    }

}
