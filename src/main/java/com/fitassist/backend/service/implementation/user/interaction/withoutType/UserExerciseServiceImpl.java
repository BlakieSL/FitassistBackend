package com.fitassist.backend.service.implementation.user.interaction.withoutType;

import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.response.exercise.ExerciseSummaryDto;
import com.fitassist.backend.dto.response.user.InteractionResponseDto;
import com.fitassist.backend.dto.response.user.UserEntitySummaryResponseDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.exercise.ExerciseMapper;
import com.fitassist.backend.model.exercise.Exercise;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.model.user.interactions.UserExercise;
import com.fitassist.backend.repository.UserExerciseRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.exercise.ExercisePopulationService;
import com.fitassist.backend.service.declaration.user.SavedServiceWithoutType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userExerciseService")
public class UserExerciseServiceImpl extends GenericSavedServiceWithoutType<Exercise, UserExercise, ExerciseSummaryDto>
		implements SavedServiceWithoutType {

	private final ExerciseMapper exerciseMapper;

	private final ExercisePopulationService exercisePopulationService;

	public UserExerciseServiceImpl(UserRepository userRepository, JpaRepository<Exercise, Integer> entityRepository,
			JpaRepository<UserExercise, Integer> userEntityRepository, ExerciseMapper mapper,
			ExercisePopulationService exercisePopulationService) {
		super(userRepository, entityRepository, userEntityRepository, mapper::toSummary, Exercise.class);
		this.exerciseMapper = mapper;
		this.exercisePopulationService = exercisePopulationService;
	}

	@Override
	@CacheEvict(value = CacheNames.EXERCISES, key = "#entityId")
	public InteractionResponseDto saveToUser(int entityId) {
		return super.saveToUser(entityId);
	}

	@Override
	@CacheEvict(value = CacheNames.EXERCISES, key = "#entityId")
	public InteractionResponseDto deleteFromUser(int entityId) {
		return super.deleteFromUser(entityId);
	}

	@Override
	protected long countByEntityId(int entityId) {
		return ((UserExerciseRepository) userEntityRepository).countByExerciseId(entityId);
	}

	@Override
	public Page<UserEntitySummaryResponseDto> getAllFromUser(int userId, Pageable pageable) {
		Page<UserExercise> userExercisePage = ((UserExerciseRepository) userEntityRepository)
			.findAllByUserIdWithMedia(userId, pageable);

		List<ExerciseSummaryDto> summaries = userExercisePage.getContent().stream().map(ue -> {
			ExerciseSummaryDto dto = exerciseMapper.toSummary(ue.getExercise());
			dto.setInteractionCreatedAt(ue.getCreatedAt());
			return dto;
		}).toList();

		exercisePopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (UserEntitySummaryResponseDto) dto).toList(), pageable,
				userExercisePage.getTotalElements());
	}

	@Override
	protected boolean isAlreadySaved(int userId, int entityId) {
		return ((UserExerciseRepository) userEntityRepository).existsByUserIdAndExerciseId(userId, entityId);
	}

	@Override
	protected UserExercise createUserEntity(User user, Exercise entity) {
		return UserExercise.of(user, entity);
	}

	@Override
	protected UserExercise findUserEntity(int userId, int entityId) {
		return ((UserExerciseRepository) userEntityRepository).findByUserIdAndExerciseId(userId, entityId)
			.orElseThrow(() -> RecordNotFoundException.of(UserExercise.class, userId, entityId));
	}

}
