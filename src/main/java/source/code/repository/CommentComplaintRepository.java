package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.complaint.CommentComplaint;

public interface CommentComplaintRepository extends JpaRepository<CommentComplaint, Integer> {
}