package com.fitassist.backend.repository;

import com.fitassist.backend.model.thread.ForumThread;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

import static com.fitassist.backend.model.thread.ForumThread.GRAPH_SUMMARY;

public interface ForumThreadRepository
		extends JpaRepository<ForumThread, Integer>, JpaSpecificationExecutor<ForumThread> {

	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	@Override
	Page<ForumThread> findAll(Specification<ForumThread> spec, @NotNull Pageable pageable);

	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	@Override
	Optional<ForumThread> findById(@NotNull Integer id);

	@EntityGraph(value = GRAPH_SUMMARY)
	@NotNull
	List<ForumThread> findAll();

}
