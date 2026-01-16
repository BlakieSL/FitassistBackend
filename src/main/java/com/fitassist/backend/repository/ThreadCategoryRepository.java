package com.fitassist.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitassist.backend.model.thread.ThreadCategory;

public interface ThreadCategoryRepository extends JpaRepository<ThreadCategory, Integer> {

	boolean existsByIdAndThreadsIsNotEmpty(Integer id);

}
