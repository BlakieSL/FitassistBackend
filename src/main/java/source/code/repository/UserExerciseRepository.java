package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
  List<UserExercise> findByUserId(int userId);

  Optional<UserExercise> findByUserIdAndExerciseIdAndType(int userId, int exerciseId, short type);

  boolean existsByUserIdAndExerciseIdAndType(int userId, int exerciseId, short type);

  long countByExerciseIdAndType(int exerciseId, short type);
}