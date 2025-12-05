package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import source.code.dto.response.category.CategoryResponseDto;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanCategoriesResponseDto implements Serializable {
    private List<CategoryResponseDto> types;
    private List<CategoryResponseDto> categories;
    private List<CategoryResponseDto> equipments;
}
