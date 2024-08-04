package com.example.simplefullstackproject.repositories;

import com.example.simplefullstackproject.models.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {
    Optional<ActivityCategory> findByName(String name);
}
