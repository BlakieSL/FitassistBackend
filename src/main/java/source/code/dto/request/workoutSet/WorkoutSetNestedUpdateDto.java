package source.code.dto.request.workoutSet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseNestedCreateDto;
import source.code.dto.request.workoutSetExercise.WorkoutSetExerciseNestedUpdateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSetNestedUpdateDto {

	private Integer id;

	private Short orderIndex;

	private Short restSeconds;

	@Valid
	private List<WorkoutSetExerciseNestedUpdateDto> workoutSetExercises;

}
