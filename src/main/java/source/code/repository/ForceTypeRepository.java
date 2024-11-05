package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.exercise.ForceType;

public interface ForceTypeRepository extends JpaRepository<ForceType, Integer> {
}