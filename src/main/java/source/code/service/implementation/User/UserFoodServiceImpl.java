package source.code.service.implementation.User;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.FoodResponseDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.mapper.Food.FoodMapper;
import source.code.model.Food.Food;
import source.code.model.User.User;
import source.code.model.User.UserFood;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.User.UserFoodService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserFoodServiceImpl implements UserFoodService {
  private final UserFoodRepository userFoodRepository;
  private final FoodRepository foodRepository;
  private final UserRepository userRepository;
  private final FoodMapper foodMapper;

  public UserFoodServiceImpl(
          UserFoodRepository userFoodRepository,
          FoodRepository foodRepository,
          UserRepository userRepository,
          FoodMapper foodMapper) {
    this.userFoodRepository = userFoodRepository;
    this.foodRepository = foodRepository;
    this.userRepository = userRepository;
    this.foodMapper = foodMapper;
  }

  @Transactional
  public void saveFoodToUser(int userId, int foodId, short type) {
    if (isFoodAlreadySaved(userId, foodId, type)) {
      throw new NotUniqueRecordException(
              "User with id: " + userId
                      + " already has saved food with id: " + foodId
                      + " and type: " + type);
    }

    User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));

    Food food = foodRepository
            .findById(foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + foodId + " not found"));

    UserFood userFood =
            UserFood.createWithUserFoodType(user, food, type);
    userFoodRepository.save(userFood);
  }

  @Transactional
  public void deleteSavedFoodFromUser(int foodId, int userId, short type) {
    UserFood userFood = userFoodRepository
            .findByUserIdAndFoodIdAndType(userId, foodId, type)
            .orElseThrow(() -> new NoSuchElementException(
                    "UserFood with user id: " + userId
                            + ", food id: " + foodId
                            + " and type: " + type + "not found"));

    userFoodRepository.delete(userFood);
  }

  public List<FoodResponseDto> getFoodsByUserAndType(int userId, short type) {
    List<UserFood> userFoods = userFoodRepository.findByUserIdAndType(userId, type);

    List<Food> foods = userFoods.stream()
            .map(UserFood::getFood)
            .collect(Collectors.toList());

    return foods.stream()
            .map(foodMapper::toResponseDto)
            .collect(Collectors.toList());
  }

  public LikesAndSavesResponseDto calculateFoodLikesAndSaves(int foodId) {
    foodRepository.findById(foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + foodId + " not found"));

    long saves = userFoodRepository.countByFoodIdAndType(foodId, (short) 1);
    long likes = userFoodRepository.countByFoodIdAndType(foodId, (short) 2);

    return new LikesAndSavesResponseDto(likes, saves);
  }

  private boolean isFoodAlreadySaved(int userId, int foodId, short type) {
    return userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type);
  }
}
