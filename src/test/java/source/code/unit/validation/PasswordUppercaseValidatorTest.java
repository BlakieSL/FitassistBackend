package source.code.unit.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.validation.password.PasswordUppercaseDomain;
import source.code.validation.password.PasswordUppercaseValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PasswordUppercaseValidatorTest {

	@Mock
	private ConstraintValidatorContext context;

	@Mock
	private PasswordUppercaseDomain annotation;

	@InjectMocks
	private PasswordUppercaseValidator validator;

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
	void isValid_shouldReturnTrueForPasswordWithUppercase() {
		boolean result = validator.isValid("Password", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithMultipleUppercase() {
		boolean result = validator.isValid("PASSWORD", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithMixedCase() {
		boolean result = validator.isValid("PaSSwoRd", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordWithUppercaseAndNumbers() {
		boolean result = validator.isValid("PASS123", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnTrueForPasswordStartingWithUppercase() {
		boolean result = validator.isValid("Password123", context);
		assertTrue(result);
	}

	@Test
	void isValid_shouldReturnFalseForPasswordWithoutUppercase() {
		boolean result = validator.isValid("password123", context);
		assertFalse(result);
	}

	@Test
	void isValid_shouldReturnFalseForPasswordWithOnlyLowercase() {
		boolean result = validator.isValid("abcdef", context);
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
