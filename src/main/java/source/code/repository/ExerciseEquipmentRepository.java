package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Exercise.ExerciseEquipment;

public interface ExerciseEquipmentRepository extends JpaRepository<ExerciseEquipment, Integer> {
}