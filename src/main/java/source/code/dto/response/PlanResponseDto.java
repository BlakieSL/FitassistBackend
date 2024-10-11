package source.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.other.PlanCategoryShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanResponseDto {
  private Integer id;
  private String name;
  private String description;
  private String text;
  private Double score;
  private PlanCategoryShortDto planType;
  private PlanCategoryShortDto planDuration;
  private PlanCategoryShortDto planEquipment;
  private PlanCategoryShortDto planExpertiseLevel;
  private List<PlanCategoryShortDto> categories;
}
