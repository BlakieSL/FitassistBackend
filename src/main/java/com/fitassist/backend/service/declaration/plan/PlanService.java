package com.fitassist.backend.service.declaration.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.response.plan.PlanCategoriesResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.model.plan.Plan;

import java.util.List;

public interface PlanService {

	PlanResponseDto createPlan(PlanCreateDto planDto);

	void updatePlan(int planId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException;

	void deletePlan(int planId);

	PlanResponseDto getPlan(int id);

	Page<PlanSummaryDto> getFilteredPlans(FilterDto filter, Pageable pageable);

	List<Plan> getAllPlanEntities();

	void incrementViews(int planId);

	PlanCategoriesResponseDto getAllPlanCategories();

}
