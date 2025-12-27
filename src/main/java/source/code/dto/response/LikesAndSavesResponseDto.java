package source.code.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(staticName = "of")
public class LikesAndSavesResponseDto implements Serializable {

	private long likes;

	private long saves;

}
