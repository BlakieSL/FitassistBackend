package source.code.service;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.model.Food;
import source.code.model.User;
import source.code.model.UserFood;
import source.code.repository.FoodRepository;
import source.code.repository.UserFoodRepository;
import source.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserFoodService {
    private final UserFoodRepository userFoodRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public UserFoodService(
            final UserFoodRepository userFoodRepository,
            final FoodRepository foodRepository,
            final UserRepository userRepository){
        this.userFoodRepository = userFoodRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveFoodToUser(int foodId, int userId, short type) {
        if(userFoodRepository.existsByUserIdAndFoodIdAndType(userId, foodId, type)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId +
                            " already has saved food with id: " + foodId +
                            " and type: " + type);
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + userId + " not found"));

        Food food = foodRepository
                .findById(foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Food with id: " + foodId + " not found"));

        UserFood userFood = new UserFood();
        userFood.setUser(user);
        userFood.setFood(food);
        userFood.setType(type);
        userFoodRepository.save(userFood);
    }

    @Transactional
    public void deleteSavedFoodFromUser(int foodId, int userId, short type) {
        UserFood userFood = userFoodRepository
                .findByUserIdAndFoodIdAndType(userId, foodId, type)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserFood with user id: " + userId +
                                ", food id: " + foodId +
                                " and type: " + type + "not found"));

        userFoodRepository.delete(userFood);
    }

    public LikesAndSavesResponseDto calculateLikesAndSavesByFoodId(int foodId) {
        foodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Food with id: " + foodId + " not found"));

        long saves = userFoodRepository.countByFoodIdAndType(foodId, (short) 1);
        long likes = userFoodRepository.countByFoodIdAndType(foodId, (short) 2);
        return new LikesAndSavesResponseDto(likes, saves);
    }
}
