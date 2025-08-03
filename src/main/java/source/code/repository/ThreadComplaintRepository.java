package source.code.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.model.complaint.CommentComplaint;
import source.code.model.complaint.ThreadComplaint;

import java.util.Optional;

public interface ThreadComplaintRepository extends JpaRepository<ThreadComplaint, Integer> {
}