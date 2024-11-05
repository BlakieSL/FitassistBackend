package source.code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {
    private final RedissonInterceptor redissonInterceptor;

    public AppConfig(RedissonInterceptor redissonInterceptor) {
        this.redissonInterceptor = redissonInterceptor;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(redissonInterceptor)
                        .addPathPatterns("/**");
            }
        };
    }
}
