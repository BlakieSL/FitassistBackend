package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.forum.ThreadComplaint;

public interface ThreadComplaintRepository extends JpaRepository<ThreadComplaint, Integer> {
}