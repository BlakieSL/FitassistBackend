package source.code.unit.food;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.pojo.projection.SavesProjection;
import source.code.dto.response.food.FoodResponseDto;
import source.code.helper.user.AuthorizationUtil;
import source.code.repository.UserFoodRepository;
import source.code.service.implementation.food.FoodPopulationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodPopulationServiceTest {
    @Mock
    private UserFoodRepository userFoodRepository;
    @InjectMocks
    private FoodPopulationServiceImpl foodPopulationService;

    private FoodResponseDto foodResponseDto;
    private SavesProjection savesProjection;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;
    private int foodId;
    private int userId;

    @BeforeEach
    void setUp() {
        foodId = 1;
        userId = 1;
        foodResponseDto = new FoodResponseDto();
        foodResponseDto.setId(foodId);
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
        when(userFoodRepository.findCountsAndInteractionsByFoodId(foodId, userId)).thenReturn(savesProjection);

        foodPopulationService.populate(foodResponseDto);

        assertEquals(5, foodResponseDto.getSavesCount());
        assertTrue(foodResponseDto.isSaved());
        verify(userFoodRepository).findCountsAndInteractionsByFoodId(foodId, userId);
    }

    @Test
    void populate_shouldSetZeroSavesCountWhenNoSaves() {
        savesProjection = mock(SavesProjection.class);
        when(savesProjection.savesCount()).thenReturn(0L);
        when(savesProjection.isSaved()).thenReturn(false);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findCountsAndInteractionsByFoodId(foodId, userId)).thenReturn(savesProjection);

        foodPopulationService.populate(foodResponseDto);

        assertEquals(0, foodResponseDto.getSavesCount());
        assertFalse(foodResponseDto.isSaved());
        verify(userFoodRepository).findCountsAndInteractionsByFoodId(foodId, userId);
    }
}
