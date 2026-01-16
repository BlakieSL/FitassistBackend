package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.exercise.MechanicsType;

public interface MechanicsTypeRepository extends JpaRepository<MechanicsType, Integer> {

}
