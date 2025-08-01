package source.code.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import source.code.helper.utils.MessageUtils;

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
