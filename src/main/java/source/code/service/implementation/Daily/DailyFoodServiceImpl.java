package source.code.service.implementation.Daily;

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
import source.code.service.implementation.Helpers.JsonPatchServiceImpl;
import source.code.service.implementation.Helpers.ValidationServiceImpl;
import source.code.mapper.Food.DailyFoodMapper;
import source.code.model.Food.DailyFood;
import source.code.model.Food.DailyFoodItem;
import source.code.model.Food.Food;
import source.code.model.User.User;
import source.code.repository.DailyFoodItemRepository;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.Daily.DailyFoodService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyFoodServiceImpl implements DailyFoodService {
  private final ValidationServiceImpl validationServiceImpl;
  private final JsonPatchServiceImpl jsonPatchServiceImpl;
  private final DailyFoodMapper dailyFoodMapper;
  private final DailyFoodRepository dailyFoodRepository;
  private final DailyFoodItemRepository dailyFoodItemRepository;
  private final FoodRepository foodRepository;
  private final UserRepository userRepository;

  public DailyFoodServiceImpl(
          ValidationServiceImpl validationServiceImpl,
          DailyFoodRepository dailyFoodRepository,
          FoodRepository foodRepository,
          UserRepository userRepository,
          JsonPatchServiceImpl jsonPatchServiceImpl,
          DailyFoodMapper dailyFoodMapper,
          DailyFoodItemRepository dailyFoodItemRepository) {
    this.validationServiceImpl = validationServiceImpl;
    this.dailyFoodRepository = dailyFoodRepository;
    this.foodRepository = foodRepository;
    this.userRepository = userRepository;
    this.jsonPatchServiceImpl = jsonPatchServiceImpl;
    this.dailyFoodMapper = dailyFoodMapper;
    this.dailyFoodItemRepository = dailyFoodItemRepository;
  }

  @Scheduled(cron = "0 0 0 * * ?", zone = "GMT+2")
  @Transactional
  public void resetDailyCarts() {
    List<DailyFood> carts = dailyFoodRepository.findAll();

    for (DailyFood cart : carts) {
      resetDailyFood(cart);
      dailyFoodRepository.save(cart);
    }
  }

  @Transactional
  public void addFoodToDailyFoodItem(int userId, int foodId, DailyFoodItemCreateDto dto) {
    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
    Food food = getFood(foodId);

    DailyFoodItem dailyFoodItem = getOrCreateDailyFoodItem(dailyFood, food, dto.getAmount());
    updateOrAddDailyFoodItem(dailyFood, dailyFoodItem);

    dailyFoodRepository.save(dailyFood);
  }

  @Transactional
  public void removeFoodFromDailyFoodItem(int userId, int foodId) {
    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
    DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

    dailyFood.getDailyFoodItems().remove(dailyFoodItem);
    dailyFoodRepository.save(dailyFood);
  }

  @Transactional
  public void updateDailyFoodItem(int userId, int foodId, JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    DailyFood dailyFood = getOrCreateDailyFoodForUser(userId);
    DailyFoodItem dailyFoodItem = getDailyFoodItem(dailyFood.getId(), foodId);

    DailyFoodItemCreateDto patchedDto = applyPatchToDailyFoodItem(dailyFoodItem, patch);
    validationServiceImpl.validate(patchedDto);

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

  private DailyFoodItem getOrCreateDailyFoodItem(DailyFood dailyFood, Food food, int amount) {
    return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), food.getId())
            .orElse(DailyFoodItem.createWithAmountFoodDailyFood(amount, food, dailyFood));
  }

  private void updateOrAddDailyFoodItem(DailyFood dailyFood, DailyFoodItem dailyFoodItem) {
    if(existByDailyFoodAndItem(dailyFoodItem, dailyFood)) {
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

    return dailyFoodRepository.save(DailyFood.createForToday(user));
  }

  private DailyFoodItemCreateDto applyPatchToDailyFoodItem(DailyFoodItem dailyFoodItem,
                                                           JsonMergePatch patch)
          throws JsonPatchException, JsonProcessingException {

    DailyFoodItemCreateDto createDto = new DailyFoodItemCreateDto(dailyFoodItem.getAmount());
    return jsonPatchServiceImpl.applyPatch(patch, createDto, DailyFoodItemCreateDto.class);
  }

  private DailyFoodItem getDailyFoodItem(int dailyFoodId, int foodId) {
    return dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFoodId, foodId)
            .orElseThrow(() -> new RecordNotFoundException("DailyFoodItem", foodId));
  }

  private Food getFood(int foodId) {
    return foodRepository.findById(foodId)
            .orElseThrow(() -> new RecordNotFoundException("Food", foodId));
  }

  private boolean existByDailyFoodAndItem(DailyFoodItem dailyFoodItem, DailyFood dailyFood) {
    return dailyFoodItemRepository
            .existsByIdAndDailyFoodId(dailyFoodItem.getId(), dailyFood.getId());
  }

  private void resetDailyFood(DailyFood dailyFood) {
    dailyFood.setDate(LocalDate.now());
    dailyFood.getDailyFoodItems().clear();
  }
}
