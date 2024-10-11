package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.DailyFoodItemCreateDto;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.DailyFoodMapper;
import source.code.model.*;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DailyFoodServiceTest {
  @Mock
  private ValidationHelper validationHelper;
  @Mock
  private JsonPatchHelper jsonPatchHelper;
  @Mock
  private DailyFoodMapper dailyFoodMapper;
  @Mock
  private DailyFoodRepository dailyFoodRepository;
  @Mock
  private FoodRepository foodRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private DailyFoodServiceImpl dailyFoodService;

  private User user1;
  private User user2;
  private FoodCategory foodCategory1;
  private FoodCategory foodCategory2;
  private Food food1;
  private Food food2;
  private DailyFood dailyFood1;
  private DailyFood dailyFood2;
  private DailyFoodItem dailyFoodItem1;
  private DailyFoodItem dailyFoodItem2;
  private DailyFoodItemCreateDto createDto;


  @BeforeEach
  void setup() {
    user1 = createUser(1);
    user2 = createUser(2);

    foodCategory1 = createFoodCategory(1, "Name1");
    foodCategory2 = createFoodCategory(2, "Name2");

    food1 = createFood(1);
    food1.setFoodCategory(foodCategory1);

    food2 = createFood(2);
    food2.setFoodCategory(foodCategory2);

    dailyFood1 = createDailyFood(1, user1);
    dailyFood2 = createDailyFood(2, user2);

    dailyFoodItem1 = createDailyFoodItem(1, food1, dailyFood1);
    dailyFoodItem2 = createDailyFoodItem(2, food2, dailyFood2);

    dailyFood1.getDailyFoodItems().add(dailyFoodItem1);
    dailyFood2.getDailyFoodItems().add(dailyFoodItem2);

    createDto = createDailyFoodItemCreateDto(30);
  }

  private User createUser(int id) {
    return User.createWithId(id);
  }

  private FoodCategory createFoodCategory(int id, String name) {
    return FoodCategory.createWithIdName(id, name);
  }

  private Food createFood(int id) {
    return Food.createWithId(id);
  }

  private DailyFood createDailyFood(int id, User user) {
    return DailyFood.createWithIdUser(id, user);
  }

  private DailyFoodItem createDailyFoodItem(int id,
                                            Food food,
                                            DailyFood dailyFood) {

    return DailyFoodItem.createWithIdFoodDailyFood(id, food, dailyFood);
  }

  private DailyFoodItemCreateDto createDailyFoodItemCreateDto(int amount) {
    return new DailyFoodItemCreateDto(amount);
  }

  private void clearDailyFood(DailyFood dailyFood) {
    dailyFood.getDailyFoodItems().clear();
  }

  @Test
  void resetDailyCarts_shouldUpdateFoods_whenFoodsExist() {
    // Arrange
    LocalDate today = LocalDate.now();
    dailyFood1.setDate(today);
    dailyFood1.getDailyFoodItems().clear();

    dailyFood2.setDate(today);
    dailyFood2.getDailyFoodItems().clear();

    when(dailyFoodRepository.findAll()).thenReturn(List.of(dailyFood1, dailyFood2));

    // Act
    dailyFoodService.resetDailyCarts();

    //Assert
    verify(dailyFoodRepository, times(1)).findAll();
    verify(dailyFoodRepository, times(1)).save(dailyFood1);
    verify(dailyFoodRepository, times(1)).save(dailyFood2);
    assertEquals(today, dailyFood1.getDate());
    assertEquals(today, dailyFood2.getDate());
    assertTrue(dailyFood1.getDailyFoodItems().isEmpty());
    assertTrue(dailyFood2.getDailyFoodItems().isEmpty());
  }

  @Test
  void resetDailyCarts_shouldDoNothing_whenNoFoodsExists() {
    // Arrange
    when(dailyFoodRepository.findAll()).thenReturn(Collections.emptyList());

    // Act
    dailyFoodService.resetDailyCarts();

    // Assert
    verify(dailyFoodRepository, times(1)).findAll();
    verify(dailyFoodRepository, never()).save(any(DailyFood.class));
  }

  @Test
  void addFoodToDailyFoodItem_shouldAddFood_whenFoodDoesNotExistInDailyCart() {
    // Arrange
    int userId = user1.getId();
    int foodId = food1.getId();
    when(dailyFoodRepository.findByUserId(userId)).thenReturn(Optional.of(dailyFood1));
    when(foodRepository.findById(foodId)).thenReturn(Optional.of(food1));

    // Act
    dailyFoodService.addFoodToDailyFoodItem(userId, foodId, createDto);

    // Assert
    verify(validationHelper, times(1)).validate(createDto);
    verify(dailyFoodRepository, times(1)).save(dailyFood1);
    assertEquals(1, dailyFood1.getDailyFoodItems().size());

    DailyFoodItem addedFood = dailyFood1.getDailyFoodItems().get(0);
    assertEquals(food1, addedFood.getFood());
    assertEquals(createDto.getAmount(), addedFood.getAmount());
    assertEquals(dailyFood1, addedFood.getDailyFood());
  }

  @Test
  void addFoodToDailyFoodItem_shouldUpdateTime_whenFoodExistsInDailyCart() {
    // Arrange
    int userId = user1.getId();
    int foodId = food1.getId();
    int existingAmount = 15;
    dailyFoodItem1.setAmount(existingAmount);

    when(dailyFoodRepository.findByUserId(userId)).thenReturn(Optional.of(dailyFood1));
    when(foodRepository.findById(foodId)).thenReturn(Optional.of(food1));

    // Act
    dailyFoodService.addFoodToDailyFoodItem(userId, foodId, createDto);

    // Assert
    verify(validationHelper, times(1)).validate(createDto);
    verify(dailyFoodRepository, times(1)).save(dailyFood1);
    assertEquals(1, dailyFood1.getDailyFoodItems().size());

    DailyFoodItem updatedFood = dailyFood1.getDailyFoodItems().get(0);
    assertEquals(food1, updatedFood.getFood());
    assertEquals(createDto.getAmount(), updatedFood.getAmount());
    assertEquals(dailyFood1, updatedFood.getDailyFood());
  }

  @Test
  void addFoodToDailyFoodItem_shouldThrowException_whenValidationFails() {
    // Arrange
    doThrow(new IllegalArgumentException("Validation failed")).when(validationHelper).validate(createDto);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            dailyFoodService.addFoodToDailyFoodItem(user1.getId(), food1.getId(), createDto));

    assertEquals("Validation failed", exception.getMessage());
    verify(validationHelper, times(1)).validate(createDto);
    verify(dailyFoodRepository, never()).save(any());
  }

  @Test
  void addFoodToDailyFoodItem_shouldThrowException_whenFoodNotFound() {
    // Arrange
    int userId = user1.getId();
    int foodId = food1.getId();
    clearDailyFood(dailyFood1);
    when(dailyFoodRepository.findByUserId(userId)).thenReturn(Optional.of(dailyFood1));
    when(foodRepository.findById(foodId)).thenReturn(Optional.empty());

    // Act & Assert
    NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
            dailyFoodService.addFoodToDailyFoodItem(userId, foodId, createDto));

    assertEquals("Food with id: " + foodId + " not found", exception.getMessage());
    verify(validationHelper, times(1)).validate(createDto);
    verify(foodRepository, times(1)).findById(foodId);
    verify(dailyFoodRepository, never()).save(any());
  }
}
