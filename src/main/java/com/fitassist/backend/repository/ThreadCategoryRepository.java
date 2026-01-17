package com.fitassist.backend.repository;

import com.fitassist.backend.model.thread.ThreadCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadCategoryRepository extends JpaRepository<ThreadCategory, Integer> {

	boolean existsByIdAndThreadsIsNotEmpty(Integer id);

}
