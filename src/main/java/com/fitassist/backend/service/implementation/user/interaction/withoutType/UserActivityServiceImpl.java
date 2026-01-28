package com.fitassist.backend.service.implementation.user.interaction.withoutType;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.response.activity.ActivitySummaryDto;
import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.ActivityMapper;
import com.fitassist.backend.model.activity.Activity;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.UserActivity;
import com.fitassist.backend.repository.UserActivityRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.activity.ActivityPopulationService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userActivityService")
public class UserActivityServiceImpl extends GenericSavedServiceWithoutType<Activity, UserActivity, ActivitySummaryDto>
		implements SavedServiceWithoutType {

	private final ActivityMapper activityMapper;

	private final ActivityPopulationService activityPopulationService;

	public UserActivityServiceImpl(UserRepository userRepository, JpaRepository<Activity, Integer> entityRepository,
			JpaRepository<UserActivity, Integer> userEntityRepository, ActivityMapper mapper,
			ActivityPopulationService activityPopulationService) {
		super(userRepository, entityRepository, userEntityRepository, mapper::toSummaryDto, Activity.class);
		this.activityMapper = mapper;
		this.activityPopulationService = activityPopulationService;
	}

	@Override
	@CacheEvict(value = CacheNames.ACTIVITIES, key = "#entityId")
	public InteractionResponseDto saveToUser(int entityId) {
		return super.saveToUser(entityId);
	}

	@Override
	@CacheEvict(value = CacheNames.ACTIVITIES, key = "#entityId")
	public InteractionResponseDto deleteFromUser(int entityId) {
		return super.deleteFromUser(entityId);
	}

	@Override
	protected long countByEntityId(int entityId) {
		return ((UserActivityRepository) userEntityRepository).countByActivityId(entityId);
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, Pageable pageable) {
		Page<UserActivity> userActivityPage = ((UserActivityRepository) userEntityRepository)
			.findAllByUserIdWithMedia(userId, pageable);

		List<ActivitySummaryDto> summaries = userActivityPage.getContent().stream().map(ua -> {
			ActivitySummaryDto dto = activityMapper.toSummaryDto(ua.getActivity());
			dto.setInteractionCreatedAt(ua.getCreatedAt());
			return dto;
		}).toList();

		activityPopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userActivityPage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int entityId) {
		return ((UserActivityRepository) userEntityRepository).existsByUserIdAndActivityId(userId, entityId);
	}

	@Override
	protected UserActivity createUserEntity(User user, Activity entity) {
		return UserActivity.of(user, entity);
	}

	@Override
	protected UserActivity findUserEntity(int userId, int entityId) {
		return ((UserActivityRepository) userEntityRepository).findByUserIdAndActivityId(userId, entityId)
			.orElseThrow(() -> RecordNotFoundException.of(UserActivity.class, userId, entityId));
	}

}
