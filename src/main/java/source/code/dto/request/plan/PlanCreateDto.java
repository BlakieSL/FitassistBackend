package source.code.dto.request.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.request.text.PlanInstructionCreateDto;
import source.code.helper.Enum.model.PlanStructureType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanCreateDto {

	private static final int NAME_MAX_LENGTH = 100;

	private static final int DESCRIPTION_MAX_LENGTH = 255;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = DESCRIPTION_MAX_LENGTH)
	private String description;

	private Boolean isPublic = false;

	@NotNull
	private PlanStructureType planStructureType;

	private List<Integer> categoryIds;

	private List<PlanInstructionCreateDto> instructions;

}
