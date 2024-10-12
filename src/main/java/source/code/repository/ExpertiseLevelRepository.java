package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Exercise.ExpertiseLevel;

public interface ExpertiseLevelRepository extends JpaRepository<ExpertiseLevel, Integer> {
}