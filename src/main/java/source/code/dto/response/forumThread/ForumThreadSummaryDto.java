package source.code.dto.response.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThreadSummaryDto implements BaseUserEntity {
    private int id;
    private String title;
    private LocalDateTime dateCreated;
    private String text;
    private int viewsCount;
    private int savesCount;
    private int commentsCount;
    private String authorUsername;
    private int authorId;
    private String authorImageName;
    private String authorImageUrl;
}
