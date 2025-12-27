package source.code.dto.request.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanInstructionUpdateDto {

	private short orderIndex;

	private String title;

	private String text;

}
