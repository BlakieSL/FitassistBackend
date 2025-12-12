package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.thread.ForumThread;

import java.util.List;
import java.util.Optional;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer>, JpaSpecificationExecutor<ForumThread> {
    @EntityGraph(value = "ForumThread.summary")
    @NotNull
    Page<ForumThread> findAll(Specification<ForumThread> spec, @NotNull Pageable pageable);

    @EntityGraph(value = "ForumThread.summary")
    @Query("SELECT ft FROM ForumThread ft WHERE ft.id = :id")
    Optional<ForumThread> findByIdWithDetails(@Param("id") Integer id);

    @EntityGraph(value = "ForumThread.summary")
    @NotNull
    List<ForumThread> findAll();

    @EntityGraph(value = "ForumThread.summary")
    List<ForumThread> findAllByThreadCategoryId(int categoryId);

    @Query(value = """
      SELECT ft
      FROM ForumThread ft
      JOIN FETCH ft.user u
      WHERE ft.user.id = :userId
    """)
    Page<ForumThread> findCreatedByUserWithDetails(@Param("userId") int userId, Pageable pageable);
}