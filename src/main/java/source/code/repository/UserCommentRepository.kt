package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository
import source.code.model.user.UserComment

interface UserCommentRepository : JpaRepository<UserComment, Int> {
}