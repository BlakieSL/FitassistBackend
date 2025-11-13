package source.code.dto.response.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CommentResponseDto implements BaseUserEntity {
    private Integer id;
    private String text;
    private LocalDateTime dateCreated;
    private Integer threadId;
    private Integer userId;
    private Integer parentCommentId;
    private List<CommentResponseDto> replies;
}
