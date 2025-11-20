package source.code.unit.user.withoutType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import source.code.dto.response.food.FoodSummaryDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserFood;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.implementation.user.interaction.withoutType.UserFoodServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFoodServiceTest {
    @Mock
    private UserFoodRepository userFoodRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FoodMapper foodMapper;
    @Mock
    private ImageUrlPopulationService imagePopulationService;
    private UserFoodServiceImpl userFoodService;

    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
        userFoodService = new UserFoodServiceImpl(
                userRepository,
                foodRepository,
                userFoodRepository,
                foodMapper,
                imagePopulationService
        );
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int foodId = 100;
        User user = new User();
        Food food = new Food();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

        userFoodService.saveToUser(foodId);

        verify(userFoodRepository).save(any(UserFood.class));
    }

    @Test
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int foodId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userFoodService.saveToUser(foodId));

        verify(userFoodRepository, never()).save(any());
    }

    @Test
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int foodId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.saveToUser(foodId));

        verify(userFoodRepository, never()).save(any());
    }

    @Test
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfFoodNotFound() {
        int userId = 1;
        int foodId = 100;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(foodRepository.findById(foodId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.saveToUser(foodId));

        verify(userFoodRepository, never()).save(any());
    }

    @Test
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int foodId = 100;
        UserFood userFood = UserFood.of(new User(), new Food());

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findByUserIdAndFoodId(userId, foodId))
                .thenReturn(Optional.of(userFood));

        userFoodService.deleteFromUser(foodId);

        verify(userFoodRepository).delete(userFood);
    }

    @Test
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserFoodNotFound() {
        int userId = 1;
        int foodId = 100;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findByUserIdAndFoodId(userId, foodId))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.deleteFromUser(foodId));

        verify(userFoodRepository, never()).delete(any());
    }

    @Test
    public void getAllFromUser_ShouldReturnAllFoodsByType() {
        int userId = 1;

        Food food1 = new Food();
        food1.setId(1);
        food1.setMediaList(new ArrayList<>());
        Media media1 = new Media();
        media1.setImageName("food1.jpg");
        food1.getMediaList().add(media1);

        Food food2 = new Food();
        food2.setId(2);
        food2.setMediaList(new ArrayList<>());
        Media media2 = new Media();
        media2.setImageName("food2.jpg");
        food2.getMediaList().add(media2);

        UserFood uf1 = UserFood.of(new User(), food1);
        UserFood uf2 = UserFood.of(new User(), food2);

        FoodSummaryDto dto1 = new FoodSummaryDto();
        dto1.setId(1);
        dto1.setImageName("food1.jpg");
        FoodSummaryDto dto2 = new FoodSummaryDto();
        dto2.setId(2);
        dto2.setImageName("food2.jpg");

        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(List.of(uf1, uf2));
        when(foodMapper.toSummaryDto(food1)).thenReturn(dto1);
        when(foodMapper.toSummaryDto(food2)).thenReturn(dto2);

        var result = userFoodService.getAllFromUser(userId, Sort.Direction.DESC);

        assertEquals(2, result.size());
        verify(foodMapper, times(2)).toSummaryDto(any(Food.class));
    }

    @Test
    public void getAllFromUser_ShouldReturnEmptyListIfNoFoods() {
        int userId = 1;
        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        var result = userFoodService.getAllFromUser(userId, Sort.Direction.DESC);

        assertTrue(result.isEmpty());
    }

    @Test
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int foodId = 100;
        long saveCount = 5;
        long likeCount = 0L;
        Food food = new Food();

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        when(userFoodRepository.countByFoodId(foodId))
                .thenReturn(likeCount);
        when(userFoodRepository.countByFoodId(foodId))
                .thenReturn(saveCount);

        var result = userFoodService.calculateLikesAndSaves(foodId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(foodRepository).findById(foodId);
        verify(userFoodRepository).countByFoodId(foodId);
        verify(userFoodRepository).countByFoodId(foodId);
    }

    @Test
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfFoodNotFound() {
        int foodId = 100;

        when(foodRepository.findById(foodId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.calculateLikesAndSaves(foodId));

        verify(userFoodRepository, never()).countByFoodId(anyInt());
    }

    @Test
    public void getAllFromUser_ShouldSortByInteractionDateDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Food food1 = new Food();
        food1.setId(1);
        food1.setMediaList(new ArrayList<>());

        Food food2 = new Food();
        food2.setId(2);
        food2.setMediaList(new ArrayList<>());

        UserFood uf1 = UserFood.of(new User(), food1);
        uf1.setCreatedAt(older);
        UserFood uf2 = UserFood.of(new User(), food2);
        uf2.setCreatedAt(newer);

        FoodSummaryDto dto1 = createFoodResponseDto(1, older);
        FoodSummaryDto dto2 = createFoodResponseDto(2, newer);

        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(uf2, uf1)));
        when(foodMapper.toSummaryDto(food1)).thenReturn(dto1);
        when(foodMapper.toSummaryDto(food2)).thenReturn(dto2);

        List<BaseUserEntity> result = userFoodService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
        verify(userFoodRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    public void getAllFromUser_ShouldSortByInteractionDateAsc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Food food1 = new Food();
        food1.setId(1);
        food1.setMediaList(new ArrayList<>());

        Food food2 = new Food();
        food2.setId(2);
        food2.setMediaList(new ArrayList<>());

        UserFood uf1 = UserFood.of(new User(), food1);
        uf1.setCreatedAt(older);
        UserFood uf2 = UserFood.of(new User(), food2);
        uf2.setCreatedAt(newer);

        FoodSummaryDto dto1 = createFoodResponseDto(1, older);
        FoodSummaryDto dto2 = createFoodResponseDto(2, newer);

        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(uf1, uf2)));
        when(foodMapper.toSummaryDto(food1)).thenReturn(dto1);
        when(foodMapper.toSummaryDto(food2)).thenReturn(dto2);

        List<BaseUserEntity> result = userFoodService.getAllFromUser(userId, Sort.Direction.ASC);

        assertSortedResult(result, 2, 1, 2);
        verify(userFoodRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    public void getAllFromUser_DefaultShouldSortDesc() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Food food1 = new Food();
        food1.setId(1);
        food1.setMediaList(new ArrayList<>());

        Food food2 = new Food();
        food2.setId(2);
        food2.setMediaList(new ArrayList<>());

        UserFood uf1 = UserFood.of(new User(), food1);
        uf1.setCreatedAt(older);
        UserFood uf2 = UserFood.of(new User(), food2);
        uf2.setCreatedAt(newer);

        FoodSummaryDto dto1 = createFoodResponseDto(1, older);
        FoodSummaryDto dto2 = createFoodResponseDto(2, newer);

        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(uf2, uf1)));
        when(foodMapper.toSummaryDto(food1)).thenReturn(dto1);
        when(foodMapper.toSummaryDto(food2)).thenReturn(dto2);

        List<BaseUserEntity> result = userFoodService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 2, 2, 1);
        verify(userFoodRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    public void getAllFromUser_ShouldHandleNullDates() {
        int userId = 1;

        Food food1 = new Food();
        food1.setId(1);
        food1.setMediaList(new ArrayList<>());

        Food food2 = new Food();
        food2.setId(2);
        food2.setMediaList(new ArrayList<>());

        Food food3 = new Food();
        food3.setId(3);
        food3.setMediaList(new ArrayList<>());

        UserFood uf1 = UserFood.of(new User(), food1);
        uf1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        UserFood uf2 = UserFood.of(new User(), food2);
        uf2.setCreatedAt(null);
        UserFood uf3 = UserFood.of(new User(), food3);
        uf3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

        FoodSummaryDto dto1 = createFoodResponseDto(1, LocalDateTime.of(2024, 1, 1, 10, 0));
        FoodSummaryDto dto2 = createFoodResponseDto(2, null);
        FoodSummaryDto dto3 = createFoodResponseDto(3, LocalDateTime.of(2024, 1, 2, 10, 0));

        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(uf3, uf1, uf2)));
        when(foodMapper.toSummaryDto(food1)).thenReturn(dto1);
        when(foodMapper.toSummaryDto(food2)).thenReturn(dto2);
        when(foodMapper.toSummaryDto(food3)).thenReturn(dto3);

        List<BaseUserEntity> result = userFoodService.getAllFromUser(userId, Sort.Direction.DESC);

        assertSortedResult(result, 3, 3, 1, 2);
        verify(userFoodRepository).findAllByUserIdWithMedia(eq(userId), any(Sort.class));
    }

    @Test
    public void getAllFromUser_ShouldPopulateImageUrlsAfterSorting() {
        int userId = 1;
        LocalDateTime older = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newer = LocalDateTime.of(2024, 1, 2, 10, 0);

        Food food1 = new Food();
        food1.setId(1);
        food1.setMediaList(new ArrayList<>());
        Media media1 = new Media();
        media1.setImageName("image1.jpg");
        food1.getMediaList().add(media1);

        Food food2 = new Food();
        food2.setId(2);
        food2.setMediaList(new ArrayList<>());
        Media media2 = new Media();
        media2.setImageName("image2.jpg");
        food2.getMediaList().add(media2);

        UserFood uf1 = UserFood.of(new User(), food1);
        uf1.setCreatedAt(older);
        UserFood uf2 = UserFood.of(new User(), food2);
        uf2.setCreatedAt(newer);

        FoodSummaryDto dto1 = createFoodResponseDto(1, older);
        dto1.setImageName("image1.jpg");
        FoodSummaryDto dto2 = createFoodResponseDto(2, newer);
        dto2.setImageName("image2.jpg");

        when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), any(Sort.class)))
                .thenReturn(new ArrayList<>(List.of(uf2, uf1)));
        when(foodMapper.toSummaryDto(food1)).thenReturn(dto1);
        when(foodMapper.toSummaryDto(food2)).thenReturn(dto2);

        List<BaseUserEntity> result = userFoodService.getAllFromUser(userId, Sort.Direction.DESC);

        assertNotNull(result);
        assertEquals(2, result.size());
        // Image URL population is handled by imagePopulationService internally
        verify(foodMapper).toSummaryDto(food1);
        verify(foodMapper).toSummaryDto(food2);
    }

    private FoodSummaryDto createFoodResponseDto(int id, LocalDateTime interactionDate) {
        FoodSummaryDto dto = new FoodSummaryDto();
        dto.setId(id);
        dto.setUserFoodInteractionCreatedAt(interactionDate);
        return dto;
    }

    private void assertSortedResult(List<BaseUserEntity> result, int expectedSize, Integer... expectedIds) {
        assertNotNull(result);
        assertEquals(expectedSize, result.size());
        for (int i = 0; i < expectedIds.length; i++) {
            assertEquals(expectedIds[i], ((FoodSummaryDto) result.get(i)).getId());
        }
    }
}
