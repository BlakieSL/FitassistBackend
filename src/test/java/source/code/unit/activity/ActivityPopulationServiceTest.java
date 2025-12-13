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
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserActivityRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.activity.ActivityPopulationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityPopulationServiceTest {
    @Mock
    private UserActivityRepository userActivityRepository;
    @Mock
    private AwsS3Service s3Service;
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

    @Test
    void populateList_shouldPopulateImageUrlsAndCounts() {
        ActivitySummaryDto dto1 = new ActivitySummaryDto();
        dto1.setId(1);
        dto1.setImageName("image1.jpg");
        ActivitySummaryDto dto2 = new ActivitySummaryDto();
        dto2.setId(2);
        dto2.setImageName("image2.jpg");
        List<ActivitySummaryDto> activities = List.of(dto1, dto2);

        SavesProjection projection1 = mock(SavesProjection.class);
        when(projection1.getEntityId()).thenReturn(1);
        when(projection1.savesCount()).thenReturn(5L);
        when(projection1.isSaved()).thenReturn(true);

        SavesProjection projection2 = mock(SavesProjection.class);
        when(projection2.getEntityId()).thenReturn(2);
        when(projection2.savesCount()).thenReturn(3L);
        when(projection2.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userActivityRepository.findCountsAndInteractionsByActivityIds(eq(userId), anyList()))
                .thenReturn(List.of(projection1, projection2));
        when(s3Service.getImage("image1.jpg")).thenReturn("http://s3/image1.jpg");
        when(s3Service.getImage("image2.jpg")).thenReturn("http://s3/image2.jpg");

        activityPopulationService.populate(activities);

        assertEquals("http://s3/image1.jpg", dto1.getFirstImageUrl());
        assertEquals(5L, dto1.getSavesCount());
        assertTrue(dto1.getSaved());

        assertEquals("http://s3/image2.jpg", dto2.getFirstImageUrl());
        assertEquals(3L, dto2.getSavesCount());
        assertFalse(dto2.getSaved());
    }

    @Test
    void populateList_shouldReturnEarlyForEmptyList() {
        activityPopulationService.populate(List.of());

        verifyNoInteractions(userActivityRepository);
        verifyNoInteractions(s3Service);
    }
}
