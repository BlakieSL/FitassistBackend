package source.code.dto.response.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThreadSummaryDto {
    private int id;
    private String title;
    private LocalDateTime dateCreated;
    private String text;
    private int viewsCount;
    private int savesCount;
    private int commentsCount;
    private String authorUsername;
}
