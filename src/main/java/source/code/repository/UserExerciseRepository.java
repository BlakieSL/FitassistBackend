package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.User.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
  List<UserExercise> findByUserId(int userId);

  List<UserExercise> findByUserIdAndType(int userId, short type);
  Optional<UserExercise> findByUserIdAndExerciseIdAndType(int userId, int exerciseId, short type);

  boolean existsByUserIdAndExerciseIdAndType(int userId, int exerciseId, short type);

  long countByExerciseIdAndType(int exerciseId, short type);
}