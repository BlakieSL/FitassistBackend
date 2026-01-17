package com.fitassist.backend.integration.test.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.dto.request.auth.PasswordResetDto;
import com.fitassist.backend.dto.request.auth.PasswordResetRequestDto;
import com.fitassist.backend.integration.config.MockAwsS3Config;
import com.fitassist.backend.integration.config.MockAwsSesConfig;
import com.fitassist.backend.integration.config.MockRedisConfig;
import com.fitassist.backend.integration.containers.MySqlContainerInitializer;
import com.fitassist.backend.integration.utils.TestSetup;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class PasswordResetControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /request - Should send password reset email for existing user")
	void requestPasswordReset_Success() throws Exception {
		PasswordResetRequestDto request = new PasswordResetRequestDto();
		request.setEmail("user1@example.com");

		mockMvc
			.perform(post("/api/password-reset/request").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /request - Should return 404 for non-existent user")
	void requestPasswordReset_UserNotFound() throws Exception {
		PasswordResetRequestDto request = new PasswordResetRequestDto();
		request.setEmail("nonexistent@example.com");

		mockMvc
			.perform(post("/api/password-reset/request").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /request - Should return 400 for invalid email")
	void requestPasswordReset_InvalidEmail() throws Exception {
		PasswordResetRequestDto request = new PasswordResetRequestDto();
		request.setEmail("invalid-email");

		mockMvc
			.perform(post("/api/password-reset/request").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /reset - Should reset password with valid token")
	void resetPassword_Success() throws Exception {

		String token = jwtService.createSignedJWT("user1@example.com", 1, Collections.emptyList(), 20,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(token);
		resetDto.setNewPassword("NewPassword123!");

		mockMvc
			.perform(post("/api/password-reset/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetDto)))
			.andExpect(status().isOk());

		User user = userRepository.findByEmail("user1@example.com").orElseThrow();
		assertThat(passwordEncoder.matches("NewPassword123!", user.getPassword())).isTrue();
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /reset - Should return 401 for invalid token")
	void resetPassword_InvalidToken() throws Exception {
		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken("invalid-token");
		resetDto.setNewPassword("NewPassword123!");

		mockMvc
			.perform(post("/api/password-reset/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetDto)))
			.andExpect(status().isUnauthorized());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /reset - Should return 401 for expired token")
	void resetPassword_ExpiredToken() throws Exception {
		String token = jwtService.createSignedJWT("user1@example.com", 1, Collections.emptyList(), -1,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(token);
		resetDto.setNewPassword("NewPassword123!");

		mockMvc
			.perform(post("/api/password-reset/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetDto)))
			.andExpect(status().isUnauthorized());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /reset - Should return 401 for wrong token type")
	void resetPassword_WrongTokenType() throws Exception {
		String token = jwtService.createAccessToken("user1@example.com", 1, Collections.singletonList("ROLE_USER"));

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(token);
		resetDto.setNewPassword("NewPassword123!");

		mockMvc
			.perform(post("/api/password-reset/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetDto)))
			.andExpect(status().isUnauthorized());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /reset - Should return 400 for weak password")
	void resetPassword_WeakPassword() throws Exception {
		String token = jwtService.createSignedJWT("user1@example.com", 1, Collections.emptyList(), 20,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(token);
		resetDto.setNewPassword("weak");

		mockMvc
			.perform(post("/api/password-reset/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetDto)))
			.andExpect(status().isBadRequest());
	}

	@WithAnonymousUser
	@PasswordResetSql
	@Test
	@DisplayName("POST - /reset - Should return 404 for non-existent user in token")
	void resetPassword_UserNotFoundInToken() throws Exception {
		String token = jwtService.createSignedJWT("nonexistent@example.com", 999, Collections.emptyList(), 20,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(token);
		resetDto.setNewPassword("NewPassword123!");

		mockMvc
			.perform(post("/api/password-reset/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetDto)))
			.andExpect(status().isNotFound());
	}

}
