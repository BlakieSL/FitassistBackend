package source.code.dto.response.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.BaseUserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ForumThreadResponseDto implements BaseUserEntity {
    private Integer id;
    private String title;
    private LocalDateTime dateCreated;
    private String text;
    private int views;
    private Integer userId;
    private Integer threadCategoryId;
}
