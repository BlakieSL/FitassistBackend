package source.code.service;

import source.code.dto.response.DailyFoodsResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.dto.request.DailyCartFoodCreateDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.helper.ValidationHelper;
import source.code.model.DailyFood;
import source.code.model.DailyCartFood;
import source.code.model.Food;
import source.code.model.User;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyFoodService {
    private final ValidationHelper validationHelper;
    private final JsonPatchHelper jsonPatchHelper;
    private final DailyFoodRepository dailyFoodRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    public DailyFoodService(
            DailyFoodRepository dailyFoodRepository,
            FoodRepository foodRepository,
            UserRepository userRepository,
            ValidationHelper validationHelper,
            JsonPatchHelper jsonPatchHelper) {
        this.dailyFoodRepository = dailyFoodRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.validationHelper = validationHelper;
        this.jsonPatchHelper = jsonPatchHelper;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
    @Transactional
    public void updateDailyCarts() {
        List<DailyFood> carts = dailyFoodRepository.findAll();
        LocalDate today = LocalDate.now();
        for (DailyFood cart : carts) {
            cart.setDate(today);
            cart.getDailyCartFoods().clear();
            dailyFoodRepository.save(cart);
        }
    }

    @Transactional
    public void addFoodToDailyCartFood(int userId, int foodId, DailyCartFoodCreateDto dto) {
        validationHelper.validate(dto);
        DailyFood dailyFood = getDailyFoodByUser(userId);

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found"));


        Optional<DailyCartFood> existingDailyCartFood = dailyFood.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst();

        if (existingDailyCartFood.isPresent()) {
            DailyCartFood dailyCartFood = existingDailyCartFood.get();
            dailyCartFood.setAmount(dailyCartFood.getAmount() + dto.getAmount());
        } else {
            DailyCartFood dailyCartFood = new DailyCartFood();
            dailyCartFood.setDailyFoodFood(dailyFood);
            dailyCartFood.setFood(food);
            dailyCartFood.setAmount(dto.getAmount());
            dailyFood.getDailyCartFoods().add(dailyCartFood);
        }

        dailyFoodRepository.save(dailyFood);
    }

    @Transactional
    public void removeFoodFromDailyCartFood(int userId, int foodId) {
        DailyFood dailyFood = getDailyFoodByUser(userId);
        DailyCartFood dailyCartFood = dailyFood.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found"));
        dailyFood.getDailyCartFoods().remove(dailyCartFood);
        dailyFoodRepository.save(dailyFood);
    }

    @Transactional
    public void updateDailyCartFood(int userId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        DailyFood dailyFood = getDailyFoodByUser(userId);

        DailyCartFood dailyCartFood = dailyFood.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found in daily cart"));

        DailyCartFoodCreateDto dailyCartFoodCreateDto = new DailyCartFoodCreateDto();
        dailyCartFoodCreateDto.setAmount(dailyCartFood.getAmount());

        DailyCartFoodCreateDto patchedDailyCartFoodCreateDto = jsonPatchHelper.applyPatch(patch, dailyCartFoodCreateDto, DailyCartFoodCreateDto.class);

        validationHelper.validate(patchedDailyCartFoodCreateDto);

        dailyCartFood.setAmount(patchedDailyCartFoodCreateDto.getAmount());
        dailyFoodRepository.save(dailyFood);
    }

    private DailyFood getDailyFoodByUser(int userId) {
        return dailyFoodRepository.findByUserId(userId)
                .orElseGet(() -> createNewDailyCartForUser(userId));
    }

    private DailyFood createNewDailyCartForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " not found"));
        DailyFood newDailyFood = new DailyFood();
        newDailyFood.setUser(user);
        newDailyFood.setDate(LocalDate.now());
        return dailyFoodRepository.save(newDailyFood);
    }

    public DailyFoodsResponseDto getFoodsFromDailyCartFood(int userId) {
        DailyFood dailyFood = getDailyFoodByUser(userId);

        List<FoodCalculatedMacrosResponseDto> foods = dailyFood.getDailyCartFoods().stream()
                .map(
                        dailyCartFood -> {
                            Food food = dailyCartFood.getFood();
                            double factor = (double) dailyCartFood.getAmount() / 100;
                            return new FoodCalculatedMacrosResponseDto(
                                    food.getId(),
                                    food.getName(),
                                    food.getCalories() * factor,
                                    food.getProtein() * factor,
                                    food.getFat() * factor,
                                    food.getCarbohydrates() * factor,
                                    food.getFoodCategory().getId(),
                                    food.getFoodCategory().getName(),
                                    dailyCartFood.getAmount());
                        })
                .collect(Collectors.toList());
        double totalCalories = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getCalories).sum();
        double totalCarbohydrates = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getCarbohydrates).sum();
        double totalProtein = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getProtein).sum();
        double totalFat = foods.stream().mapToDouble(FoodCalculatedMacrosResponseDto::getFat).sum();
        return new DailyFoodsResponseDto(foods, totalCalories, totalCarbohydrates, totalProtein, totalFat);
    }
}
