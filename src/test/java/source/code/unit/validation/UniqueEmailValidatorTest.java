package source.code.unit.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.config.ContextProvider;
import source.code.dto.request.user.UserUpdateDto;
import source.code.model.user.User;
import source.code.repository.UserRepository;
import source.code.validation.email.UniqueEmailDomain;
import source.code.validation.email.UniqueEmailValidator;

@ExtendWith(MockitoExtension.class)
public class UniqueEmailValidatorTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private EntityManager entityManager;

	@Mock
	private ConstraintValidatorContext context;

	@Mock
	private UniqueEmailDomain annotation;

	@InjectMocks
	private UniqueEmailValidator validator;

	private UserUpdateDto updateDto;

	@BeforeEach
	void setUp() {
		updateDto = new UserUpdateDto();

		try (MockedStatic<ContextProvider> contextProvider = Mockito.mockStatic(ContextProvider.class)) {
			contextProvider.when(() -> ContextProvider.getBean(EntityManager.class)).thenReturn(entityManager);
			contextProvider.when(() -> ContextProvider.getBean(UserRepository.class)).thenReturn(userRepository);
			validator.initialize(annotation);
		}
	}

	@Test
	void isValid_shouldReturnTrueForStringEmailThatDoesNotExist() {
		String email = "new@example.com";
		when(userRepository.existsByEmail(email)).thenReturn(false);

		boolean result = validator.isValid(email, context);

		assertTrue(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verify(userRepository).existsByEmail(email);
	}

	@Test
	void isValid_shouldReturnFalseForStringEmailThatExists() {
		String email = "existing@example.com";
		when(userRepository.existsByEmail(email)).thenReturn(true);

		boolean result = validator.isValid(email, context);

		assertFalse(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verify(userRepository).existsByEmail(email);
	}

	@Test
	void isValid_shouldReturnTrueForUserUpdateDtoWithNullEmail() {
		updateDto.setEmail(null);
		updateDto.setId(1);

		boolean result = validator.isValid(updateDto, context);

		assertTrue(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verifyNoInteractions(userRepository);
	}

	@Test
	void isValid_shouldReturnTrueForUserUpdateDtoWithUniqueEmail() {
		updateDto.setEmail("unique@example.com");
		updateDto.setId(1);
		when(userRepository.findByEmail("unique@example.com")).thenReturn(Optional.empty());

		boolean result = validator.isValid(updateDto, context);

		assertTrue(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verify(userRepository).findByEmail("unique@example.com");
	}

	@Test
	void isValid_shouldReturnTrueForUserUpdateDtoWithSameUserEmail() {
		updateDto.setEmail("user@example.com");
		updateDto.setId(1);

		User existingUser = new User();
		existingUser.setId(1);
		when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));

		boolean result = validator.isValid(updateDto, context);

		assertTrue(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verify(userRepository).findByEmail("user@example.com");
	}

	@Test
	void isValid_shouldReturnFalseForUserUpdateDtoWithDifferentUserEmail() {
		updateDto.setEmail("other@example.com");
		updateDto.setId(1);

		User existingUser = new User();
		existingUser.setId(2);
		when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(existingUser));

		boolean result = validator.isValid(updateDto, context);

		assertFalse(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verify(userRepository).findByEmail("other@example.com");
	}

	@Test
	void isValid_shouldReturnTrueForNonStringNonUserUpdateDtoObjects() {
		boolean result = validator.isValid(123, context);

		assertTrue(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verifyNoInteractions(userRepository);
	}

	@Test
	void isValid_shouldReturnTrueForNullValue() {
		boolean result = validator.isValid(null, context);

		assertTrue(result);
		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
		verifyNoInteractions(userRepository);
	}

	@Test
	void isValid_shouldResetFlushModeEvenWhenExceptionThrown() {
		String email = "test@example.com";
		when(userRepository.existsByEmail(email)).thenThrow(new RuntimeException("Database error"));

		assertThrows(RuntimeException.class, () -> validator.isValid(email, context));

		verify(entityManager).setFlushMode(FlushModeType.COMMIT);
		verify(entityManager).setFlushMode(FlushModeType.AUTO);
	}

}
