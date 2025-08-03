package source.code.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import source.code.model.complaint.ComplaintBase;

import java.util.Optional;

public interface ComplaintRepository extends PagingAndSortingRepository<ComplaintBase, Integer> {
    @EntityGraph(value = "ComplaintBase.withoutAssociations")
    Optional<ComplaintBase> findById(Integer id);

    @EntityGraph(value = "ComplaintBase.withoutAssociations")
    @Query("SELECT cb FROM ComplaintBase cb")
    Page<ComplaintBase> findAll(@NotNull Pageable pageable);
}
