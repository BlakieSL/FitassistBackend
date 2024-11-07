package source.code.dto.response.forumThread;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ForumThreadResponseDto {
    private Integer id;
    private String title;
    private LocalDate dateCreated;
    private String text;
    private int views;
    private Integer userId;
    private Integer threadCategoryId;
    private int commentsCount;
}
