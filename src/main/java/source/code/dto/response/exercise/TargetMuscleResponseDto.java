package source.code.dto.response.exercise;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetMuscleResponseDto {

	private Integer id;

	private String name;

	private BigDecimal priority;

	public static TargetMuscleResponseDto create(Integer id, String name, BigDecimal priority) {
		return new TargetMuscleResponseDto(id, name, priority);
	}

}
