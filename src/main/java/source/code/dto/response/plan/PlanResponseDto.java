package source.code.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.PlanCategoryShortDto;
import source.code.model.user.BaseUserEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanResponseDto implements BaseUserEntity {
    private Integer id;
    private String name;
    private String description;
    private String text;
    private Integer userId;
    private PlanCategoryShortDto planType;
    private PlanCategoryShortDto expertiseLevel;
    private PlanCategoryShortDto planDuration;
    private List<PlanCategoryShortDto> categories;
}
