package source.code.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String text;

    private AuthorDto author;

    private LocalDateTime interactedWithAt;

    private long likesCount;
    private long dislikesCount;
    private long repliesCount;

    private Boolean liked;
    private Boolean disliked;
}
