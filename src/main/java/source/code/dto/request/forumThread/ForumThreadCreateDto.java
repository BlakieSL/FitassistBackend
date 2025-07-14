package source.code.dto.request.forumThread;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Integer threadCategoryId;
}
