package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.ExerciseCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseCategoryAssociationRepository extends JpaRepository<ExerciseCategoryAssociation, Integer> {
    List<ExerciseCategoryAssociation> findByExerciseCategoryId(Integer categoryId);
}