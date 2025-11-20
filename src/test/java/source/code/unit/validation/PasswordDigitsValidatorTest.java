package source.code.unit.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.validation.password.PasswordDigitsDomain;
import source.code.validation.password.PasswordDigitsValidator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PasswordDigitsValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private PasswordDigitsDomain annotation;

    private PasswordDigitsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordDigitsValidator();
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
    void isValid_shouldReturnTrueForPasswordWithDigit() {
        boolean result = validator.isValid("password1", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithMultipleDigits() {
        boolean result = validator.isValid("pass123word", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordStartingWithDigit() {
        boolean result = validator.isValid("1password", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordEndingWithDigit() {
        boolean result = validator.isValid("password9", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnFalseForPasswordWithoutDigits() {
        boolean result = validator.isValid("password", context);
        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseForPasswordWithOnlyLetters() {
        boolean result = validator.isValid("abcdefABCDEF", context);
        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseForPasswordWithOnlySpecialCharacters() {
        boolean result = validator.isValid("!@#$%^&*()", context);
        assertFalse(result);
    }
}
