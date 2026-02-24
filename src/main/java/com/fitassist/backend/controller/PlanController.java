package com.fitassist.backend.controller;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.annotation.plan.PlanOwnerOrAdminOrModerator;
import com.fitassist.backend.annotation.plan.PublicPlanOrOwnerOrAdminOrModerator;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.plan.PlanCreateDto;
import com.fitassist.backend.dto.response.plan.PlanCategoriesResponseDto;
import com.fitassist.backend.dto.response.plan.PlanResponseDto;
import com.fitassist.backend.dto.response.plan.PlanSummaryDto;
import com.fitassist.backend.service.declaration.plan.PlanService;
import jakarta.json.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

	private final PlanService planService;

	public PlanController(PlanService planService) {
		this.planService = planService;
	}

	@PostMapping
	public ResponseEntity<PlanResponseDto> createPlan(@Valid @RequestBody PlanCreateDto planDto) {
		PlanResponseDto response = planService.createPlan(planDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PlanOwnerOrAdminOrModerator
	@PatchMapping("/{planId}")
	public ResponseEntity<Void> updatePlan(@PathVariable int planId, @RequestBody JsonMergePatch patch)
			throws JacksonException {
		planService.updatePlan(planId, patch);
		return ResponseEntity.noContent().build();
	}

	@PlanOwnerOrAdminOrModerator
	@DeleteMapping("/{planId}")
	public ResponseEntity<Void> deletePlan(@PathVariable int planId) {
		planService.deletePlan(planId);
		return ResponseEntity.noContent().build();
	}

	@PublicPlanOrOwnerOrAdminOrModerator
	@GetMapping("/{planId}")
	public ResponseEntity<PlanResponseDto> getPlan(@PathVariable int planId) {
		PlanResponseDto plan = planService.getPlan(planId);
		return ResponseEntity.ok(plan);
	}

	@PostMapping("/filter")
	public ResponseEntity<Page<PlanSummaryDto>> getFilteredPlans(@Valid @RequestBody FilterDto filterDto,
			@PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<PlanSummaryDto> filtered = planService.getFilteredPlans(filterDto, pageable);
		return ResponseEntity.ok(filtered);
	}

	@PatchMapping("/{planId}/view")
	public ResponseEntity<Void> incrementViews(@PathVariable int planId) {
		planService.incrementViews(planId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/categories")
	public ResponseEntity<PlanCategoriesResponseDto> getAllPlanCategories() {
		PlanCategoriesResponseDto categories = planService.getAllPlanCategories();
		return ResponseEntity.ok(categories);
	}

}
