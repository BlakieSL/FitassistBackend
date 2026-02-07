package com.fitassist.backend.repository;

import com.fitassist.backend.model.complaint.ComplaintBase;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static com.fitassist.backend.model.complaint.ComplaintBase.GRAPH_BASE;

public interface ComplaintRepository extends JpaRepository<ComplaintBase, Integer> {

	@EntityGraph(value = GRAPH_BASE)
	@NotNull
	@Override
	Optional<ComplaintBase> findById(@NotNull Integer id);

	@EntityGraph(value = GRAPH_BASE)
	@Query("SELECT cb FROM ComplaintBase cb")
	@NotNull
	@Override
	Page<ComplaintBase> findAll(@NotNull Pageable pageable);

}
