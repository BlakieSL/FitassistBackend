package source.code.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.model.Food.Food;
import source.code.model.User.User;
import source.code.model.User.UserFood;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.UserFoodService;

import java.util.NoSuchElementException;

@Service
public class UserFoodServiceImpl implements UserFoodService {
  private final UserFoodRepository userFoodRepository;
  private final FoodRepository foodRepository;
  private final UserRepository userRepository;

  public UserFoodServiceImpl(
          UserFoodRepository userFoodRepository,
          FoodRepository foodRepository,
          UserRepository userRepository) {
    this.userFoodRepository = userFoodRepository;
    this.foodRepository = foodRepository;
    this.userRepository = userRepository;
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

  private boolean isFoodAlreadySaved(int userId, int foodId, short type) {
    return userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type);
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

  public LikesAndSavesResponseDto calculateFoodLikesAndSaves(int foodId) {
    foodRepository.findById(foodId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Food with id: " + foodId + " not found"));

    long saves = userFoodRepository.countByFoodIdAndType(foodId, (short) 1);
    long likes = userFoodRepository.countByFoodIdAndType(foodId, (short) 2);

    return new LikesAndSavesResponseDto(likes, saves);
  }
}
