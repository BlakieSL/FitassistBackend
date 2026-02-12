package com.fitassist.backend.unit.daily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.FoodMacros;
import com.fitassist.backend.dto.request.food.DailyCartFoodCreateDto;
import com.fitassist.backend.dto.request.food.DailyCartFoodUpdateDto;
import com.fitassist.backend.dto.response.daily.DailyFoodsResponseDto;
import com.fitassist.backend.dto.response.food.FoodCalculatedMacrosResponseDto;
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
import com.fitassist.backend.service.declaration.helpers.CalculationsService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.daily.DailyFoodServiceImpl;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyFoodServiceTest {

	private static final int USER_ID = 1;

	private static final int FOOD_ID = 1;

	private static final int INVALID_FOOD_ID = 999;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private ValidationService validationService;

	@Mock
	private DailyFoodMapper dailyFoodMapper;

	@Mock
	private RepositoryHelper repositoryHelper;

	@Mock
	private DailyCartRepository dailyCartRepository;

	@Mock
	private DailyCartFoodRepository dailyCartFoodRepository;

	@Mock
	private FoodRepository foodRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CalculationsService calculationsService;

	@InjectMocks
	private DailyFoodServiceImpl dailyFoodService;

	private Food food;

	private DailyCart dailyCart;

	private DailyCartFood dailyCartFood;

	private DailyCartFoodCreateDto createDto;

	private JsonMergePatch patch;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	@BeforeEach
	void setUp() {
		food = new Food();
		dailyCart = new DailyCart();
		dailyCartFood = new DailyCartFood();
		dailyCartFood.setQuantity(BigDecimal.valueOf(0));
		createDto = new DailyCartFoodCreateDto();
		createDto.setQuantity(BigDecimal.valueOf(10));
		patch = mock(JsonMergePatch.class);
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		food.setId(1);
		dailyCart.setId(1);
		dailyCartFood.setId(1);
		createDto.setQuantity(BigDecimal.valueOf(100));
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void addFoodToDailyCart_shouldUpdateExistingDailyFoodToDailyCartAmount() {
		when(dailyCartRepository.findByUserIdAndDate(anyInt(), any())).thenReturn(Optional.of(dailyCart));
		when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
		when(dailyCartFoodRepository.findByDailyCartIdAndFoodId(dailyCart.getId(), FOOD_ID))
			.thenReturn(Optional.of(dailyCartFood));

		dailyFoodService.addFoodToDailyCart(FOOD_ID, createDto);

		verify(dailyCartRepository, times(1)).save(any(DailyCart.class));
	}

	@Test
	void addFoodToDailyCart_shouldAddNewDailyFoodToDailyCart_whenNotFound() {
		when(dailyCartRepository.findByUserIdAndDate(anyInt(), any())).thenReturn(Optional.empty());
		when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
		when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(new User());
		when(dailyCartRepository.save(any())).thenReturn(dailyCart);
		when(dailyCartFoodRepository.findByDailyCartIdAndFoodId(dailyCart.getId(), FOOD_ID))
			.thenReturn(Optional.of(dailyCartFood));

		dailyFoodService.addFoodToDailyCart(FOOD_ID, createDto);

		verify(dailyCartRepository, times(2)).save(any(DailyCart.class));
	}

	@Test
	void addFoodToDailyCart_shouldCreateNewDailyCart_whenNotFound() {
		User user = new User();
		user.setId(USER_ID);
		DailyCart newDailyCart = DailyCart.createDate(user);
		newDailyCart.setId(1);
		newDailyCart.setUser(user);

		when(dailyCartRepository.findByUserIdAndDate(eq(USER_ID), any())).thenReturn(Optional.empty());
		when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);
		when(dailyCartRepository.save(any(DailyCart.class))).thenReturn(newDailyCart);
		when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
		when(dailyCartFoodRepository.findByDailyCartIdAndFoodId(anyInt(), eq(FOOD_ID))).thenReturn(Optional.empty());

		dailyFoodService.addFoodToDailyCart(FOOD_ID, createDto);

		ArgumentCaptor<DailyCart> dailyFoodCaptor = ArgumentCaptor.forClass(DailyCart.class);
		verify(dailyCartRepository, times(2)).save(dailyFoodCaptor.capture());
		DailyCart savedDailyFood = dailyFoodCaptor.getValue();
		assertEquals(USER_ID, savedDailyFood.getUser().getId());
		assertEquals(createDto.getQuantity(), savedDailyFood.getDailyCartFoods().getFirst().getQuantity());
	}

	@Test
	void removeFoodFromDailyCart_shouldRemoveExistingDailyFoodFromDailyCart() {
		when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID)).thenReturn(Optional.of(dailyCartFood));
		doNothing().when(dailyCartFoodRepository).delete(dailyCartFood);

		dailyFoodService.removeFoodFromDailyCart(FOOD_ID);

		verify(dailyCartFoodRepository).delete(dailyCartFood);
	}

	@Test
	void removeFoodFromDailyCart_shouldThrowException_whenDailyFoodFromDailyCartNotFound() {
		when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> dailyFoodService.removeFoodFromDailyCart(FOOD_ID));
	}

	@Test
	void updateDailyFoodItem_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		DailyCartFoodUpdateDto patchedDto = new DailyCartFoodUpdateDto();
		patchedDto.setQuantity(BigDecimal.valueOf(120));

		when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID)).thenReturn(Optional.of(dailyCartFood));

		doReturn(patchedDto).when(jsonPatchService)
			.createFromPatch(any(JsonMergePatch.class), eq(DailyCartFoodUpdateDto.class));

		dailyFoodService.updateDailyFoodItem(FOOD_ID, patch);

		verify(validationService).validate(patchedDto);
		assertEquals(patchedDto.getQuantity(), dailyCartFood.getQuantity());
		verify(dailyCartFoodRepository).save(dailyCartFood);
	}

	@Test
	void updateDailyFoodItem_shouldThrowException_whenDailyFoodItemNotFound() {
		when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> dailyFoodService.updateDailyFoodItem(FOOD_ID, patch));

		verifyNoInteractions(validationService);
		verify(dailyCartFoodRepository, never()).save(any());
	}

	@Test
	void updateDailyFoodItem_shouldThrowException_whenPatchFails() throws JsonPatchException, JsonProcessingException {
		when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID)).thenReturn(Optional.of(dailyCartFood));
		doThrow(JsonPatchException.class).when(jsonPatchService)
			.createFromPatch(any(JsonMergePatch.class), eq(DailyCartFoodUpdateDto.class));

		assertThrows(JsonPatchException.class, () -> dailyFoodService.updateDailyFoodItem(FOOD_ID, patch));

		verifyNoInteractions(validationService);
		verify(dailyCartFoodRepository, never()).save(any());
	}

	@Test
	void updateDailyFoodItem_shouldThrowException_whenPatchValidationFails()
			throws JsonPatchException, JsonProcessingException {
		DailyCartFoodUpdateDto patchedDto = new DailyCartFoodUpdateDto();
		patchedDto.setQuantity(BigDecimal.valueOf(120));

		when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID)).thenReturn(Optional.of(dailyCartFood));
		doReturn(patchedDto).when(jsonPatchService)
			.createFromPatch(any(JsonMergePatch.class), eq(DailyCartFoodUpdateDto.class));

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> dailyFoodService.updateDailyFoodItem(FOOD_ID, patch));

		verify(validationService).validate(patchedDto);
		verify(dailyCartFoodRepository, never()).save(any());
	}

	@Test
	void getFoodsFromDailyCart_shouldReturnFoods() {
		User user = new User();
		user.setId(USER_ID);
		user.setWeight(BigDecimal.valueOf(70));
		dailyCart.setUser(user);
		dailyCart.getDailyCartFoods().add(dailyCartFood);

		FoodCalculatedMacrosResponseDto calculatedResponseDto = new FoodCalculatedMacrosResponseDto();
		FoodMacros foodMacros = FoodMacros.of(BigDecimal.valueOf(100), BigDecimal.valueOf(10), BigDecimal.valueOf(5),
				BigDecimal.valueOf(20));
		calculatedResponseDto.setFoodMacros(foodMacros);

		when(dailyCartRepository.findByUserIdAndDateWithFoodAssociations(USER_ID, LocalDate.now()))
			.thenReturn(Optional.of(dailyCart));
		when(calculationsService.toCalculatedResponseDto(dailyCartFood)).thenReturn(calculatedResponseDto);

		DailyFoodsResponseDto result = dailyFoodService.getFoodFromDailyCart(LocalDate.now());

		assertEquals(1, result.getFoods().size());
		assertEquals(BigDecimal.valueOf(100.0).setScale(1), result.getTotalCalories());
		assertEquals(BigDecimal.valueOf(20.0).setScale(2), result.getTotalCarbohydrates());
		assertEquals(BigDecimal.valueOf(10.0).setScale(2), result.getTotalProtein());
		assertEquals(BigDecimal.valueOf(5.0).setScale(2), result.getTotalFat());
	}

	@Test
	void getFoodsFromDailyCart_shouldReturnEmptyFoods_whenDailyFoodNotFound() {
		User user = new User();
		user.setId(USER_ID);
		user.setWeight(BigDecimal.valueOf(70));
		DailyCart newDailyCart = DailyCart.createDate(user);
		newDailyCart.setUser(user);

		when(dailyCartRepository.findByUserIdAndDateWithFoodAssociations(USER_ID, LocalDate.now()))
			.thenReturn(Optional.empty());

		DailyFoodsResponseDto result = dailyFoodService.getFoodFromDailyCart(LocalDate.now());

		assertTrue(result.getFoods().isEmpty());
	}

}
