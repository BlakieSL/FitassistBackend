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
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserExerciseRepository;
import source.code.service.implementation.exercise.ExercisePopulationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExercisePopulationServiceTest {
    @Mock
    private UserExerciseRepository userExerciseRepository;
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

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findSavesCountAndUserSaved(exerciseId, userId)).thenReturn(savesProjection);

        exercisePopulationService.populate(exerciseResponseDto);

        assertEquals(10, exerciseResponseDto.getSavesCount());
        assertFalse(exerciseResponseDto.isSaved());
        verify(userExerciseRepository).findSavesCountAndUserSaved(exerciseId, userId);
    }

    @Test
    void populate_shouldSetZeroSavesCountWhenNoSaves() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(0L);
        when(savesProjection.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userExerciseRepository.findSavesCountAndUserSaved(exerciseId, userId)).thenReturn(savesProjection);

        exercisePopulationService.populate(exerciseResponseDto);

        assertEquals(0, exerciseResponseDto.getSavesCount());
        assertFalse(exerciseResponseDto.isSaved());
        verify(userExerciseRepository).findSavesCountAndUserSaved(exerciseId, userId);
    }
}
