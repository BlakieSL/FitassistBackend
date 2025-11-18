package source.code.service.implementation.user.interaction.withoutType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import source.code.dto.response.exercise.ExerciseResponseDto;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.exercise.ExerciseMapper;
import source.code.model.exercise.Exercise;
import source.code.model.user.User;
import source.code.model.user.UserExercise;
import source.code.repository.MediaRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.SavedServiceWithoutType;

import java.util.List;

@Service("userExerciseService")
public class UserExerciseServiceImpl
        extends GenericSavedServiceWithoutType<Exercise, UserExercise, ExerciseResponseDto>
        implements SavedServiceWithoutType {

    private final MediaRepository mediaRepository;
    private final AwsS3Service awsS3Service;

    public UserExerciseServiceImpl(UserRepository userRepository,
                                   JpaRepository<Exercise, Integer> entityRepository,
                                   JpaRepository<UserExercise, Integer> userEntityRepository,
                                   ExerciseMapper mapper,
                                   MediaRepository mediaRepository,
                                   AwsS3Service awsS3Service) {
        super(userRepository, entityRepository, userEntityRepository, mapper::toResponseDto, Exercise.class);
        this.mediaRepository = mediaRepository;
        this.awsS3Service = awsS3Service;
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
    public List<BaseUserEntity> getAllFromUser(int userId) {
        List<ExerciseSummaryDto> dtos = ((UserExerciseRepository) userEntityRepository)
                .findExerciseSummaryByUserId(userId);

        dtos.forEach(dto -> {
            if (dto.getImageName() != null) {
                dto.setFirstImageUrl(awsS3Service.getImage(dto.getImageName()));
            }
        });

        return dtos.stream()
                .map(dto -> (BaseUserEntity) dto)
                .toList();
    }

    @Override
    protected List<UserExercise> findAllByUser(int userId) {
        return ((UserExerciseRepository) userEntityRepository).findByUserId(userId);
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
