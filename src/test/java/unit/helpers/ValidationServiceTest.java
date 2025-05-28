package unit.helpers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.service.implementation.helpers.ValidationServiceImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private ValidationServiceImpl validationService;

    private Object dto;
    private Set<ConstraintViolation<Object>> violations;

    @BeforeEach
    void setUp() {
        dto = new Object();
        violations = mock(Set.class);
    }

    @Test
    void validate_shouldPassWhenNoViolations() {
        when(validator.validate(dto)).thenReturn(Set.of());

        validationService.validate(dto);

        verify(validator).validate(dto);
    }

    @Test
    void validate_shouldThrowExceptionWhenViolationsExist() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> validationService.validate(dto));

        verify(validator).validate(dto);
    }
}