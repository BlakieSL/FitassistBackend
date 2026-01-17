package com.fitassist.backend.service.implementation.auth;

import com.fitassist.backend.auth.JwtService;
import com.fitassist.backend.dto.request.auth.PasswordResetDto;
import com.fitassist.backend.dto.request.auth.PasswordResetRequestDto;
import com.fitassist.backend.dto.request.email.EmailRequestDto;
import com.fitassist.backend.exception.JwtAuthenticationException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.auth.PasswordResetService;
import com.fitassist.backend.service.declaration.email.EmailService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

	private static final String PASSWORD_RESET_TOKEN_TYPE = "PASSWORD_RESET";

	private static final long PASSWORD_RESET_TOKEN_DURATION_MINUTES = 20;

	private final JwtService jwtService;

	private final EmailService emailService;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Value("${app.frontend.url}")
	private String frontendUrl;

	@Value("${app.email.from}")
	private String fromEmail;

	public PasswordResetServiceImpl(JwtService jwtService, EmailService emailService, UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.jwtService = jwtService;
		this.emailService = emailService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void requestPasswordReset(PasswordResetRequestDto request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> RecordNotFoundException.of(User.class, request.getEmail()));

		String resetToken = generatePasswordResetToken(user.getEmail(), user.getId());
		String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

		EmailRequestDto emailRequest = new EmailRequestDto(fromEmail, List.of(user.getEmail()),
				"Password Reset Request", buildEmailContent(user.getUsername(), resetLink));

		emailService.sendEmail(emailRequest);
	}

	@Override
	@Transactional
	public void resetPassword(PasswordResetDto resetDto) {
		try {
			SignedJWT signedJWT = (SignedJWT) JWTParser.parse(resetDto.getToken());
			jwtService.verifySignature(signedJWT);
			jwtService.verifyExpirationTime(signedJWT);

			JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

			String tokenType = claims.getStringClaim("tokenType");
			if (!PASSWORD_RESET_TOKEN_TYPE.equals(tokenType)) {
				throw new JwtAuthenticationException("Invalid token type for password reset");
			}

			String email = claims.getSubject();
			Integer userId = claims.getIntegerClaim("userId");

			if (email == null || userId == null) {
				throw new JwtAuthenticationException("Invalid token claims");
			}

			User user = userRepository.findByEmail(email)
				.orElseThrow(() -> RecordNotFoundException.of(User.class, email));

			if (!user.getId().equals(userId)) {
				throw new JwtAuthenticationException("Token user ID mismatch");
			}

			String encodedPassword = passwordEncoder.encode(resetDto.getNewPassword());
			user.setPassword(encodedPassword);
			userRepository.save(user);

		}
		catch (ParseException e) {
			throw new JwtAuthenticationException("Invalid password reset token");
		}
	}

	private String generatePasswordResetToken(String email, Integer userId) {
		return jwtService.createSignedJWT(email, userId, Collections.emptyList(), PASSWORD_RESET_TOKEN_DURATION_MINUTES,
				PASSWORD_RESET_TOKEN_TYPE);
	}

	private String buildEmailContent(String username, String resetLink) {
		return String.format(
				"""
						<!DOCTYPE html>
						<html>
						<body>
						    <div>
						        <h2>Password Reset Request</h2>
						        <p>Hello %s,</p>
						        <p>We received a request to reset your password. Click the button below to reset your password:</p>
						        <a href="%s" class="button">Reset Password</a>
						        <p>This link will expire in 20 minutes.</p>
						        <p>If you didn't request a password reset, please ignore this email.</p>
						        <div>
						            <p>This is an automated message, please do not reply.</p>
						        </div>
						    </div>
						</body>
						</html>
						""",
				username, resetLink);
	}

}
