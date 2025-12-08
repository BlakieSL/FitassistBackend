package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.complaint.ComplaintBase;

import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<ComplaintBase, Integer> {
    @EntityGraph(value = "ComplaintBase.withoutAssociations")
    @NotNull
    Optional<ComplaintBase> findById(@NotNull Integer id);

    @EntityGraph(value = "ComplaintBase.withoutAssociations")
    @Query("SELECT cb FROM ComplaintBase cb")
    @NotNull
    Page<ComplaintBase> findAll(@NotNull Pageable pageable);
}
