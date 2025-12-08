package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

@Service("userExerciseService")
public class UserExerciseServiceImpl
        extends GenericSavedServiceWithoutType<Exercise, UserExercise, ExerciseSummaryDto>
        implements SavedServiceWithoutType {

    private final ExerciseMapper exerciseMapper;
    private final ImageUrlPopulationService imagePopulationService;

    public UserExerciseServiceImpl(UserRepository userRepository,
                                   JpaRepository<Exercise, Integer> entityRepository,
                                   JpaRepository<UserExercise, Integer> userEntityRepository,
                                   ExerciseMapper mapper,
                                   ImageUrlPopulationService imagePopulationService) {
        super(userRepository, entityRepository, userEntityRepository, mapper::toSummaryDto, Exercise.class);
        this.exerciseMapper = mapper;
        this.imagePopulationService = imagePopulationService;
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
        return ((UserExerciseRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, pageable)
                .map(ue -> {
                    ExerciseSummaryDto dto = exerciseMapper.toSummaryDto(ue.getExercise());
                    dto.setUserExerciseInteractionCreatedAt(ue.getCreatedAt());
                    imagePopulationService.populateFirstImageFromMediaList(dto, ue.getExercise().getMediaList(),
                            Media::getImageName, ExerciseSummaryDto::setImageName, ExerciseSummaryDto::setFirstImageUrl);
                    return dto;
                });
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId) {
        return ((UserExerciseRepository) userEntityRepository)
                .existsByUserIdAndExerciseId(userId, entityId);
    }

    @Override
    protected UserExercise createUserEntity(User user, Exercise entity) {
        return UserExercise.of(user, entity);
    }

    @Override
    protected UserExercise findUserEntity(int userId, int entityId) {
        return ((UserExerciseRepository) userEntityRepository)
                .findByUserIdAndExerciseId(userId, entityId)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserExercise.class,
                        userId,
                        entityId
                ));
    }
}
