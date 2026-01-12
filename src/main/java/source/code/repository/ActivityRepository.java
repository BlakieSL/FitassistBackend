package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.activity.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {

	@EntityGraph(value = "Activity.summary")
	@NotNull
	@Override
	Page<Activity> findAll(Specification<Activity> spec, @NotNull Pageable pageable);

	@EntityGraph(value = "Activity.summary")
	@NotNull
	@Override
	List<Activity> findAll();

	@EntityGraph(value = "Activity.withoutAssociations")
	@Query("SELECT a FROM Activity a")
	List<Activity> findAllWithoutAssociations();

	@EntityGraph(value = "Activity.withAssociations")
	@Query("SELECT a FROM Activity a WHERE a.id = :id")
	Optional<Activity> findByIdWithAssociations(int id);

	@Query("""
			SELECT a FROM Activity a
			LEFT JOIN FETCH a.activityCategory
			LEFT JOIN FETCH a.mediaList
			WHERE a.id = :id
			""")
	Optional<Activity> findByIdWithMedia(int id);

}
