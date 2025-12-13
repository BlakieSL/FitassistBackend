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
import source.code.dto.response.exercise.ExerciseSummaryDto;
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
        when(userExerciseRepository.findCountsAndInteractions(exerciseId, userId)).thenReturn(savesProjection);
        when(mediaRepository.findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE)).thenReturn(List.of(media));
        when(awsS3Service.getImage("test-image.jpg")).thenReturn("https://s3.example.com/test-image.jpg");

        exercisePopulationService.populate(exerciseResponseDto);

        assertEquals(10, exerciseResponseDto.getSavesCount());
        assertFalse(exerciseResponseDto.isSaved());
        assertEquals(List.of("https://s3.example.com/test-image.jpg"), exerciseResponseDto.getImageUrls());
        verify(userExerciseRepository).findCountsAndInteractions(exerciseId, userId);
        verify(mediaRepository).findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE);
        verify(awsS3Service).getImage("test-image.jpg");
    }

    @Test
    void populate_shouldSetZeroSavesCountWhenNoSaves() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(0L);
        when(savesProjection.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findCountsAndInteractions(exerciseId, userId)).thenReturn(savesProjection);
        when(mediaRepository.findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE)).thenReturn(List.of());

        exercisePopulationService.populate(exerciseResponseDto);

        assertEquals(0, exerciseResponseDto.getSavesCount());
        assertFalse(exerciseResponseDto.isSaved());
        assertTrue(exerciseResponseDto.getImageUrls().isEmpty());
        verify(userExerciseRepository).findCountsAndInteractions(exerciseId, userId);
        verify(mediaRepository).findByParentIdAndParentType(exerciseId, MediaConnectedEntity.EXERCISE);
    }

    @Test
    void populateList_shouldPopulateImageUrlsAndCounts() {
        ExerciseSummaryDto dto1 = new ExerciseSummaryDto();
        dto1.setId(1);
        dto1.setImageName("image1.jpg");
        ExerciseSummaryDto dto2 = new ExerciseSummaryDto();
        dto2.setId(2);
        dto2.setImageName("image2.jpg");
        List<ExerciseSummaryDto> exercises = List.of(dto1, dto2);

        SavesProjection projection1 = mock(SavesProjection.class);
        when(projection1.getEntityId()).thenReturn(1);
        when(projection1.savesCount()).thenReturn(5L);
        when(projection1.isSaved()).thenReturn(true);

        SavesProjection projection2 = mock(SavesProjection.class);
        when(projection2.getEntityId()).thenReturn(2);
        when(projection2.savesCount()).thenReturn(3L);
        when(projection2.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findCountsAndInteractionsByExerciseIds(eq(userId), anyList()))
                .thenReturn(List.of(projection1, projection2));
        when(awsS3Service.getImage("image1.jpg")).thenReturn("http://s3/image1.jpg");
        when(awsS3Service.getImage("image2.jpg")).thenReturn("http://s3/image2.jpg");

        exercisePopulationService.populate(exercises);

        assertEquals("http://s3/image1.jpg", dto1.getFirstImageUrl());
        assertEquals(5L, dto1.getSavesCount());
        assertTrue(dto1.getSaved());

        assertEquals("http://s3/image2.jpg", dto2.getFirstImageUrl());
        assertEquals(3L, dto2.getSavesCount());
        assertFalse(dto2.getSaved());
    }

    @Test
    void populateList_shouldReturnEarlyForEmptyList() {
        exercisePopulationService.populate(List.of());

        verifyNoInteractions(userExerciseRepository);
        verifyNoInteractions(awsS3Service);
    }
}
