package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.Other.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
}