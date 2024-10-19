package source.code.service.implementation.Food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import source.code.dto.request.Food.DailyFoodItemCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.dto.response.FoodCalculatedMacrosResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.JsonPatchHelper;
import source.code.helper.ValidationHelper;
import source.code.mapper.Food.DailyFoodMapper;
import source.code.model.Food.DailyFood;
import source.code.model.Food.DailyFoodItem;
import source.code.model.Food.Food;
import source.code.model.User.User;
import source.code.repository.DailyFoodItemRepository;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.DailyFoodService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyFoodServiceImpl implements DailyFoodService {
  private final ValidationHelper validationHelper;
  private final JsonPatchHelper jsonPatchHelper;
  private final DailyFoodMapper dailyFoodMapper;
  private final DailyFoodRepository dailyFoodRepository;
  private final DailyFoodItemRepository dailyFoodItemRepository;
  private final FoodRepository foodRepository;
  private final UserRepository userRepository;

  public DailyFoodServiceImpl(
          ValidationHelper validationHelper,
          DailyFoodRepository dailyFoodRepository,
          FoodRepository foodRepository,
          UserRepository userRepository,
          JsonPatchHelper jsonPatchHelper,
          DailyFoodMapper dailyFoodMapper,
          DailyFoodItemRepository dailyFoodItemRepository) {
    this.validationHelper = validationHelper;
    this.dailyFoodRepository = dailyFoodRepository;
    this.foodRepository = foodRepository;
    this.userRepository = userRepository;
    this.jsonPatchHelper = jsonPatchHelper;
    this.dailyFoodMapper = dailyFoodMapper;
    this.dailyFoodItemRepository = dailyFoodItemRepository;
  }

  @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
  @Transactional
  public void resetDailyCarts() {
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
    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
    Food food = getFoodById(foodId);

    DailyFoodItem dailyFoodItem = findOrCreateDailyFoodItem(dailyFood, food, dto.getAmount());
    updateOrAddDailyFoodItem(dailyFood, dailyFoodItem);

    dailyFoodRepository.save(dailyFood);
  }

  @Transactional
  public void removeFoodFromDailyFoodItem(int userId, int foodId) {
    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
    DailyFoodItem dailyFoodItem = getDailyFoodItemById(dailyFood.getId(), foodId);

    dailyFood.getDailyFoodItems().remove(dailyFoodItem);
    dailyFoodRepository.save(dailyFood);
  }

  @Transactional
  public void updateDailyFoodItem(int userId, int foodId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
    DailyFoodItem dailyFoodItem = getDailyFoodItemById(dailyFood.getId(), foodId);

    DailyFoodItemCreateDto patchedDto = applyPatchToDailyFoodItem(dailyFoodItem, patch);
    validationHelper.validate(patchedDto);

    updateDailyFoodItemAmount(dailyFoodItem, patchedDto.getAmount());

    dailyFoodRepository.save(dailyFood);
  }

  public DailyFoodsResponseDto getFoodsFromDailyFoodItem(int userId) {
    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);

    List<FoodCalculatedMacrosResponseDto> foods = dailyFood.getDailyFoodItems().stream()
            .map(dailyFoodMapper::toFoodCalculatedMacrosResponseDto)
            .collect(Collectors.toList());

    return dailyFoodMapper.toDailyFoodsResponseDto(foods);
  }

  private DailyFoodItem findOrCreateDailyFoodItem(DailyFood dailyFood, Food food, int amount) {
    return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), food.getId())
            .orElse(DailyFoodItem.createWithAmountFoodDailyFood(amount, food, dailyFood));
  }

  private void updateOrAddDailyFoodItem(DailyFood dailyFood, DailyFoodItem dailyFoodItem) {
    if(!dailyFood.getDailyFoodItems().contains(dailyFoodItem)) {
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
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RecordNotFoundException("User", userId));

    DailyFood newDailyFood = DailyFood.createForToday(user);

    return dailyFoodRepository.save(newDailyFood);
  }

  private DailyFoodItem getDailyFoodItemById(int dailyFoodId, int foodId) {
    return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFoodId, foodId)
            .orElseThrow(() -> new RecordNotFoundException("DailyFoodItem", foodId));
  }

  private Food getFoodById(int foodId) {
    return foodRepository.findById(foodId)
            .orElseThrow(() -> new RecordNotFoundException("Food", foodId));
  }

  private DailyFoodItemCreateDto applyPatchToDailyFoodItem(DailyFoodItem dailyFoodItem,
                                                           JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    DailyFoodItemCreateDto dailyFoodItemCreateDto = new
            DailyFoodItemCreateDto(dailyFoodItem.getAmount());

    return jsonPatchHelper.applyPatch(patch, dailyFoodItemCreateDto, DailyFoodItemCreateDto.class);
  }
}
