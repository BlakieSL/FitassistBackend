package source.code.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.mapper.FoodMapper;
import source.code.repository.FoodCategoryRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.service.implementation.Food.FoodServiceImpl;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {
  @Mock
  private FoodRepository foodRepository;
  @Mock
  private FoodMapper foodMapper;
  @Mock
  private FoodCategoryRepository foodCategoryRepository;
  @Mock
  private UserFoodRepository userFoodRepository;

  @InjectMocks
  private FoodServiceImpl foodService;
  @BeforeEach
  void setup() {

  }

  @Test
  void createFood_shouldCreate() {

  }

  @Test
  void getFood_shouldReturnFoodResponseDto_whenFoodFound() {

  }

  @Test
  void getFood_shouldNotMap_whenFooNotFound() {

  }

  @Test
  void getAllFoods_shouldReturnAllFoods_whenFoodsFound() {

  }

  @Test
  void getAllFoods_shouldReturnEmptyList_whenFoodNotFound() {

  }

  @Test
  void calculateFoodMacros_shouldCalculateFoodMacros_whenFoodFound() {

  }

  @Test
  void calculateFoodMacros_shouldThrowException_whenFoodNotFound() {

  }

  @Test
  void searchFoods_shouldReturnFoods_whenFoodsFound() {

  }

  @Test
  void searchFoods_shouldReturnEmptyList_whenNoFoodsFound() {

  }

  @Test
  void getFoodsByUser_shouldReturnFoods_whenUserAndFoodsFound() {

  }

  @Test
  void getFoodsByUser_shouldThrowException_whenUserNotFound() {

  }

  @Test
  void getFoodsByUser_shouldReturnEmptyList_whenNoFoodsFound() {

  }

  @Test
  void getAllCategories_shouldReturnFoodCategories_whenFoodCategoriesFound() {

  }

  @Test
  void getAllCategories_shouldReturnEmptyList_whenNoFoodCategoriesFound() {

  }

  @Test
  void getFoodsByCategory_shouldReturnFoods_whenFoodsFound() {

  }

  @Test
  void getFoodsByCategory_shouldReturnEmptyList_whenNoFoodsFound() {

  }
}



