package source.code.service.declaration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.Plan.PlanCreateDto;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.helper.enumerators.PlanField;

import java.util.List;

public interface PlanService {
  PlanResponseDto createPlan(PlanCreateDto planDto);

  void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deletePlan(int planId);

  PlanResponseDto getPlan(int id);

  List<PlanResponseDto> getAllPlans();

  List<PlanCategoryResponseDto> getAllCategories();

  List<PlanResponseDto> getPlansByCategory(int categoryId);

  public List<PlanResponseDto> getPlansByField(PlanField field, int value);
}