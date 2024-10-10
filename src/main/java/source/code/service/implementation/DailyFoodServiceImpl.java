package source.code.service.implementation;

import source.code.dto.response.DailyFoodsResponseDto;
import source.code.helper.JsonPatchHelper;
import source.code.dto.request.DailyFoodItemCreateDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.helper.ValidationHelper;
import source.code.mapper.DailyFoodMapper;
import source.code.model.DailyFood;
import source.code.model.DailyFoodItem;
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
import source.code.service.declaration.DailyFoodService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DailyFoodServiceImpl implements DailyFoodService {
    private final ValidationHelper validationHelper;
    private final JsonPatchHelper jsonPatchHelper;
    private final DailyFoodMapper dailyFoodMapper;
    private final DailyFoodRepository dailyFoodRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    public DailyFoodServiceImpl(
            DailyFoodRepository dailyFoodRepository,
            FoodRepository foodRepository,
            UserRepository userRepository,
            ValidationHelper validationHelper,
            JsonPatchHelper jsonPatchHelper, DailyFoodMapper dailyFoodMapper) {
        this.dailyFoodRepository = dailyFoodRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.validationHelper = validationHelper;
        this.jsonPatchHelper = jsonPatchHelper;
        this.dailyFoodMapper = dailyFoodMapper;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
    @Transactional
    public void updateDailyCarts() {
        List<DailyFood> carts = dailyFoodRepository.findAll();
        LocalDate today = LocalDate.now();
        for (DailyFood cart : carts) {
            cart.setDate(today);
            cart.getDailyFoodItems().clear();
            dailyFoodRepository.save(cart);
        }
    }

    @Transactional
    public void addFoodToDailyFoodItem(int userId, int foodId, DailyFoodItemCreateDto dto) {
        validationHelper.validate(dto);
        DailyFood dailyFood = getDailyFoodByUser(userId);

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found"));


        Optional<DailyFoodItem> existingDailyFoodItem = dailyFood.getDailyFoodItems().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst();

        if (existingDailyFoodItem.isPresent()) {
            DailyFoodItem dailyFoodItem = existingDailyFoodItem.get();
            dailyFoodItem.setAmount(dailyFoodItem.getAmount() + dto.getAmount());
        } else {
            DailyFoodItem dailyFoodItem = new DailyFoodItem();
            dailyFoodItem.setDailyFoodFood(dailyFood);
            dailyFoodItem.setFood(food);
            dailyFoodItem.setAmount(dto.getAmount());
            dailyFood.getDailyFoodItems().add(dailyFoodItem);
        }

        dailyFoodRepository.save(dailyFood);
    }

    @Transactional
    public void removeFoodFromDailyFoodItem(int userId, int foodId) {
        DailyFood dailyFood = getDailyFoodByUser(userId);
        DailyFoodItem dailyFoodItem = dailyFood.getDailyFoodItems().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found"));
        dailyFood.getDailyFoodItems().remove(dailyFoodItem);
        dailyFoodRepository.save(dailyFood);
    }

    @Transactional
    public void updateDailyFoodItem(int userId, int foodId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        DailyFood dailyFood = getDailyFoodByUser(userId);

        DailyFoodItem dailyFoodItem = dailyFood.getDailyFoodItems().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Food with id: " + foodId + " not found in daily cart"));

        DailyFoodItemCreateDto dailyFoodItemCreateDto = new DailyFoodItemCreateDto();
        dailyFoodItemCreateDto.setAmount(dailyFoodItem.getAmount());

        DailyFoodItemCreateDto patchedDailyFoodItemCreateDto = jsonPatchHelper.applyPatch(patch, dailyFoodItemCreateDto, DailyFoodItemCreateDto.class);

        validationHelper.validate(patchedDailyFoodItemCreateDto);

        dailyFoodItem.setAmount(patchedDailyFoodItemCreateDto.getAmount());
        dailyFoodRepository.save(dailyFood);
    }

    private DailyFood getDailyFoodByUser(int userId) {
        return dailyFoodRepository.findByUserId(userId)
                .orElseGet(() -> createNewDailyFoodForUser(userId));
    }

    private DailyFood createNewDailyFoodForUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + userId + " not found"));

        DailyFood newDailyFood = DailyFood.createForToday(user);

        return dailyFoodRepository.save(newDailyFood);
    }

    public DailyFoodsResponseDto getFoodsFromDailyFoodItem(int userId) {
        DailyFood dailyFood = getDailyFoodByUser(userId);

        List<FoodCalculatedMacrosResponseDto> foods = dailyFood.getDailyFoodItems().stream()
                .map(dailyFoodMapper::toFoodCalculatedMacrosResponseDto)
                .collect(Collectors.toList());

        return dailyFoodMapper.toDailyFoodsResponseDto(foods);
    }
}
