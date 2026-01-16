package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.exercise.ForceType;

public interface ForceTypeRepository extends JpaRepository<ForceType, Integer> {

}
