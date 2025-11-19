package source.code.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.user.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
    List<UserExercise> findByUserId(int userId);

    Optional<UserExercise> findByUserIdAndExerciseId(int userId, int exerciseId);

    boolean existsByUserIdAndExerciseId(int userId, int exerciseId);

    @Query("""
           SELECT ue FROM UserExercise ue
           JOIN FETCH ue.exercise e
           JOIN FETCH e.expertiseLevel
           JOIN FETCH e.equipment
           JOIN FETCH e.mechanicsType
           JOIN FETCH e.forceType
           LEFT JOIN FETCH e.mediaList
           WHERE ue.user.id = :userId
           """)
    List<UserExercise> findAllByUserIdWithMedia(@Param("userId") int userId, Sort sort);

    long countByExerciseId(int exerciseId);
}