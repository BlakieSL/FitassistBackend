package com.fitassist.backend.repository;

import com.fitassist.backend.model.text.TextBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TextRepository extends JpaRepository<TextBase, Integer> {

	@Modifying
	@Query(value = """
			    DELETE FROM text
			    WHERE plan_id = :planId
			""", nativeQuery = true)
	void deleteByPlanId(@Param("planId") int planId);

}
