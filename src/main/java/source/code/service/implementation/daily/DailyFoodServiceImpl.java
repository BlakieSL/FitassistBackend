package source.code.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import source.code.dto.Request.Food.DailyFoodItemCreateDto;
import source.code.dto.Response.DailyFoodsResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.daily.DailyFoodMapper;
import source.code.model.Food.DailyFood;
import source.code.model.Food.DailyFoodItem;
import source.code.model.Food.Food;
import source.code.model.User.User;
import source.code.repository.DailyFoodItemRepository;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.daily.DailyFoodService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.time.LocalDate;

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

    @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
    @Transactional
    public void resetDailyCarts() {
        dailyFoodRepository.findAll().forEach(cart -> {
            resetDailyFood(cart);
            dailyFoodRepository.save(cart);
        });
    }

    @Override
    @Transactional
    public void addFoodToDailyFoodItem(int userId, int foodId, DailyFoodItemCreateDto dto) {
        DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
        Food food = repositoryHelper.find(foodRepository, Food.class, foodId);
        DailyFoodItem dailyFoodItem = getOrCreateDailyFoodItem(dailyFood, food, dto.getAmount());

        updateOrAddDailyFoodItem(dailyFood, dailyFoodItem);
        dailyFoodRepository.save(dailyFood);
    }

    @Override
    @Transactional
    public void removeFoodFromDailyFoodItem(int userId, int foodId) {
        DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
        DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

        dailyFood.getDailyFoodItems().remove(dailyFoodItem);
        dailyFoodRepository.save(dailyFood);
    }

    @Override
    @Transactional
    public void updateDailyFoodItem(int userId, int foodId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
        DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

        DailyFoodItemCreateDto patchedDto = applyPatchToDailyFoodItem(dailyFoodItem, patch);
        validationService.validate(patchedDto);

        updateDailyFoodItemAmount(dailyFoodItem, patchedDto.getAmount());
        dailyFoodRepository.save(dailyFood);
    }

    @Override
    public DailyFoodsResponseDto getFoodsFromDailyFoodItem(int userId) {
        DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);

        return dailyFoodMapper.toDailyFoodsResponseDto(
                dailyFood.getDailyFoodItems().stream()
                        .map(dailyFoodMapper::toFoodCalculatedMacrosResponseDto)
                        .toList()
        );
    }

    private DailyFoodItem getOrCreateDailyFoodItem(DailyFood dailyFood, Food food, int amount) {
        return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), food.getId())
                .orElse(DailyFoodItem.of(amount, food, dailyFood));
    }

    private void updateOrAddDailyFoodItem(DailyFood dailyFood, DailyFoodItem dailyFoodItem) {
        if (existByDailyFoodAndItem(dailyFoodItem, dailyFood)) {
            dailyFood.getDailyFoodItems().add(dailyFoodItem);
        }
        updateDailyFoodItemAmount(dailyFoodItem, dailyFoodItem.getAmount());
    }

    private void updateDailyFoodItemAmount(DailyFoodItem dailyFoodItem, int amount) {
        dailyFoodItem.setAmount(amount);
    }

    private DailyFood getOrCreateDailyFoodForUser(int userId) {
        return dailyFoodRepository.findByUserId(userId)
                .orElseGet(() -> createDailyFood(userId));
    }

    @Transactional
    public DailyFood createDailyFood(int userId) {
        User user = repositoryHelper.find(userRepository, User.class, userId);
        return dailyFoodRepository.save(DailyFood.createForToday(user));
    }

    private DailyFoodItemCreateDto applyPatchToDailyFoodItem(
            DailyFoodItem dailyFoodItem, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        DailyFoodItemCreateDto createDto = DailyFoodItemCreateDto.of(dailyFoodItem.getAmount());
        return jsonPatchService.applyPatch(patch, createDto, DailyFoodItemCreateDto.class);
    }

    private DailyFoodItem getDailyFoodItem(int dailyFoodId, int foodId) {
        return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFoodId, foodId)
                .orElseThrow(() -> RecordNotFoundException.of(DailyFoodItem.class, foodId));
    }

    private boolean existByDailyFoodAndItem(DailyFoodItem dailyFoodItem, DailyFood dailyFood) {
        return dailyFoodItemRepository.existsByIdAndDailyFoodId(
                dailyFoodItem.getId(),
                dailyFood.getId()
        );
    }

    private void resetDailyFood(DailyFood dailyFood) {
        dailyFood.setDate(LocalDate.now());
        dailyFood.getDailyFoodItems().clear();
    }
}
