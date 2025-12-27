package source.code.dto.request.workout;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutUpdateDto {

	private static final int NAME_MAX_LENGTH = 50;

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@PositiveOrZero
	private Short duration;

	@Positive
	private Short orderIndex;

	@PositiveOrZero
	private Byte restDaysAfter;

}
