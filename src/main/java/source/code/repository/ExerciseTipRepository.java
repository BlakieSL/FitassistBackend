package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.text.ExerciseTip;

import java.util.List;

public interface ExerciseTipRepository extends JpaRepository<ExerciseTip, Integer> {
    List<ExerciseTip> getAllByExerciseId(int exerciseId);
}