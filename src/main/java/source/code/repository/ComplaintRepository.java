package source.code.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import source.code.model.complaint.ComplaintBase;

import java.util.Optional;

public interface ComplaintRepository extends PagingAndSortingRepository<ComplaintBase, Integer> {
    Optional<ComplaintBase> findById(Integer id);
}
