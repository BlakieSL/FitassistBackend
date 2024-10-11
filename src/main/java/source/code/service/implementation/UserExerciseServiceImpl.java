package source.code.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.exception.NotUniqueRecordException;
import source.code.model.Exercise;
import source.code.model.User;
import source.code.model.UserExercise;
import source.code.repository.ExerciseRepository;
import source.code.repository.UserExerciseRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.UserExerciseService;

import java.util.NoSuchElementException;

@Service
public class UserExerciseServiceImpl implements UserExerciseService {
  private final UserExerciseRepository userExerciseRepository;
  private final ExerciseRepository exerciseRepository;
  private final UserRepository userRepository;

  public UserExerciseServiceImpl(
          UserExerciseRepository userExerciseRepository,
          ExerciseRepository exerciseRepository,
          UserRepository userRepository) {
    this.userExerciseRepository = userExerciseRepository;
    this.exerciseRepository = exerciseRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void saveExerciseToUser(int exerciseId, int userId, short type) {
    if (userExerciseRepository.existsByUserIdAndExerciseIdAndType(userId, exerciseId, type)) {
      throw new NotUniqueRecordException(
              "User with id: " + userId
                      + " already has exercise with id: " + exerciseId
                      + " and type: " + type);
    }

    User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                    "User with id: " + userId + " not found"));

    Exercise exercise = exerciseRepository
            .findById(exerciseId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Exercise with id: " + exerciseId + " not found"));

    UserExercise userExercise =
            UserExercise.createWithUserExerciseType(user, exercise, type);
    userExerciseRepository.save(userExercise);
  }

  @Transactional
  public void deleteSavedExerciseFromUser(int exerciseId, int userId, short type) {
    UserExercise userExercise = userExerciseRepository
            .findByUserIdAndExerciseIdAndType(userId, exerciseId, type)
            .orElseThrow(() -> new NoSuchElementException(
                    "UserExercise with user id: " + userId
                            + ", exercise id: " + exerciseId
                            + " and type: " + type + " not found"));

    userExerciseRepository.delete(userExercise);
  }

  public LikesAndSavesResponseDto calculateExerciseLikesAndSaves(int exerciseId) {
    exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new NoSuchElementException(
                    "Exercise with id: " + exerciseId + " not found"));

    long saves = userExerciseRepository.countByExerciseIdAndType(exerciseId, (short) 1);
    long likes = userExerciseRepository.countByExerciseIdAndType(exerciseId, (short) 2);

    return new LikesAndSavesResponseDto(likes, saves);
  }
}
