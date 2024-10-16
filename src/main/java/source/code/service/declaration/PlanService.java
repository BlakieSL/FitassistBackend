package source.code.service.declaration;

import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;

import java.util.List;

public interface PlanService {
  PlanResponseDto createPlan(PlanCreateDto planDto);

  PlanResponseDto getPlan(int id);

  List<PlanResponseDto> getAllPlans();

  List<PlanResponseDto> getPlansByUserAndType(int userId, short type);

  List<PlanCategoryResponseDto> getAllCategories();

  List<PlanResponseDto> getPlansByCategory(int categoryId);

  List<PlanResponseDto> getPlansByType(int planTypeId);

  List<PlanResponseDto> getPlansByDuration(int planDurationId);

  List<PlanResponseDto> getPlansByEquipment(int planEquipmentId);

  List<PlanResponseDto> getPlansByExpertiseLevel(int planExpertiseLevelId);
}