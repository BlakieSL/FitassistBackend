package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Components.JsonPatchHelper;
import com.example.simplefullstackproject.Dtos.DailyCartFoodDto;
import com.example.simplefullstackproject.Dtos.FoodDtoResponse;
import com.example.simplefullstackproject.Dtos.UserUpdateRequest;
import com.example.simplefullstackproject.Models.DailyCart;
import com.example.simplefullstackproject.Models.DailyCartFood;
import com.example.simplefullstackproject.Models.Food;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Repositories.DailyCartRepository;
import com.example.simplefullstackproject.Repositories.FoodRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyCartService {
    private final DailyCartRepository dailyCartRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final ValidationHelper validationHelper;
    private final JsonPatchHelper jsonPatchHelper;
    public DailyCartService(
            DailyCartRepository dailyCartRepository,
            FoodRepository foodRepository,
            UserRepository userRepository,
            ValidationHelper validationHelper,
            JsonPatchHelper jsonPatchHelper) {
        this.dailyCartRepository = dailyCartRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.validationHelper = validationHelper;
        this.jsonPatchHelper = jsonPatchHelper;
    }

    private DailyCart getDailyCartByUserId(Integer userId) {
        return dailyCartRepository.findByUserId(userId)
                .orElseGet(() -> createNewDailyCartForUser(userId));
    }

    private DailyCart createNewDailyCartForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " not found"));
        DailyCart newDailyCart = new DailyCart();
        newDailyCart.setUser(user);
        newDailyCart.setDate(LocalDate.now());
        return dailyCartRepository.save(newDailyCart);
    }

    @Transactional
    public void addFoodToCart(Integer userId, DailyCartFoodDto dto)
    {
        validationHelper.validate(dto);
        DailyCart dailyCart = getDailyCartByUserId(userId);

        Food food = foodRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + dto.getId() + " not found"));


        Optional<DailyCartFood> existingDailyCartFood = dailyCart.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(dto.getId()))
                .findFirst();

        if (existingDailyCartFood.isPresent()) {
            DailyCartFood dailyCartFood = existingDailyCartFood.get();
            dailyCartFood.setAmount(dailyCartFood.getAmount() + dto.getAmount());
        } else {
            DailyCartFood dailyCartFood = new DailyCartFood();
            dailyCartFood.setDailyCartFood(dailyCart);
            dailyCartFood.setFood(food);
            dailyCartFood.setAmount(dto.getAmount());
            dailyCart.getDailyCartFoods().add(dailyCartFood);
        }

        dailyCartRepository.save(dailyCart);
    }

    @Transactional
    public void removeFoodFromCart(Integer userId, Integer foodId) {
        DailyCart dailyCart = getDailyCartByUserId(userId);
        DailyCartFood dailyCartFood = dailyCart.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found"));
        dailyCart.getDailyCartFoods().remove(dailyCartFood);
        dailyCartRepository.save(dailyCart);
    }

    public List<FoodDtoResponse> getFoodsInCart(Integer userId) {
        DailyCart dailyCart = getDailyCartByUserId(userId);

        return dailyCart.getDailyCartFoods().stream()
                .map(
                        dailyCartFood -> {
                            Food food = dailyCartFood.getFood();
                            double factor = (double) dailyCartFood.getAmount() / 100;
                            return new FoodDtoResponse(
                                    food.getId(),
                                    food.getName(),
                                    food.getCalories() * factor,
                                    food.getProtein() * factor,
                                    food.getFat() * factor,
                                    food.getCarbohydrates() * factor,
                                    food.getCategory().getId(),
                                    dailyCartFood.getAmount());
                        })
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
    @Transactional
    public void updateDailyCarts() {
        List<DailyCart> carts = dailyCartRepository.findAll();
        LocalDate today = LocalDate.now();
        for (DailyCart cart : carts) {
            cart.setDate(today);
            cart.getDailyCartFoods().clear();
            dailyCartRepository.save(cart);
        }
    }

    @Transactional
    public void modifyDailyCartFood(Integer userId, Integer foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        DailyCart dailyCart = getDailyCartByUserId(userId);

        DailyCartFood dailyCartFood = dailyCart.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found in daily cart"));

        DailyCartFoodDto dailyCartFoodDto = new DailyCartFoodDto();
        dailyCartFoodDto.setId(dailyCartFood.getFood().getId());
        dailyCartFoodDto.setAmount(dailyCartFood.getAmount());

        DailyCartFoodDto patchedDailyCartFoodDto = jsonPatchHelper.applyPatch(patch, dailyCartFoodDto, DailyCartFoodDto.class);

        dailyCartFood.setAmount(patchedDailyCartFoodDto.getAmount());
        dailyCartRepository.save(dailyCart);
    }
}
