package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Exceptions.NotUniqueRecordException;
import com.example.simplefullstackproject.Models.Food;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Models.UserFood;
import com.example.simplefullstackproject.Repositories.FoodRepository;
import com.example.simplefullstackproject.Repositories.UserFoodRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;
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
    public void addFoodToUser(Integer foodId, Integer userId) {
        if(userFoodRepository.existsByUserIdAndFoodId(userId, foodId)){
            throw new NotUniqueRecordException(
                    "User with id: " + userId + " already has food with id: " + foodId);
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
        userFoodRepository.save(userFood);
    }

    @Transactional
    public void deleteFoodFromUser(Integer foodId, Integer userId) {
        UserFood userFood = userFoodRepository
                .findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserFood with user id: " + userId +
                                " and food id: " + foodId + " not found"));

        userFoodRepository.delete(userFood);
    }
}
