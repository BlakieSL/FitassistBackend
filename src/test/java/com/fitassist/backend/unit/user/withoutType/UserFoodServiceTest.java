package com.fitassist.backend.unit.user.withoutType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.food.FoodMapper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.UserFood;
import com.fitassist.backend.repository.FoodRepository;
import com.fitassist.backend.repository.UserFoodRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.food.FoodPopulationService;
import com.fitassist.backend.service.implementation.user.interaction.withoutType.UserFoodImplService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFoodServiceTest {

	private static final int USER_ID = 1;

	private static final int FOOD_ID = 100;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FoodRepository foodRepository;

	@Mock
	private UserFoodRepository userFoodRepository;

	@Mock
	private FoodMapper foodMapper;

	@Mock
	private FoodPopulationService foodPopulationService;

	private UserFoodImplService userFoodService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	private User user;

	private Food food;

	private UserFood userFood;

	@BeforeEach
	void setUp() {
		userFoodService = new UserFoodImplService(userRepository, foodRepository, userFoodRepository, foodMapper,
				foodPopulationService);
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);

		user = new User();
		user.setId(USER_ID);
		food = new Food();
		food.setId(FOOD_ID);
		food.setMediaList(new ArrayList<>());
		userFood = UserFood.of(user, food);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		when(userFoodRepository.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(foodRepository.findById(FOOD_ID)).thenReturn(Optional.of(food));

		userFoodService.saveToUser(FOOD_ID);

		verify(userFoodRepository).save(any(UserFood.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		when(userFoodRepository.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userFoodService.saveToUser(FOOD_ID));

		verify(userFoodRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		when(userFoodRepository.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userFoodService.saveToUser(FOOD_ID));

		verify(userFoodRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfFoodNotFound() {
		when(userFoodRepository.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(false);
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
		when(foodRepository.findById(FOOD_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userFoodService.saveToUser(FOOD_ID));

		verify(userFoodRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		when(userFoodRepository.findByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(Optional.of(userFood));

		userFoodService.deleteFromUser(FOOD_ID);

		verify(userFoodRepository).delete(userFood);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserFoodNotFound() {
		when(userFoodRepository.findByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userFoodService.deleteFromUser(FOOD_ID));

		verify(userFoodRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnPagedFoods() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Food food2 = new Food();
		food2.setId(2);
		food2.setMediaList(new ArrayList<>());
		UserFood uf2 = UserFood.of(user, food2);

		FoodSummaryDto dto1 = new FoodSummaryDto();
		dto1.setId(FOOD_ID);
		FoodSummaryDto dto2 = new FoodSummaryDto();
		dto2.setId(2);

		Page<UserFood> userFoodPage = new PageImpl<>(List.of(userFood, uf2), pageable, 2);

		when(userFoodRepository.findAllByUserIdWithMedia(eq(USER_ID), eq(pageable))).thenReturn(userFoodPage);
		when(foodMapper.toSummary(food)).thenReturn(dto1);
		when(foodMapper.toSummary(food2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userFoodService.getAllFromUser(USER_ID, pageable);

		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		verify(foodMapper, times(2)).toSummary(any(Food.class));
		verify(foodPopulationService).populate(anyList());
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyPageIfNoFoods() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserFood> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userFoodRepository.findAllByUserIdWithMedia(eq(USER_ID), eq(pageable))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userFoodService.getAllFromUser(USER_ID, pageable);

		assertTrue(result.isEmpty());
		assertEquals(0, result.getTotalElements());
	}

}
