package source.code.dto.Request.comment;

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
public class CommentCreateDto {
    @NotBlank
    private String text;
    @NotNull
    private Integer threadId;
    @NotNull
    private Integer userId;
    private Integer parentCommentId;
}
