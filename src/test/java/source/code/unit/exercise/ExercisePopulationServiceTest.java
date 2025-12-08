package source.code.unit.exercise;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.repository.UserExerciseRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.exercise.ExercisePopulationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExercisePopulationServiceTest {
    @Mock
    private UserExerciseRepository userExerciseRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private AwsS3Service awsS3Service;
    @InjectMocks
    private ExercisePopulationServiceImpl exercisePopulationService;

    private ExerciseResponseDto exerciseResponseDto;
    private SavesProjection savesProjection;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;
    private int exerciseId;
    private int userId;

    @BeforeEach
    void setUp() {
        exerciseId = 1;
        userId = 1;
        exerciseResponseDto = new ExerciseResponseDto();
        exerciseResponseDto.setId(exerciseId);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void populate_shouldSetSavesCountAndSavedFalse() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(10L);
        when(savesProjection.isSaved()).thenReturn(false);

        Media media = new Media();
        media.setImageName("test-image.jpg");

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findSavesCountAndUserSaved(exerciseId, userId)).thenReturn(savesProjection);
        when(mediaRepository.findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE)).thenReturn(List.of(media));
        when(awsS3Service.getImage("test-image.jpg")).thenReturn("https://s3.example.com/test-image.jpg");

        exercisePopulationService.populate(exerciseResponseDto);

        assertEquals(10, exerciseResponseDto.getSavesCount());
        assertFalse(exerciseResponseDto.isSaved());
        assertEquals(List.of("https://s3.example.com/test-image.jpg"), exerciseResponseDto.getImageUrls());
        verify(userExerciseRepository).findSavesCountAndUserSaved(exerciseId, userId);
        verify(mediaRepository).findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE);
        verify(awsS3Service).getImage("test-image.jpg");
    }

    @Test
    void populate_shouldSetZeroSavesCountWhenNoSaves() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(0L);
        when(savesProjection.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findSavesCountAndUserSaved(exerciseId, userId)).thenReturn(savesProjection);
        when(mediaRepository.findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE)).thenReturn(List.of());

        exercisePopulationService.populate(exerciseResponseDto);

        assertEquals(0, exerciseResponseDto.getSavesCount());
        assertFalse(exerciseResponseDto.isSaved());
        assertTrue(exerciseResponseDto.getImageUrls().isEmpty());
        verify(userExerciseRepository).findSavesCountAndUserSaved(exerciseId, userId);
        verify(mediaRepository).findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE);
    }
}
