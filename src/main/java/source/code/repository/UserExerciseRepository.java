package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.dto.response.exercise.ExerciseSummaryDto;
import source.code.model.user.UserExercise;

import java.util.List;
import java.util.Optional;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer> {
    List<UserExercise> findByUserId(int userId);

    Optional<UserExercise> findByUserIdAndExerciseId(int userId, int exerciseId);

    boolean existsByUserIdAndExerciseId(int userId, int exerciseId);

    @Query("""
           SELECT new source.code.dto.response.exercise.ExerciseSummaryDto(
               e.id,
               e.name,
               (SELECT m.imageName FROM Media m
                WHERE m.parentId = e.id
                AND m.parentType = 'EXERCISE'
                ORDER BY m.id ASC
                LIMIT 1),
               null,
               new source.code.dto.pojo.CategoryDto(e.expertiseLevel.id, e.expertiseLevel.name),
               new source.code.dto.pojo.CategoryDto(e.equipment.id, e.equipment.name),
               new source.code.dto.pojo.CategoryDto(e.mechanicsType.id, e.mechanicsType.name),
               new source.code.dto.pojo.CategoryDto(e.forceType.id, e.forceType.name))
           FROM UserExercise ue
           JOIN ue.exercise e
           WHERE ue.user.id = :userId
           """)
    List<ExerciseSummaryDto> findExerciseSummaryByUserId(@Param("userId") int userId);

    long countByExerciseId(int exerciseId);
}