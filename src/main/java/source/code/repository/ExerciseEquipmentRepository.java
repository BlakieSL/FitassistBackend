package source.code.repository;

import source.code.model.ExerciseEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseEquipmentRepository extends JpaRepository<ExerciseEquipment, Integer> {
}