package source.code.repository;

import source.code.model.ExerciseTip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseTipRepository extends JpaRepository<ExerciseTip, Integer> {
    List<ExerciseTip> getAllByExerciseId(int exerciseId);
}