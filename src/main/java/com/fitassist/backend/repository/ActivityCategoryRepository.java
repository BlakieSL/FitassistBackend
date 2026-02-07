package com.fitassist.backend.repository;

import com.fitassist.backend.model.activity.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {

	boolean existsByIdAndActivitiesIsNotEmpty(int id);

}
