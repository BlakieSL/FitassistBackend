package source.code.dto.Response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class LikesAndSavesResponseDto {
    private long likes;
    private long saves;
}
