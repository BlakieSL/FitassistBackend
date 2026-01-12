package source.code.dto.request.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FilterRecipesByFoodsDto {

	@NotNull
	private List<Integer> foodIds;

}
