package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.user.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
    List<UserExercise> findByUserId(int userId);

    Optional<UserExercise> findByUserIdAndExerciseId(int userId, int exerciseId);

    boolean existsByUserIdAndExerciseId(int userId, int exerciseId);

    long countByExerciseId(int exerciseId);
}