package source.code.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikesAndSavesResponseDto {
    private long likes;
    private long saves;

    public static LikesAndSavesResponseDto of(long likes, long saves) {
        return new LikesAndSavesResponseDto(likes, saves);
    }
}
