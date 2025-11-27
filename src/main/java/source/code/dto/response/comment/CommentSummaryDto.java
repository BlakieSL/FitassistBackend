package source.code.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSummaryDto implements BaseUserEntity, Serializable {
    private Integer id;
    private String text;
    private LocalDateTime createdAt;
    private String authorUsername;
    private Integer authorId;
    private String authorImageName;
    private String authorImageUrl;
    private int likeCount;
    private int repliesCount;
    private LocalDateTime userCommentInteractionCreatedAt;
}
