package source.code.unit.activity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.activity.ActivityResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserActivityRepository;
import source.code.service.implementation.activity.ActivityPopulationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityPopulationServiceTest {
    @Mock
    private UserActivityRepository userActivityRepository;
    @InjectMocks
    private ActivityPopulationServiceImpl activityPopulationService;

    private ActivityResponseDto activityResponseDto;
    private SavesProjection savesProjection;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;
    private int activityId;
    private int userId;

    @BeforeEach
    void setUp() {
        activityId = 1;
        userId = 1;
        activityResponseDto = new ActivityResponseDto();
        activityResponseDto.setId(activityId);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void populate_shouldSetSavesCountAndSavedTrue() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(5L);
        when(savesProjection.isSaved()).thenReturn(true);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findSavesCountAndUserSaved(activityId, userId)).thenReturn(savesProjection);

        activityPopulationService.populate(activityResponseDto);

        assertEquals(5, activityResponseDto.getSavesCount());
        assertTrue(activityResponseDto.isSaved());
        verify(userActivityRepository).findSavesCountAndUserSaved(activityId, userId);
    }

    @Test
    void populate_shouldSetZeroSavesCountWhenNoSaves() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(0L);
        when(savesProjection.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findSavesCountAndUserSaved(activityId, userId)).thenReturn(savesProjection);

        activityPopulationService.populate(activityResponseDto);

        assertEquals(0, activityResponseDto.getSavesCount());
        assertFalse(activityResponseDto.isSaved());
        verify(userActivityRepository).findSavesCountAndUserSaved(activityId, userId);
    }
}
