package source.code.service.declaration.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.response.category.EquipmentResponseDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanService {
    PlanResponseDto createPlan(PlanCreateDto planDto);

    void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deletePlan(int planId);

    PlanResponseDto getPlan(int id);

    List<PlanResponseDto> getAllPlans(Boolean isPrivate);

    List<PlanResponseDto> getFilteredPlans(FilterDto filter);

    List<Plan> getAllPlanEntities();
}