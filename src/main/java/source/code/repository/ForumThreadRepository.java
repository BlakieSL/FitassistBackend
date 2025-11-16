package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.model.thread.ForumThread;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer> {
    List<ForumThread> findAllByThreadCategoryId(int categoryId);

    List<ForumThread> findAllByUser_Id(Integer userId);

    @Query("""
      SELECT new source.code.dto.response.forumThread.ForumThreadSummaryDto(
             t.id,
             t.title,
             t.dateCreated,
             t.text,
             t.views,
             SIZE(t.userThreads),
             SIZE(t.comments),
             t.user.username,
             t.user.id,
             null)
      FROM ForumThread t
      WHERE t.user.id = :userId
      """)
    List<ForumThreadSummaryDto> findSummaryByUserId(Integer userId);
}
