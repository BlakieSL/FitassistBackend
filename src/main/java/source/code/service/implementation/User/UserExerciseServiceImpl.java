package source.code.service.implementation.User;

import org.springframework.stereotype.Service;
import source.code.dto.response.ExerciseResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.Exercise.ExerciseMapper;
import source.code.model.Exercise.Exercise;
import source.code.model.User.User;
import source.code.model.User.UserExercise;
import source.code.repository.ExerciseRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.User.SavedService;

import java.util.List;

@Service("userExerciseService")
public class UserExerciseServiceImpl
        extends GenericSavedService<Exercise, UserExercise, ExerciseResponseDto>
        implements SavedService {

  public UserExerciseServiceImpl(UserExerciseRepository userExerciseRepository,
                                 ExerciseRepository exerciseRepository,
                                 UserRepository userRepository,
                                 ExerciseMapper exerciseMapper) {
    super(userRepository,
            exerciseRepository,
            userExerciseRepository,
            exerciseMapper::toResponseDto,
            Exercise.class);
  }

  @Override
  protected boolean isAlreadySaved(int userId, int exerciseId, short type) {
    return ((UserExerciseRepository) userEntityRepository)
            .existsByUserIdAndExerciseIdAndType(userId, exerciseId, type);
  }

  @Override
  protected UserExercise createUserEntity(User user, Exercise entity, short type) {
    return UserExercise.createWithUserExerciseType(user, entity, type);
  }

  @Override
  protected UserExercise findUserEntity(int userId, int exerciseId, short type) {
    return ((UserExerciseRepository) userEntityRepository)
            .findByUserIdAndExerciseIdAndType(userId, exerciseId, type)
            .orElseThrow(() -> new RecordNotFoundException(UserExercise.class, userId, exerciseId, type));
  }

  @Override
  protected List<UserExercise> findAllByUserAndType(int userId, short type) {
    return ((UserExerciseRepository) userEntityRepository).findByUserIdAndType(userId, type);
  }

  @Override
  protected Exercise extractEntity(UserExercise userExercise) {
    return userExercise.getExercise();
  }

  @Override
  protected long countSaves(int exerciseId) {
    return ((UserExerciseRepository) userEntityRepository).countByExerciseIdAndType(exerciseId, (short) 1);
  }

  @Override
  protected long countLikes(int exerciseId) {
    return ((UserExerciseRepository) userEntityRepository).countByExerciseIdAndType(exerciseId, (short) 2);
  }
}
