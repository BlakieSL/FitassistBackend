package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.exercise.ExercisePopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userExerciseService")
public class UserExerciseServiceImpl extends GenericSavedServiceWithoutType<Exercise, UserExercise, ExerciseSummaryDto>
		implements SavedServiceWithoutType {

	private final ExerciseMapper exerciseMapper;

	private final ExercisePopulationService exercisePopulationService;

	public UserExerciseServiceImpl(UserRepository userRepository, JpaRepository<Exercise, Integer> entityRepository,
			JpaRepository<UserExercise, Integer> userEntityRepository, ExerciseMapper mapper,
			ExercisePopulationService exercisePopulationService) {
		super(userRepository, entityRepository, userEntityRepository, mapper::toSummaryDto, Exercise.class);
		this.exerciseMapper = mapper;
		this.exercisePopulationService = exercisePopulationService;
	}

	@Override
	@CacheEvict(value = CacheNames.EXERCISES, key = "#entityId")
	public void saveToUser(int entityId) {
		super.saveToUser(entityId);
	}

	@Override
	@CacheEvict(value = CacheNames.EXERCISES, key = "#entityId")
	public void deleteFromUser(int entityId) {
		super.deleteFromUser(entityId);
	}

	@Override
	public Page<BaseUserEntity> getAllFromUser(int userId, Pageable pageable) {
		Page<UserExercise> userExercisePage = ((UserExerciseRepository) userEntityRepository)
			.findAllByUserIdWithMedia(userId, pageable);

		List<ExerciseSummaryDto> summaries = userExercisePage.getContent().stream().map(ue -> {
			ExerciseSummaryDto dto = exerciseMapper.toSummaryDto(ue.getExercise());
			dto.setInteractionCreatedAt(ue.getCreatedAt());
			return dto;
		}).toList();

		exercisePopulationService.populate(summaries);

		return new PageImpl<>(summaries.stream().map(dto -> (BaseUserEntity) dto).toList(), pageable,
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
