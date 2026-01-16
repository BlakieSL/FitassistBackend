package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.exercise.ExpertiseLevel;

public interface ExpertiseLevelRepository extends JpaRepository<ExpertiseLevel, Integer> {

}
