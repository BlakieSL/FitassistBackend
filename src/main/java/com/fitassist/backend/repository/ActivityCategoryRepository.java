package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.activity.ActivityCategory;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {

	boolean existsByIdAndActivitiesIsNotEmpty(Integer id);

}
