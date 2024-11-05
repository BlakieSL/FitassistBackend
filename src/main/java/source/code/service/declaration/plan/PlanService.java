package source.code.service.declaration.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Request.Plan.PlanCreateDto;
import source.code.dto.Response.Category.EquipmentResponseDto;
import source.code.dto.Response.PlanResponseDto;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanService {
    PlanResponseDto createPlan(PlanCreateDto planDto);

    void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deletePlan(int planId);

    PlanResponseDto getPlan(int id);

    List<PlanResponseDto> getAllPlans();

    List<PlanResponseDto> getFilteredPlans(FilterDto filter);

    List<Plan> getAllPlanEntities();

    List<PlanResponseDto> getPlansByCategory(int categoryId);

    List<EquipmentResponseDto> getAllEquipment(int planId);
}