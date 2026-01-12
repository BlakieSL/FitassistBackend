package source.code.dto.request.recipe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.request.text.TextCreateDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCreateDto {

	private static final int NAME_MAX_LENGTH = 100;

	private static final int DESCRIPTION_MAX_LENGTH = 255;

	@NotBlank
	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@NotBlank
	@Size(max = DESCRIPTION_MAX_LENGTH)
	private String description;

	@NotNull
	private Short minutesToPrepare;

	private Boolean isPublic = false;

	private List<Integer> categoryIds;

	@Valid
	private List<TextCreateDto> instructions;

}
