package source.code.unit.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import source.code.auth.JwtService;
import source.code.dto.request.auth.PasswordResetDto;
import source.code.dto.request.auth.PasswordResetRequestDto;
import source.code.dto.request.email.EmailRequestDto;
import source.code.exception.JwtAuthenticationException;
import source.code.exception.RecordNotFoundException;
import source.code.model.user.User;
import source.code.repository.UserRepository;
import source.code.service.declaration.email.EmailService;
import source.code.service.implementation.auth.PasswordResetServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private EmailService emailService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private PasswordResetServiceImpl passwordResetService;

	private User user;

	private String email;

	private Integer userId;

	private String resetToken;

	@BeforeEach
	void setUp() {
		email = "test@example.com";
		userId = 1;
		resetToken = "valid.reset.token";

		user = new User();
		user.setId(userId);
		user.setEmail(email);
		user.setUsername("testuser");
		user.setPassword("oldPassword");

		ReflectionTestUtils.setField(passwordResetService, "frontendUrl", "http://localhost:3000");
		ReflectionTestUtils.setField(passwordResetService, "fromEmail", "noreply@fitassist.com");
	}

	@Test
	void requestPasswordReset_shouldSendEmailForValidUser() {
		PasswordResetRequestDto request = new PasswordResetRequestDto();
		request.setEmail(email);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(jwtService.createSignedJWT(eq(email), eq(userId), eq(Collections.emptyList()), eq(20L),
				eq("PASSWORD_RESET")))
			.thenReturn(resetToken);

		passwordResetService.requestPasswordReset(request);

		ArgumentCaptor<EmailRequestDto> emailCaptor = ArgumentCaptor.forClass(EmailRequestDto.class);
		verify(emailService).sendEmail(emailCaptor.capture());

		EmailRequestDto sentEmail = emailCaptor.getValue();
		assertEquals("noreply@fitassist.com", sentEmail.getFromEmail());
		assertTrue(sentEmail.getToEmails().contains(email));
		assertEquals("Password Reset Request", sentEmail.getSubject());
		assertTrue(sentEmail.getContent().contains(resetToken));
		assertTrue(sentEmail.isHtml());
	}

	@Test
	void requestPasswordReset_shouldThrowWhenUserNotFound() {
		PasswordResetRequestDto request = new PasswordResetRequestDto();
		request.setEmail("nonexistent@example.com");

		when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> passwordResetService.requestPasswordReset(request));

		verifyNoInteractions(emailService);
		verifyNoInteractions(jwtService);
	}

	@Test
	void resetPassword_shouldUpdatePasswordForValidToken() throws Exception {
		String sharedKey = "thisIsASecretKeyForTestingPurposesThatIsLongEnough";
		JwtService realJwtService = new JwtService(sharedKey);
		String validToken = realJwtService.createSignedJWT(email, userId, Collections.emptyList(), 20,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(validToken);
		resetDto.setNewPassword("newPassword123");

		PasswordResetServiceImpl serviceWithRealJwt = new PasswordResetServiceImpl(realJwtService, emailService,
				userRepository, passwordEncoder);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

		serviceWithRealJwt.resetPassword(resetDto);

		verify(userRepository).save(user);
		assertEquals("encodedNewPassword", user.getPassword());
	}

	@Test
	void resetPassword_shouldThrowForExpiredToken() throws Exception {
		String sharedKey = "thisIsASecretKeyForTestingPurposesThatIsLongEnough";
		JwtService realJwtService = new JwtService(sharedKey);
		String expiredToken = realJwtService.createSignedJWT(email, userId, Collections.emptyList(), -1,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(expiredToken);
		resetDto.setNewPassword("newPassword123");

		PasswordResetServiceImpl serviceWithRealJwt = new PasswordResetServiceImpl(realJwtService, emailService,
				userRepository, passwordEncoder);

		assertThrows(JwtAuthenticationException.class, () -> serviceWithRealJwt.resetPassword(resetDto));

		verify(userRepository, never()).save(any());
	}

	@Test
	void resetPassword_shouldThrowForInvalidTokenType() throws Exception {
		String sharedKey = "thisIsASecretKeyForTestingPurposesThatIsLongEnough";
		JwtService realJwtService = new JwtService(sharedKey);
		String accessToken = realJwtService.createSignedJWT(email, userId, Collections.emptyList(), 20, "ACCESS");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(accessToken);
		resetDto.setNewPassword("newPassword123");

		PasswordResetServiceImpl serviceWithRealJwt = new PasswordResetServiceImpl(realJwtService, emailService,
				userRepository, passwordEncoder);

		assertThrows(JwtAuthenticationException.class, () -> serviceWithRealJwt.resetPassword(resetDto));

		verify(userRepository, never()).save(any());
	}

	@Test
	void resetPassword_shouldThrowForInvalidToken() {
		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken("invalid.token.format");
		resetDto.setNewPassword("newPassword123");

		assertThrows(JwtAuthenticationException.class, () -> passwordResetService.resetPassword(resetDto));

		verify(userRepository, never()).save(any());
	}

	@Test
	void resetPassword_shouldThrowWhenUserNotFound() throws Exception {
		String sharedKey = "thisIsASecretKeyForTestingPurposesThatIsLongEnough";
		JwtService realJwtService = new JwtService(sharedKey);
		String validToken = realJwtService.createSignedJWT(email, userId, Collections.emptyList(), 20,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(validToken);
		resetDto.setNewPassword("newPassword123");

		PasswordResetServiceImpl serviceWithRealJwt = new PasswordResetServiceImpl(realJwtService, emailService,
				userRepository, passwordEncoder);

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> serviceWithRealJwt.resetPassword(resetDto));

		verify(userRepository, never()).save(any());
	}

	@Test
	void resetPassword_shouldThrowWhenUserIdMismatch() throws Exception {
		String sharedKey = "thisIsASecretKeyForTestingPurposesThatIsLongEnough";
		JwtService realJwtService = new JwtService(sharedKey);
		String validToken = realJwtService.createSignedJWT(email, userId, Collections.emptyList(), 20,
				"PASSWORD_RESET");

		PasswordResetDto resetDto = new PasswordResetDto();
		resetDto.setToken(validToken);
		resetDto.setNewPassword("newPassword123");

		User differentUser = new User();
		differentUser.setId(999);
		differentUser.setEmail(email);

		PasswordResetServiceImpl serviceWithRealJwt = new PasswordResetServiceImpl(realJwtService, emailService,
				userRepository, passwordEncoder);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(differentUser));

		assertThrows(JwtAuthenticationException.class, () -> serviceWithRealJwt.resetPassword(resetDto));

		verify(userRepository, never()).save(any());
	}

}
