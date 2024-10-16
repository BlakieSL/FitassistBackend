package source.code.service.declaration;

import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.helper.enumerators.PlanField;

import java.util.List;

public interface PlanService {
  PlanResponseDto createPlan(PlanCreateDto planDto);

  PlanResponseDto getPlan(int id);

  List<PlanResponseDto> getAllPlans();

  List<PlanResponseDto> getPlansByUserAndType(int userId, short type);

  List<PlanCategoryResponseDto> getAllCategories();

  List<PlanResponseDto> getPlansByCategory(int categoryId);

  public List<PlanResponseDto> getPlansByField(PlanField field, int value);
}