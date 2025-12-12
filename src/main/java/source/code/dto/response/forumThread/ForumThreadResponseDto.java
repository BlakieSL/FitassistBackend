package source.code.dto.response.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.response.category.CategoryResponseDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ForumThreadResponseDto implements Serializable {
    private Integer id;
    private String title;
    private LocalDateTime createdAt;
    private String text;
    private long views;
    private Integer userId;
    private CategoryResponseDto category;
}
