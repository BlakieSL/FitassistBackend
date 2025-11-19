package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import source.code.model.activity.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository
        extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {
    @EntityGraph(value = "Activity.withoutAssociations")
    @Query("SELECT a FROM Activity a")
    List<Activity> findAllWithoutAssociations();

    @EntityGraph(value = "Activity.withAssociations")
    @Query("SELECT a FROM Activity a WHERE a.id = :id")
    Optional<Activity> findByIdWithAssociations(int id);

    @EntityGraph(attributePaths = {"activityCategory"})
    @Query("SELECT a FROM Activity a")
    List<Activity> findAllWithActivityCategory();

    @Query("SELECT a FROM Activity a " +
           "LEFT JOIN FETCH a.activityCategory " +
           "LEFT JOIN FETCH a.mediaList " +
           "WHERE a.id = :id")
    Optional<Activity> findByIdWithMedia(int id);
}