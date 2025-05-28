package source.code.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import source.code.dto.request.food.DailyFoodItemCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyFoodMapper;
import source.code.model.food.DailyFood;
import source.code.model.food.DailyFoodItem;
import source.code.model.food.Food;
import source.code.model.user.profile.User;
import source.code.repository.DailyFoodItemRepository;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.daily.DailyFoodService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Service
public class DailyFoodServiceImpl implements DailyFoodService {
    private final ValidationService validationService;
    private final JsonPatchService jsonPatchService;
    private final DailyFoodMapper dailyFoodMapper;
    private final RepositoryHelper repositoryHelper;
    private final DailyFoodRepository dailyFoodRepository;
    private final DailyFoodItemRepository dailyFoodItemRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public DailyFoodServiceImpl(
            ValidationService validationService,
            RepositoryHelper repositoryHelper,
            DailyFoodRepository dailyFoodRepository,
            FoodRepository foodRepository,
            UserRepository userRepository,
            JsonPatchService jsonPatchService,
            DailyFoodMapper dailyFoodMapper,
            DailyFoodItemRepository dailyFoodItemRepository) {
        this.validationService = validationService;
        this.repositoryHelper = repositoryHelper;
        this.dailyFoodRepository = dailyFoodRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.jsonPatchService = jsonPatchService;
        this.dailyFoodMapper = dailyFoodMapper;
        this.dailyFoodItemRepository = dailyFoodItemRepository;
    }

    @Override
    @Transactional
    public void addFoodToDailyFoodItem(int foodId, DailyFoodItemCreateDto dto) {
        int userId = AuthorizationUtil.getUserId();
        DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
        Food food = repositoryHelper.find(foodRepository, Food.class, foodId);

        updateOrAddDailyFoodItem(dailyFood, food, dto.getAmount());
        dailyFoodRepository.save(dailyFood);
    }

    @Override
    @Transactional
    public void removeFoodFromDailyFoodItem(int foodId) {
        int userId = AuthorizationUtil.getUserId();
        DailyFood dailyFood = getDailyFoodForUserOrThrow(userId);
        DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

        dailyFood.getDailyFoodItems().remove(dailyFoodItem);
        dailyFoodRepository.save(dailyFood);
    }

    @Override
    @Transactional
    public void updateDailyFoodItem(int foodId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        int userId = AuthorizationUtil.getUserId();
        DailyFood dailyFood = getDailyFoodForUserOrThrow(userId);
        DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

        DailyFoodItemCreateDto patchedDto = applyPatchToDailyFoodItem(dailyFoodItem, patch);
        validationService.validate(patchedDto);

        updateAmount(dailyFoodItem, patchedDto.getAmount());
        dailyFoodRepository.save(dailyFood);
    }

    @Override
    @Transactional
    public DailyFoodsResponseDto getFoodsFromDailyFoodItem() {
        int userId = AuthorizationUtil.getUserId();

        return Optional.ofNullable(getOrCreateDailyFoodForUser(userId))
                .map(DailyFood::getDailyFoodItems)
                .filter(items -> !items.isEmpty())
                .map(dailyFoodMapper::toDailyFoodsResponseDto)
                .orElseGet(() -> DailyFoodsResponseDto.of(
                        Collections.emptyList(),
                        0,
                        0,
                        0,
                        0)
                );
    }

    private void updateOrAddDailyFoodItem(
            DailyFood dailyFood,
            Food food,
            int amount
    ) {
        dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), food.getId())
                .ifPresentOrElse(
                        foundItem -> foundItem.setAmount(foundItem.getAmount() + amount),
                        () -> {
                            DailyFoodItem newItem = DailyFoodItem.of(food, dailyFood, amount);
                            dailyFood.getDailyFoodItems().add(newItem);
                        }
                );
    }

    private DailyFood getOrCreateDailyFoodForUser(int userId) {
        return dailyFoodRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseGet(() -> createDailyFood(userId));
    }

    private DailyFood getDailyFoodForUserOrThrow(int userId) {
        return dailyFoodRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow(() -> RecordNotFoundException.of(DailyFood.class, userId));
    }

    private void updateAmount(DailyFoodItem dailyFoodItem, int amount) {
        dailyFoodItem.setAmount(amount);
    }

    public DailyFood createDailyFood(int userId) {
        User user = repositoryHelper.find(userRepository, User.class, userId);
        return dailyFoodRepository.save(DailyFood.createForToday(user));
    }

    private DailyFoodItemCreateDto applyPatchToDailyFoodItem(
            DailyFoodItem dailyFoodItem, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        DailyFoodItemCreateDto createDto = DailyFoodItemCreateDto.of(dailyFoodItem.getAmount());
        return jsonPatchService.applyPatch(patch, createDto, DailyFoodItemCreateDto.class);
    }

    private DailyFoodItem getDailyFoodItem(int dailyFoodId, int foodId) {
        return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFoodId, foodId)
                .orElseThrow(() -> RecordNotFoundException.of(DailyFoodItem.class, foodId));
    }
}
