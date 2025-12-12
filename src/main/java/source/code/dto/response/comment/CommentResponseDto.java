package source.code.dto.response.comment;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CommentResponseDto implements Serializable {
    private Integer id;
    private String text;
    private LocalDateTime createdAt;
    private Integer threadId;
    private Integer userId;
    private AuthorDto author;
    private Integer parentCommentId;
    private List<CommentResponseDto> replies;
}
