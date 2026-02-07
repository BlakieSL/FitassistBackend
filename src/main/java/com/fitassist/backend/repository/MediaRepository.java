package com.fitassist.backend.repository;

import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Integer> {

	List<Media> findByParentIdAndParentType(int parentId, MediaConnectedEntity parentType);

	Optional<Media> findFirstByParentIdAndParentTypeOrderByIdAsc(@Param("parentId") int parentId,
			@Param("parentType") MediaConnectedEntity parentType);

	@Query("""
			    SELECT m FROM Media m
			    WHERE m.parentId IN :parentIds AND m.parentType = :parentType
			    	AND m.id = (
						SELECT MIN(m2.id)
						FROM Media m2
						WHERE m2.parentId = m.parentId
							AND m2.parentType = :parentType
					)
			""")
	List<Media> findFirstMediaByParentIds(@Param("parentIds") List<Integer> parentIds,
			@Param("parentType") MediaConnectedEntity parentType);

}
