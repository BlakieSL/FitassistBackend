package source.code.service;

import source.code.dto.DailyCartResponse;
import source.code.helper.JsonPatchHelper;
import source.code.dto.DailyFoodDto;
import source.code.dto.FoodCalculatedDto;
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
    public void addFoodToCart(int userId, int foodId, DailyFoodDto dto) {
        validationHelper.validate(dto);
        DailyFood dailyFood = getDailyCartByUserId(userId);

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
    public void removeFoodFromCart(int userId, int foodId) {
        DailyFood dailyFood = getDailyCartByUserId(userId);
        DailyCartFood dailyCartFood = dailyFood.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found"));
        dailyFood.getDailyCartFoods().remove(dailyCartFood);
        dailyFoodRepository.save(dailyFood);
    }

    @Transactional
    public void modifyDailyCartFood(int userId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        DailyFood dailyFood = getDailyCartByUserId(userId);

        DailyCartFood dailyCartFood = dailyFood.getDailyCartFoods().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found in daily cart"));

        DailyFoodDto dailyFoodDto = new DailyFoodDto();
        dailyFoodDto.setAmount(dailyCartFood.getAmount());

        DailyFoodDto patchedDailyFoodDto = jsonPatchHelper.applyPatch(patch, dailyFoodDto, DailyFoodDto.class);

        validationHelper.validate(patchedDailyFoodDto);

        dailyCartFood.setAmount(patchedDailyFoodDto.getAmount());
        dailyFoodRepository.save(dailyFood);
    }

    private DailyFood getDailyCartByUserId(int userId) {
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

    public DailyCartResponse getFoodsInCart(int userId) {
        DailyFood dailyFood = getDailyCartByUserId(userId);

        List<FoodCalculatedDto> foods = dailyFood.getDailyCartFoods().stream()
                .map(
                        dailyCartFood -> {
                            Food food = dailyCartFood.getFood();
                            double factor = (double) dailyCartFood.getAmount() / 100;
                            return new FoodCalculatedDto(
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
        double totalCalories = foods.stream().mapToDouble(FoodCalculatedDto::getCalories).sum();
        double totalCarbohydrates = foods.stream().mapToDouble(FoodCalculatedDto::getCarbohydrates).sum();
        double totalProtein = foods.stream().mapToDouble(FoodCalculatedDto::getProtein).sum();
        double totalFat = foods.stream().mapToDouble(FoodCalculatedDto::getFat).sum();
        return new DailyCartResponse(foods, totalCalories, totalCarbohydrates, totalProtein, totalFat);
    }
}
