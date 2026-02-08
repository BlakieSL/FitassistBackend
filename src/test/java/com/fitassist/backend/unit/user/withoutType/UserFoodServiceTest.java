package com.fitassist.backend.unit.user.withoutType;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.NotUniqueRecordException;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.food.FoodMapper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.media.Media;
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

	@Mock
	private UserFoodRepository userFoodRepository;

	@Mock
	private FoodRepository foodRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FoodMapper foodMapper;

	@Mock
	private FoodPopulationService foodPopulationService;

	private UserFoodImplService userFoodService;

	private MockedStatic<AuthorizationUtil> mockedAuthUtil;

	@BeforeEach
	void setUp() {
		mockedAuthUtil = Mockito.mockStatic(AuthorizationUtil.class);
		userFoodService = new UserFoodImplService(userRepository, foodRepository, userFoodRepository, foodMapper,
				foodPopulationService);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthUtil != null) {
			mockedAuthUtil.close();
		}
	}

	@Test
	public void saveToUser_ShouldSaveToUserWithType() {
		int userId = 1;
		int foodId = 100;
		User user = new User();
		Food food = new Food();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

		userFoodService.saveToUser(foodId);

		verify(userFoodRepository).save(any(UserFood.class));
	}

	@Test
	public void saveToUser_ShouldThrowNotUniqueRecordExceptionIfAlreadySaved() {
		int userId = 1;
		int foodId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId)).thenReturn(true);

		assertThrows(NotUniqueRecordException.class, () -> userFoodService.saveToUser(foodId));

		verify(userFoodRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfUserNotFound() {
		int userId = 1;
		int foodId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userFoodService.saveToUser(foodId));

		verify(userFoodRepository, never()).save(any());
	}

	@Test
	public void saveToUser_ShouldThrowRecordNotFoundExceptionIfFoodNotFound() {
		int userId = 1;
		int foodId = 100;
		User user = new User();

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userFoodRepository.existsByUserIdAndFoodId(userId, foodId)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(foodRepository.findById(foodId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userFoodService.saveToUser(foodId));

		verify(userFoodRepository, never()).save(any());
	}

	@Test
	public void deleteFromUser_ShouldDeleteFromUser() {
		int userId = 1;
		int foodId = 100;
		UserFood userFood = UserFood.of(new User(), new Food());

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userFoodRepository.findByUserIdAndFoodId(userId, foodId)).thenReturn(Optional.of(userFood));

		userFoodService.deleteFromUser(foodId);

		verify(userFoodRepository).delete(userFood);
	}

	@Test
	public void deleteFromUser_ShouldThrowRecordNotFoundExceptionIfUserFoodNotFound() {
		int userId = 1;
		int foodId = 100;

		mockedAuthUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(userFoodRepository.findByUserIdAndFoodId(userId, foodId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> userFoodService.deleteFromUser(foodId));

		verify(userFoodRepository, never()).delete(any());
	}

	@Test
	public void getAllFromUser_ShouldReturnPagedFoods() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

		Food food1 = new Food();
		food1.setId(1);
		food1.setMediaList(new ArrayList<>());
		Media media1 = new Media();
		media1.setImageName("food1.jpg");
		food1.getMediaList().add(media1);

		Food food2 = new Food();
		food2.setId(2);
		food2.setMediaList(new ArrayList<>());
		Media media2 = new Media();
		media2.setImageName("food2.jpg");
		food2.getMediaList().add(media2);

		UserFood uf1 = UserFood.of(new User(), food1);
		UserFood uf2 = UserFood.of(new User(), food2);

		FoodSummaryDto dto1 = new FoodSummaryDto();
		dto1.setId(1);
		dto1.setImageName("food1.jpg");
		FoodSummaryDto dto2 = new FoodSummaryDto();
		dto2.setId(2);
		dto2.setImageName("food2.jpg");

		Page<UserFood> userFoodPage = new PageImpl<>(List.of(uf1, uf2), pageable, 2);

		when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), eq(pageable))).thenReturn(userFoodPage);
		when(foodMapper.toSummary(food1)).thenReturn(dto1);
		when(foodMapper.toSummary(food2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userFoodService.getAllFromUser(userId, pageable);

		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		verify(foodMapper, times(2)).toSummary(any(Food.class));
	}

	@Test
	public void getAllFromUser_ShouldReturnEmptyPageIfNoFoods() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserFood> emptyPage = new PageImpl<>(List.of(), pageable, 0);

		when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), eq(pageable))).thenReturn(emptyPage);

		Page<UserEntitySummaryResponseDto> result = userFoodService.getAllFromUser(userId, pageable);

		assertTrue(result.isEmpty());
		assertEquals(0, result.getTotalElements());
	}

	@Test
	public void getAllFromUser_ShouldPopulateImageUrls() {
		int userId = 1;
		Pageable pageable = PageRequest.of(0, 10);

		Food food1 = new Food();
		food1.setId(1);
		food1.setMediaList(new ArrayList<>());
		Media media1 = new Media();
		media1.setImageName("image1.jpg");
		food1.getMediaList().add(media1);

		Food food2 = new Food();
		food2.setId(2);
		food2.setMediaList(new ArrayList<>());
		Media media2 = new Media();
		media2.setImageName("image2.jpg");
		food2.getMediaList().add(media2);

		UserFood uf1 = UserFood.of(new User(), food1);
		UserFood uf2 = UserFood.of(new User(), food2);

		FoodSummaryDto dto1 = new FoodSummaryDto();
		dto1.setId(1);
		dto1.setImageName("image1.jpg");
		FoodSummaryDto dto2 = new FoodSummaryDto();
		dto2.setId(2);
		dto2.setImageName("image2.jpg");

		Page<UserFood> userFoodPage = new PageImpl<>(List.of(uf1, uf2), pageable, 2);

		when(userFoodRepository.findAllByUserIdWithMedia(eq(userId), eq(pageable))).thenReturn(userFoodPage);
		when(foodMapper.toSummary(food1)).thenReturn(dto1);
		when(foodMapper.toSummary(food2)).thenReturn(dto2);

		Page<UserEntitySummaryResponseDto> result = userFoodService.getAllFromUser(userId, pageable);

		assertNotNull(result);
		assertEquals(2, result.getContent().size());
		verify(foodMapper).toSummary(food1);
		verify(foodMapper).toSummary(food2);
	}

}
