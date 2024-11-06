package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository
import source.code.model.thread.Comment

interface CommentRepository : JpaRepository<Comment, Int> {
}