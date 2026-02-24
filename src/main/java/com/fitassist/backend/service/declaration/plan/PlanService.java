package com.fitassist.backend.service.declaration.plan;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.response.plan.PlanCategoriesResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.model.plan.Plan;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlanService {

	PlanResponseDto createPlan(PlanCreateDto planDto);

	void updatePlan(int planId, JsonMergePatch patch) throws JacksonException;

	void deletePlan(int planId);

	PlanResponseDto getPlan(int id);

	Page<PlanSummaryDto> getFilteredPlans(FilterDto filter, Pageable pageable);

	List<Plan> getAllPlanEntities();

	void incrementViews(int planId);

	PlanCategoriesResponseDto getAllPlanCategories();

}
