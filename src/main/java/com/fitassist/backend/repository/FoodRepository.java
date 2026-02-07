package com.fitassist.backend.repository;

import com.fitassist.backend.model.food.Food;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static com.fitassist.backend.model.food.Food.GRAPH_BASE;
import static com.fitassist.backend.model.food.Food.GRAPH_SUMMARY;

public interface FoodRepository extends JpaRepository<Food, Integer>, JpaSpecificationExecutor<Food> {

	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	Page<Food> findAll(Specification<Food> spec, @NotNull Pageable pageable);

	@EntityGraph(value = GRAPH_BASE)
	@Query("SELECT f FROM Food f")
	List<Food> findAllWithoutAssociations();

	@Query("""
			    SELECT f FROM Food f
			    LEFT JOIN FETCH f.foodCategory
			    LEFT JOIN FETCH f.mediaList
			    WHERE f.id = :id
			""")
	Optional<Food> findByIdWithMedia(@Param("id") int id);

	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	List<Food> findAll();

}
