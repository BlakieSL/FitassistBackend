package source.code.dto.response.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThreadSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String title;
    private String text;
    private CategoryResponseDto category;

    private AuthorDto author;

    private LocalDateTime interactedWithAt;

    private long views;
    private long savesCount;
    private long commentsCount;

    private Boolean saved;
}
