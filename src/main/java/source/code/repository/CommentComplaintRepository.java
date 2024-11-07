package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.forum.CommentComplaint;

public interface CommentComplaintRepository extends JpaRepository<CommentComplaint, Integer> {
}