package source.code.dto.request.forumThread;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ForumThreadCreateDto {
    @NotBlank
    private String title;

    @NotBlank
    private String text;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer threadCategoryId;
}
