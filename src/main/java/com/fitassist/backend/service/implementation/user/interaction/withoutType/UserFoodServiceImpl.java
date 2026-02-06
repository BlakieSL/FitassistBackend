package com.fitassist.backend.service.implementation.user.interaction.withoutType;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.response.food.FoodSummaryDto;
import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.food.FoodMapper;
import com.fitassist.backend.model.food.Food;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserFood;
import com.fitassist.backend.repository.UserFoodRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.food.FoodPopulationService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userFoodService")
public class UserFoodServiceImpl extends GenericSavedServiceWithoutType<Food, UserFood, FoodSummaryDto>
		implements SavedServiceWithoutType {

	private final FoodMapper foodMapper;

	private final FoodPopulationService foodPopulationService;

	public UserFoodServiceImpl(UserRepository userRepository, JpaRepository<Food, Integer> entityRepository,
			JpaRepository<UserFood, Integer> userEntityRepository, FoodMapper mapper,
			FoodPopulationService foodPopulationService) {
		super(userRepository, entityRepository, userEntityRepository, mapper::toSummary, Food.class);
		this.foodMapper = mapper;
		this.foodPopulationService = foodPopulationService;
	}

	@Override
	@CacheEvict(value = CacheNames.FOODS, key = "#entityId")
	public InteractionResponseDto saveToUser(int entityId) {
		return super.saveToUser(entityId);
	}

	@Override
	@CacheEvict(value = CacheNames.FOODS, key = "#entityId")
	public InteractionResponseDto deleteFromUser(int entityId) {
		return super.deleteFromUser(entityId);
	}

	@Override
	protected long countByEntityId(int entityId) {
		return ((UserFoodRepository) userEntityRepository).countByFoodId(entityId);
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, Pageable pageable) {
		Page<UserFood> userFoodPage = ((UserFoodRepository) userEntityRepository).findAllByUserIdWithMedia(userId,
				pageable);

		List<FoodSummaryDto> summaries = userFoodPage.getContent().stream().map(uf -> {
			FoodSummaryDto dto = foodMapper.toSummary(uf.getFood());
			dto.setInteractionCreatedAt(uf.getCreatedAt());
			return dto;
		}).toList();

		foodPopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userFoodPage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int entityId) {
		return ((UserFoodRepository) userEntityRepository).existsByUserIdAndFoodId(userId, entityId);
	}

	@Override
	protected UserFood createUserEntity(User user, Food entity) {
		return UserFood.of(user, entity);
	}

	@Override
	protected UserFood findUserEntity(int userId, int entityId) {
		return ((UserFoodRepository) userEntityRepository).findByUserIdAndFoodId(userId, entityId)
			.orElseThrow(() -> RecordNotFoundException.of(UserFood.class, userId, entityId));
	}

}
