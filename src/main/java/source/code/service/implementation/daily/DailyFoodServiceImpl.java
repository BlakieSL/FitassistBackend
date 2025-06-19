package source.code.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.food.DailyFoodItemCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyFoodMapper;
import source.code.model.daily.DailyCart;
import source.code.model.food.DailyFoodItem;
import source.code.model.food.Food;
import source.code.model.user.profile.User;
import source.code.repository.DailyCartRepository;
import source.code.repository.DailyFoodItemRepository;
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
    private final DailyCartRepository dailyCartRepository;
    private final DailyFoodItemRepository dailyFoodItemRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public DailyFoodServiceImpl(
            ValidationService validationService,
            RepositoryHelper repositoryHelper,
            DailyCartRepository dailyCartRepository,
            FoodRepository foodRepository,
            UserRepository userRepository,
            JsonPatchService jsonPatchService,
            DailyFoodMapper dailyFoodMapper,
            DailyFoodItemRepository dailyFoodItemRepository) {
        this.validationService = validationService;
        this.repositoryHelper = repositoryHelper;
        this.dailyCartRepository = dailyCartRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.jsonPatchService = jsonPatchService;
        this.dailyFoodMapper = dailyFoodMapper;
        this.dailyFoodItemRepository = dailyFoodItemRepository;
    }

    @Override
    @Transactional
    public void addFoodToDailyCart(int foodId, DailyFoodItemCreateDto dto) {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getOrCreateDailyCartForUser(userId);
        Food food = repositoryHelper.find(foodRepository, Food.class, foodId);

        updateOrAddDailyFoodItem(dailyCart, food, dto.getAmount());
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public void removeFoodFromDailyCart(int foodId) {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getDailyFoodForUserOrThrow(userId);
        DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyCart.getId(), foodId);

        dailyCart.getDailyFoodItems().remove(dailyFoodItem);
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public void updateDailyFoodItem(int foodId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyFood = getDailyFoodForUserOrThrow(userId);
        DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

        DailyFoodItemCreateDto patchedDto = applyPatchToDailyFoodItem(dailyFoodItem, patch);
        validationService.validate(patchedDto);

        updateAmount(dailyFoodItem, patchedDto.getAmount());
        dailyCartRepository.save(dailyFood);
    }

    @Override
    @Transactional
    public DailyFoodsResponseDto getFoodFromDailyCart() {
        int userId = AuthorizationUtil.getUserId();

        return Optional.ofNullable(getOrCreateDailyCartForUser(userId))
                .map(DailyCart::getDailyFoodItems)
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
            DailyCart dailyCart,
            Food food,
            int amount
    ) {
        dailyFoodItemRepository.findByDailyCartIdAndFoodId(dailyCart.getId(), food.getId())
                .ifPresentOrElse(
                        foundItem -> foundItem.setAmount(foundItem.getAmount() + amount),
                        () -> {
                            DailyFoodItem newItem = DailyFoodItem.of(food, dailyCart, amount);
                            dailyCart.getDailyFoodItems().add(newItem);
                        }
                );
    }

    private DailyCart getOrCreateDailyCartForUser(int userId) {
        return dailyCartRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseGet(() -> createDailyCart(userId));
    }

    private DailyCart getDailyFoodForUserOrThrow(int userId) {
        return dailyCartRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow(() -> RecordNotFoundException.of(DailyCart.class, userId));
    }

    private void updateAmount(DailyFoodItem dailyFoodItem, int amount) {
        dailyFoodItem.setAmount(amount);
    }

    public DailyCart createDailyCart(int userId) {
        User user = repositoryHelper.find(userRepository, User.class, userId);
        return dailyCartRepository.save(DailyCart.createForToday(user));
    }

    private DailyFoodItemCreateDto applyPatchToDailyFoodItem(
            DailyFoodItem dailyFoodItem, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        DailyFoodItemCreateDto createDto = DailyFoodItemCreateDto.of(dailyFoodItem.getAmount());
        return jsonPatchService.applyPatch(patch, createDto, DailyFoodItemCreateDto.class);
    }

    private DailyFoodItem getDailyFoodItem(int dailyFoodId, int foodId) {
        return dailyFoodItemRepository.findByDailyCartIdAndFoodId(dailyFoodId, foodId)
                .orElseThrow(() -> RecordNotFoundException.of(DailyFoodItem.class, foodId));
    }
}
