package com.fitassist.backend.service.implementation.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.request.food.DailyCartFoodCreateDto;
import com.fitassist.backend.dto.request.food.DailyCartFoodUpdateDto;
import com.fitassist.backend.dto.response.daily.DailyFoodsResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.daily.DailyFoodMapper;
import com.fitassist.backend.model.daily.DailyCart;
import com.fitassist.backend.model.daily.DailyCartFood;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.DailyCartFoodRepository;
import com.fitassist.backend.repository.DailyCartRepository;
import com.fitassist.backend.repository.FoodRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.daily.DailyFoodService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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

	public DailyFoodServiceImpl(ValidationService validationService, RepositoryHelper repositoryHelper,
			DailyCartRepository dailyCartRepository, FoodRepository foodRepository, UserRepository userRepository,
			JsonPatchService jsonPatchService, DailyFoodMapper dailyFoodMapper,
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
	public DailyFoodsResponseDto getFoodFromDailyCart(LocalDate date) {
		int userId = AuthorizationUtil.getUserId();

		return dailyCartRepository.findByUserIdAndDateWithFoodAssociations(userId, date)
			.map(dailyCart -> DailyFoodsResponseDto.create(dailyCart.getDailyCartFoods()
				.stream()
				.map(dailyFoodMapper::toFoodCalculatedMacrosResponseDto)
				.toList()))
			.orElse(DailyFoodsResponseDto.of(Collections.emptyList()));
	}

	private void updateOrAddDailyFoodItem(DailyCart dailyCart, Food food, BigDecimal quantity) {
		dailyCartFoodRepository.findByDailyCartIdAndFoodId(dailyCart.getId(), food.getId())
			.ifPresentOrElse(foundItem -> {
				BigDecimal newQuantity = foundItem.getQuantity().add(quantity);
				foundItem.setQuantity(newQuantity);
			}, () -> {
				DailyCartFood newItem = DailyCartFood.of(food, dailyCart, quantity);
				dailyCart.getDailyCartFoods().add(newItem);
			});
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
		return dailyCartRepository.findByUserIdAndDate(userId, date).orElseGet(() -> createDailyCart(userId, date));
	}

	private DailyCartFood findWithoutAssociations(int dailyCartFoodId) {
		return dailyCartFoodRepository.findByIdWithoutAssociations(dailyCartFoodId)
			.orElseThrow(() -> new RecordNotFoundException(DailyCartFood.class, dailyCartFoodId));
	}

}
