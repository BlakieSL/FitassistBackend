package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.MechanicsType;

public interface MechanicsTypeRepository extends JpaRepository<MechanicsType, Integer> {
}