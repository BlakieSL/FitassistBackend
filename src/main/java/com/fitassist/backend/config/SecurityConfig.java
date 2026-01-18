package com.fitassist.backend.config;

import com.fitassist.backend.auth.BearerTokenFilter;
import com.fitassist.backend.auth.CookieService;
import com.fitassist.backend.auth.JwtAuthenticationFilter;
import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.auth.RateLimitingFilter;
import com.fitassist.backend.service.implementation.user.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

	public SecurityConfig(JwtService jwtService, @Lazy UserServiceImpl userServiceImpl,
			@Lazy RateLimitingFilter rateLimitingFilter, CorsConfigurationSource corsConfigurationSource,
			CookieService cookieService) {
		this.jwtService = jwtService;
		this.userServiceImpl = userServiceImpl;
		this.rateLimitingFilter = rateLimitingFilter;
		this.corsConfigurationSource = corsConfigurationSource;
		this.cookieService = cookieService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManagerBuilder authenticationManagerBuilder,
			RequestMatcher requestMatcher) throws Exception {
		AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtService,
				userServiceImpl, cookieService);

		BearerTokenFilter bearerTokenFilter = new BearerTokenFilter(jwtService, cookieService);

		http.authorizeHttpRequests(
				request -> request.requestMatchers(requestMatcher).permitAll().anyRequest().authenticated())
			.cors((cors) -> cors.configurationSource(corsConfigurationSource))
			.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(AbstractHttpConfigurer::disable)
			.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(bearerTokenFilter, JwtAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RequestMatcher publicEndpointsMatcher() {
		return new OrRequestMatcher(
				(request) -> "/api/users/register".equals(request.getRequestURI())
						&& ("POST".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())),
				(request) -> "/api/users/login".equals(request.getRequestURI())
						&& ("POST".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())),
				(request) -> "/api/users/refresh-token".equals(request.getRequestURI())
						&& ("POST".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())),
				(request) -> "/api/users/logout".equals(request.getRequestURI())
						&& ("POST".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())),
				(request) -> "/api/password-reset/request".equals(request.getRequestURI())
						&& ("POST".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())),
				(request) -> "/api/password-reset/reset".equals(request.getRequestURI())
						&& ("POST".equals(request.getMethod()) || "OPTIONS".equals(request.getMethod())),
				(request) -> "/api/virtual-threads/thread-info".equals(request.getRequestURI())
						&& "GET".equals(request.getMethod()),
				(request) -> "/actuator/health".equals(request.getRequestURI()) && "GET".equals(request.getMethod()));
	}

}
