package source.code.unit.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.validation.password.PasswordSpecialDomain;
import source.code.validation.password.PasswordSpecialValidator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PasswordSpecialValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private PasswordSpecialDomain annotation;

    private PasswordSpecialValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordSpecialValidator();
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
    void isValid_shouldReturnTrueForPasswordWithExclamation() {
        boolean result = validator.isValid("password!", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithAtSymbol() {
        boolean result = validator.isValid("pass@word", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithHash() {
        boolean result = validator.isValid("#password", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithMultipleSpecialChars() {
        boolean result = validator.isValid("pass!@#word", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithDollarSign() {
        boolean result = validator.isValid("pa$$word", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithPercent() {
        boolean result = validator.isValid("password%123", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnTrueForPasswordWithAmpersand() {
        boolean result = validator.isValid("password&test", context);
        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnFalseForPasswordWithoutSpecialChars() {
        boolean result = validator.isValid("password123", context);
        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseForPasswordWithOnlyLetters() {
        boolean result = validator.isValid("abcdefABCDEF", context);
        assertFalse(result);
    }

    @Test
    void isValid_shouldReturnFalseForPasswordWithOnlyNumbers() {
        boolean result = validator.isValid("123456789", context);
        assertFalse(result);
    }
}