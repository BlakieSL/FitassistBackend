package com.example.simplefullstackproject.repository;

import com.example.simplefullstackproject.model.RecipeCategoryAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeCategoryAssociationRepository extends JpaRepository<RecipeCategoryAssociation, Integer> {
    List<RecipeCategoryAssociation> findByRecipeCategoryId(int categoryId);
}