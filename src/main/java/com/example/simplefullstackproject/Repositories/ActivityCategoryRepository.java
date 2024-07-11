package com.example.simplefullstackproject.Repositories;

import com.example.simplefullstackproject.Models.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {
    Optional<ActivityCategory> findByName(String name);
}
