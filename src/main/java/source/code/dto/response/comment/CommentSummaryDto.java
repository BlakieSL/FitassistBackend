package source.code.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentSummaryDto {
    private int id;
    private String text;
    private LocalDateTime dateCreated;
    private String authorUsername;
    private int authorId;
    private String authorImageUrl;
    private int likeCount;
    private int repliesCount;
}
