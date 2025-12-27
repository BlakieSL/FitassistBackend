package source.code.repository;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import source.code.model.thread.ForumThread;

public interface ForumThreadRepository
		extends JpaRepository<ForumThread, Integer>, JpaSpecificationExecutor<ForumThread> {

	@EntityGraph(value = "ForumThread.summary")
	@NotNull
	@Override
	Page<ForumThread> findAll(Specification<ForumThread> spec, @NotNull Pageable pageable);

	@EntityGraph(value = "ForumThread.summary")
	@NotNull
	@Override
	Optional<ForumThread> findById(@NotNull Integer id);

	@EntityGraph(value = "ForumThread.summary")
	@NotNull
	List<ForumThread> findAll();

}
