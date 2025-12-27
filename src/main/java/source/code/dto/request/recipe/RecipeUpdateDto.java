package source.code.dto.request.recipe;

import jakarta.validation.constraints.Size;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeUpdateDto {

	private static final int NAME_MAX_LENGTH = 100;

	private static final int DESCRIPTION_MAX_LENGTH = 255;

	@Size(max = NAME_MAX_LENGTH)
	private String name;

	@Size(max = DESCRIPTION_MAX_LENGTH)
	private String description;

	private Short minutesToPrepare;

	private Boolean isPublic;

	private List<Integer> categoryIds;

}
