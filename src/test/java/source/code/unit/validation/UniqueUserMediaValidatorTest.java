package source.code.unit.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.config.ContextProvider;
import source.code.dto.request.media.MediaCreateDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.validation.media.UniqueUserMedia;
import source.code.validation.media.UniqueUserMediaValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UniqueUserMediaValidatorTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private UniqueUserMedia annotation;

    private UniqueUserMediaValidator validator;
    private MediaCreateDto createDto;

    @BeforeEach
    void setUp() {
        validator = new UniqueUserMediaValidator();
        createDto = new MediaCreateDto();
        
        try (MockedStatic<ContextProvider> contextProvider = Mockito.mockStatic(ContextProvider.class)) {
            contextProvider.when(() -> ContextProvider.getBean(EntityManager.class)).thenReturn(entityManager);
            contextProvider.when(() -> ContextProvider.getBean(MediaRepository.class)).thenReturn(mediaRepository);
            validator.initialize(annotation);
        }
    }

    @Test
    void isValid_shouldReturnTrueWhenParentTypeIsNotUser() {
        createDto.setParentType(MediaConnectedEntity.FOOD);
        createDto.setParentId(1);

        boolean result = validator.isValid(createDto, context);

        assertTrue(result);
        verify(entityManager).setFlushMode(FlushModeType.COMMIT);
        verify(entityManager).setFlushMode(FlushModeType.AUTO);
        verifyNoInteractions(mediaRepository);
    }

    @Test
    void isValid_shouldReturnTrueWhenUserHasNoExistingMedia() {
        createDto.setParentType(MediaConnectedEntity.USER);
        createDto.setParentId(1);

        when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(1, MediaConnectedEntity.USER))
                .thenReturn(Optional.empty());

        boolean result = validator.isValid(createDto, context);

        assertTrue(result);
        verify(entityManager).setFlushMode(FlushModeType.COMMIT);
        verify(entityManager).setFlushMode(FlushModeType.AUTO);
        verify(mediaRepository).findFirstByParentIdAndParentTypeOrderByIdAsc(1, MediaConnectedEntity.USER);
    }

    @Test
    void isValid_shouldReturnFalseWhenUserAlreadyHasMedia() {
        createDto.setParentType(MediaConnectedEntity.USER);
        createDto.setParentId(1);

        Media existingMedia = new Media();
        when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(1, MediaConnectedEntity.USER))
                .thenReturn(Optional.of(existingMedia));

        boolean result = validator.isValid(createDto, context);

        assertFalse(result);
        verify(entityManager).setFlushMode(FlushModeType.COMMIT);
        verify(entityManager).setFlushMode(FlushModeType.AUTO);
        verify(mediaRepository).findFirstByParentIdAndParentTypeOrderByIdAsc(1, MediaConnectedEntity.USER);
    }

    @Test
    void isValid_shouldReturnTrueForNonMediaCreateDtoObjects() {
        boolean result = validator.isValid("not a MediaCreateDto", context);

        assertTrue(result);
        verify(entityManager).setFlushMode(FlushModeType.COMMIT);
        verify(entityManager).setFlushMode(FlushModeType.AUTO);
        verifyNoInteractions(mediaRepository);
    }

    @Test
    void isValid_shouldReturnTrueForNullValue() {
        boolean result = validator.isValid(null, context);

        assertTrue(result);
        verify(entityManager).setFlushMode(FlushModeType.COMMIT);
        verify(entityManager).setFlushMode(FlushModeType.AUTO);
        verifyNoInteractions(mediaRepository);
    }

    @Test
    void isValid_shouldResetFlushModeEvenWhenExceptionThrown() {
        createDto.setParentType(MediaConnectedEntity.USER);
        createDto.setParentId(1);

        when(mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(1, MediaConnectedEntity.USER))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> validator.isValid(createDto, context));

        verify(entityManager).setFlushMode(FlushModeType.COMMIT);
        verify(entityManager).setFlushMode(FlushModeType.AUTO);
    }
}