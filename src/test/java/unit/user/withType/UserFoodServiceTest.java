package unit.user.withType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.food.FoodResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.model.user.UserFood;
import source.code.model.user.profile.User;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.implementation.user.interaction.withType.UserFoodServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    @InjectMocks
    private UserFoodServiceImpl userFoodService;
    private MockedStatic<AuthorizationUtil> mockedAuthUtil;

    @BeforeEach
    void setUp() {
        mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthUtil != null) {
            mockedAuthUtil.close();
        }
    }

    @Test
    @DisplayName("saveToUser - Should save to user with type")
    public void saveToUser_ShouldSaveToUserWithType() {
        int userId = 1;
        int foodId = 100;
        short type = 1;
        User user = new User();
        Food food = new Food();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

        userFoodService.saveToUser(foodId, type);

        verify(userFoodRepository).save(any(UserFood.class));
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if already saved")
    public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
        int userId = 1;
        int foodId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type))
                .thenReturn(true);

        assertThrows(NotUniqueRecordException.class,
                () -> userFoodService.saveToUser(foodId, type));

        verify(userFoodRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if user not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
        int userId = 1;
        int foodId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.saveToUser(foodId, type));

        verify(userFoodRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveToUser - Should throw exception if food not found")
    public void saveToUser_ShouldThrowRecordNotFoundExceptionIfFoodNotFound() {
        int userId = 1;
        int foodId = 100;
        short type = 1;
        User user = new User();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type))
                .thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(foodRepository.findById(foodId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.saveToUser(foodId, type));

        verify(userFoodRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFromUser - Should delete from user")
    public void deleteFromUser_ShouldDeleteFromUser() {
        int userId = 1;
        int foodId = 100;
        short type = 1;
        UserFood userFood = UserFood.createWithUserFoodType(new User(), new Food(), type);

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findByUserIdAndFoodIdAndType(userId, foodId, type))
                .thenReturn(Optional.of(userFood));

        userFoodService.deleteFromUser(foodId, type);

        verify(userFoodRepository).delete(userFood);
    }

    @Test
    @DisplayName("deleteFromUser - Should throw exception if user food not found")
    public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserFoodNotFound() {
        int userId = 1;
        int foodId = 100;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findByUserIdAndFoodIdAndType(userId, foodId, type))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.deleteFromUser(foodId, type));

        verify(userFoodRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllFromUser - Should return all foods by type")
    public void getAllFromUser_ShouldReturnAllFoodsByType() {
        int userId = 1;
        short type = 1;
        UserFood food1 = UserFood.createWithUserFoodType(new User(), new Food(), type);
        UserFood food2 = UserFood.createWithUserFoodType(new User(), new Food(), type);
        FoodResponseDto dto1 = new FoodResponseDto();
        FoodResponseDto dto2 = new FoodResponseDto();

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of(food1, food2));
        when(foodMapper.toResponseDto(food1.getFood())).thenReturn(dto1);
        when(foodMapper.toResponseDto(food2.getFood())).thenReturn(dto2);

        var result = userFoodService.getAllFromUser(type);

        assertEquals(2, result.size());
        assertTrue(result.contains((BaseUserEntity) dto1));
        assertTrue(result.contains((BaseUserEntity) dto2));
    }

    @Test
    @DisplayName("getAllFromUser - Should return empty list if no foods")
    public void getAllFromUser_ShouldReturnEmptyListIfNoFoods() {
        int userId = 1;
        short type = 1;

        mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
        when(userFoodRepository.findByUserIdAndType(userId, type))
                .thenReturn(List.of());

        var result = userFoodService.getAllFromUser(type);

        assertTrue(result.isEmpty());
        verify(foodMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should return correct counts")
    public void calculateLikesAndSaves_ShouldReturnCorrectCounts() {
        int foodId = 100;
        long saveCount = 5;
        long likeCount = 10;
        Food food = new Food();

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        when(userFoodRepository.countByFoodIdAndType(foodId, (short) 1))
                .thenReturn(saveCount);
        when(userFoodRepository.countByFoodIdAndType(foodId, (short) 2))
                .thenReturn(likeCount);

        var result = userFoodService.calculateLikesAndSaves(foodId);

        assertEquals(saveCount, result.getSaves());
        assertEquals(likeCount, result.getLikes());
        verify(foodRepository).findById(foodId);
        verify(userFoodRepository).countByFoodIdAndType(foodId, (short) 1);
        verify(userFoodRepository).countByFoodIdAndType(foodId, (short) 2);
    }

    @Test
    @DisplayName("calculateLikesAndSaves - Should throw exception if food not found")
    public void calculateLikesAndSaves_ShouldThrowRecordNotFoundExceptionIfFoodNotFound() {
        int foodId = 100;

        when(foodRepository.findById(foodId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userFoodService.calculateLikesAndSaves(foodId));

        verify(userFoodRepository, never()).countByFoodIdAndType(anyInt(), anyShort());
    }
}