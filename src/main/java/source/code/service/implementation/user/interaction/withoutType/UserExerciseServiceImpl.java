package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.media.Media;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.ImageUrlPopulationService;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

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

    @Override
    public List<BaseUserEntity> getAllFromUser(int userId, Sort.Direction sortDirection) {
        Sort sort = Sort.by(sortDirection, "createdAt");

        List<UserExercise> userExercises = ((UserExerciseRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, sort);

        return userExercises.stream()
                .map(ue -> {
                    ExerciseSummaryDto dto = exerciseMapper.toSummaryDto(ue.getExercise());
                    dto.setUserExerciseInteractionCreatedAt(ue.getCreatedAt());

                    imagePopulationService.populateFirstImageFromMediaList(
                            List.of(dto),
                            d -> ue.getExercise().getMediaList(),
                            Media::getImageName,
                            ExerciseSummaryDto::setImageName,
                            ExerciseSummaryDto::setFirstImageUrl
                    );

                    return (BaseUserEntity) dto;
                })
                .toList();
    }

    @Override
    protected List<UserExercise> findAllByUser(int userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return ((UserExerciseRepository) userEntityRepository)
                .findAllByUserIdWithMedia(userId, sort);
    }

    @Override
    protected Exercise extractEntity(UserExercise userEntity) {
        return userEntity.getExercise();
    }

    @Override
    protected long countSaves(int entityId) {
        return ((UserExerciseRepository) userEntityRepository)
                .countByExerciseId(entityId);
    }

    @Override
    protected long countLikes(int entityId) {
        return 0;
    }
}
