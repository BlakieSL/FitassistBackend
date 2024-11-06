package source.code.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class LikesAndSavesResponseDto {
    private long likes;
    private long saves;
}
