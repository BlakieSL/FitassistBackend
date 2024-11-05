package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.other.ExpertiseLevel;

public interface ExpertiseLevelRepository extends JpaRepository<ExpertiseLevel, Integer> {
}