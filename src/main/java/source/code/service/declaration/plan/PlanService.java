package source.code.service.declaration.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.model.plan.Plan;

import java.util.List;

public interface PlanService {
    PlanResponseDto createPlan(PlanCreateDto planDto);

    void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

    void deletePlan(int planId);

    PlanResponseDto getPlan(int id);

    Page<PlanSummaryDto> getAllPlans(Boolean showPrivate, Pageable pageable);

    Page<PlanSummaryDto> getFilteredPlans(FilterDto filter, Pageable pageable);

    List<Plan> getAllPlanEntities();

    void incrementViews(int planId);
}