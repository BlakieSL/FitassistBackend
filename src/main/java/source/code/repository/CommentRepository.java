package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.forum.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByThreadIdAndParentCommentNull(int threadId);
    List<Comment> findAllByParentCommentId(int commentId);
    long countAllByThreadId(int threadId);
}
