package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExpertiseLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertiseLevelRepository extends JpaRepository<ExpertiseLevel, Integer> {
}