package source.code.unit.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.validation.password.PasswordLowercaseDomain;
import source.code.validation.password.PasswordLowercaseValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PasswordLowercaseValidatorTest {

	@Mock
	private ConstraintValidatorContext context;

	@Mock
	private PasswordLowercaseDomain annotation;

	@InjectMocks
	private PasswordLowercaseValidator validator;

	@BeforeEach
	void setUp() {
		validator.initialize(annotation);
	}

	@Test
	void isValid_shouldReturnTrueForNullPassword() {
		boolean result = validator.isValid(null, context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForEmptyPassword() {
		boolean result = validator.isValid("", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithLowercase() {
		boolean result = validator.isValid("Password", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithMultipleLowercase() {
		boolean result = validator.isValid("password", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithMixedCase() {
		boolean result = validator.isValid("PaSSwoRd", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithLowercaseAndNumbers() {
		boolean result = validator.isValid("pass123", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnFalseForPasswordWithoutLowercase() {
		boolean result = validator.isValid("PASSWORD123", context);
		assertFalse(result);
	}

	@Test
	void isValid_shouldReturnFalseForPasswordWithOnlyUppercase() {
		boolean result = validator.isValid("ABCDEF", context);
		assertFalse(result);
	}

	@Test
	void isValid_shouldReturnFalseForPasswordWithOnlyNumbers() {
		boolean result = validator.isValid("123456", context);
		assertFalse(result);
	}

	@Test
	void isValid_shouldReturnFalseForPasswordWithOnlySpecialCharacters() {
		boolean result = validator.isValid("!@#$%^", context);
		assertFalse(result);
	}

}
