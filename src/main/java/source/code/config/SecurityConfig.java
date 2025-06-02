package source.code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import source.code.auth.BearerTokenFilter;
import source.code.auth.JwtAuthenticationFilter;
import source.code.auth.JwtService;
import source.code.auth.RateLimitingFilter;
import source.code.service.implementation.user.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtService jwtService;
    private final UserServiceImpl userServiceImpl;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityConfig(JwtService jwtService,
                          @Lazy UserServiceImpl userServiceImpl,
                          @Lazy RateLimitingFilter rateLimitingFilter
    ) {
        this.jwtService = jwtService;
        this.userServiceImpl = userServiceImpl;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            MvcRequestMatcher.Builder mvc,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            RequestMatcher requestMatcher)
            throws Exception {
        AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtService, userServiceImpl);

        BearerTokenFilter bearerTokenFilter = new BearerTokenFilter(jwtService);

        http.authorizeHttpRequests(request -> request
                .requestMatchers(requestMatcher).permitAll()
                .anyRequest().authenticated());
        http.cors((cors) -> cors
                .configurationSource(corsConfigurationSource()));


        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);

        http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(bearerTokenFilter, JwtAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RequestMatcher publicEndpointsMatcher(MvcRequestMatcher.Builder mvc) {
        return new OrRequestMatcher(
                mvc.pattern("/api/users/register"),
                mvc.pattern("/api/users/login"),
                mvc.pattern("/api/users/refresh-token")
        );
    }
}
