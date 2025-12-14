package source.code.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.request.food.DailyCartFoodCreateDto;
import source.code.dto.request.food.DailyCartFoodGetDto;
import source.code.dto.request.food.DailyCartFoodUpdateDto;
import source.code.dto.response.daily.DailyFoodsResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyFoodMapper;
import source.code.model.daily.DailyCart;
import source.code.model.daily.DailyCartFood;
import source.code.model.food.Food;
import source.code.model.user.User;
import source.code.repository.DailyCartFoodRepository;
import source.code.repository.DailyCartRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.daily.DailyFoodService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

@Service
public class DailyFoodServiceImpl implements DailyFoodService {
    private final ValidationService validationService;
    private final JsonPatchService jsonPatchService;
    private final DailyFoodMapper dailyFoodMapper;
    private final RepositoryHelper repositoryHelper;
    private final DailyCartRepository dailyCartRepository;
    private final DailyCartFoodRepository dailyCartFoodRepository;
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
            DailyCartFoodRepository dailyCartFoodRepository) {
        this.validationService = validationService;
        this.repositoryHelper = repositoryHelper;
        this.dailyCartRepository = dailyCartRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
        this.jsonPatchService = jsonPatchService;
        this.dailyFoodMapper = dailyFoodMapper;
        this.dailyCartFoodRepository = dailyCartFoodRepository;
    }

    @Override
    @Transactional
    public void addFoodToDailyCart(int foodId, DailyCartFoodCreateDto dto) {
        int userId = AuthorizationUtil.getUserId();
        DailyCart dailyCart = getOrCreateDailyCartForUser(userId, dto.getDate());
        Food food = repositoryHelper.find(foodRepository, Food.class, foodId);

        updateOrAddDailyFoodItem(dailyCart, food, dto.getQuantity());
        dailyCartRepository.save(dailyCart);
    }

    @Override
    @Transactional
    public void removeFoodFromDailyCart(int dailyCartFoodId) {
        DailyCartFood dailyCartFood = findWithoutAssociations(dailyCartFoodId);
        dailyCartFoodRepository.delete(dailyCartFood);
    }

    @Override
    @Transactional
    public void updateDailyFoodItem(int dailyCartFoodId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        DailyCartFood dailyCartFood = findWithoutAssociations(dailyCartFoodId);

        DailyCartFoodUpdateDto patchedDto = applyPatchToDailyFoodItem(patch);
        validationService.validate(patchedDto);

        updateAmount(dailyCartFood, patchedDto.getQuantity());
        dailyCartFoodRepository.save(dailyCartFood);
    }

    @Override
    public DailyFoodsResponseDto getFoodFromDailyCart(DailyCartFoodGetDto request) {
        int userId = AuthorizationUtil.getUserId();

        return dailyCartRepository
                .findByUserIdAndDateWithFoodAssociations(userId, request.getDate())
                .map(dailyCart -> DailyFoodsResponseDto.create(dailyCart.getDailyCartFoods().stream()
                        .map(dailyFoodMapper::toFoodCalculatedMacrosResponseDto)
                        .toList()))
                .orElse(DailyFoodsResponseDto.of(Collections.emptyList()));
    }

    private void updateOrAddDailyFoodItem(DailyCart dailyCart, Food food, BigDecimal quantity) {
        dailyCartFoodRepository
                .findByDailyCartIdAndFoodId(dailyCart.getId(), food.getId())
                .ifPresentOrElse(foundItem -> {
                            BigDecimal newQuantity = foundItem.getQuantity().add(quantity);
                            foundItem.setQuantity(newQuantity);
                        },
                        () -> {
                            DailyCartFood newItem = DailyCartFood.of(food, dailyCart, quantity);
                            dailyCart.getDailyCartFoods().add(newItem);
                        }
                );
    }

    private void updateAmount(DailyCartFood dailyCartFood, BigDecimal quantity) {
        dailyCartFood.setQuantity(quantity);
    }

    public DailyCart createDailyCart(int userId, LocalDate date) {
        User user = repositoryHelper.find(userRepository, User.class, userId);
        return dailyCartRepository.save(DailyCart.of(user, date));
    }

    private DailyCartFoodUpdateDto applyPatchToDailyFoodItem(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        return jsonPatchService.createFromPatch(patch, DailyCartFoodUpdateDto.class);
    }

    private DailyCart getOrCreateDailyCartForUser(int userId, LocalDate date) {
        return dailyCartRepository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> createDailyCart(userId, date));
    }

    private DailyCartFood findWithoutAssociations(int dailyCartFoodId) {
        return dailyCartFoodRepository.findByIdWithoutAssociations(dailyCartFoodId)
                .orElseThrow(() -> new RecordNotFoundException(DailyCartFood.class, dailyCartFoodId));
    }
}
