package source.code.service.Declaration.Plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Plan.PlanCreateDto;
import source.code.dto.Response.PlanResponseDto;
import source.code.helper.Enum.PlanField;
import source.code.model.Plan.Plan;

import java.util.List;

public interface PlanService {
  PlanResponseDto createPlan(PlanCreateDto planDto);

  void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

  void deletePlan(int planId);

  PlanResponseDto getPlan(int id);

  List<PlanResponseDto> getAllPlans();

  List<Plan> getAllPlanEntities();

  List<PlanResponseDto> getPlansByCategory(int categoryId);

  public List<PlanResponseDto> getPlansByField(PlanField field, int value);
}