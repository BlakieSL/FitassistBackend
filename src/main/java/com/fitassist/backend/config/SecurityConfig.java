package com.fitassist.backend.config;

import com.fitassist.backend.auth.*;
import com.fitassist.backend.service.implementation.user.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtService jwtService;

	private final UserServiceImpl userServiceImpl;

	private final RateLimitingFilter rateLimitingFilter;

	private final CorsConfigurationSource corsConfigurationSource;

	private final CookieService cookieService;

	private final TokenProperties tokenProperties;

	public SecurityConfig(JwtService jwtService, @Lazy UserServiceImpl userServiceImpl,
			@Lazy RateLimitingFilter rateLimitingFilter, CorsConfigurationSource corsConfigurationSource,
			CookieService cookieService, TokenProperties tokenProperties) {
		this.jwtService = jwtService;
		this.userServiceImpl = userServiceImpl;
		this.rateLimitingFilter = rateLimitingFilter;
		this.corsConfigurationSource = corsConfigurationSource;
		this.cookieService = cookieService;
		this.tokenProperties = tokenProperties;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder,
			RequestMatcher requestMatcher) throws Exception {
		AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtService,
				userServiceImpl, cookieService);
		BearerTokenFilter bearerTokenFilter = new BearerTokenFilter(jwtService, cookieService, tokenProperties);

		http.authorizeHttpRequests(
				request -> request.requestMatchers(requestMatcher).permitAll().anyRequest().authenticated())
			.cors((cors) -> cors.configurationSource(corsConfigurationSource))
			.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
				.ignoringRequestMatchers(requestMatcher))
			.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(bearerTokenFilter, JwtAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public RequestMatcher publicEndpointsMatcher() {
		PathPatternRequestMatcher.Builder matcher = PathPatternRequestMatcher.withDefaults();

		return new OrRequestMatcher(matcher.matcher(HttpMethod.POST, "/api/users/register"),
				matcher.matcher(HttpMethod.POST, "/api/users/login"),
				matcher.matcher(HttpMethod.POST, "/api/users/refresh-token"),
				matcher.matcher(HttpMethod.POST, "/api/users/logout"),
				matcher.matcher(HttpMethod.POST, "/api/password-reset/request"),
				matcher.matcher(HttpMethod.POST, "/api/password-reset/reset"),
				matcher.matcher(HttpMethod.GET, "/api/virtual-threads/thread-info"),
				matcher.matcher(HttpMethod.GET, "/actuator/health"));
	}

}
