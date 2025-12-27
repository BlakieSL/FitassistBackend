package source.code.dto.response.text;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTipResponseDto implements BaseTextResponseDto, Serializable {

	private Integer id;

	private short orderIndex;

	private String text;

}
