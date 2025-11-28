package source.code.controller.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.plan.PlanOwnerOrAdmin;
import source.code.annotation.plan.PublicPlanOrOwnerOrAdmin;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.service.declaration.plan.PlanService;

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

    @PlanOwnerOrAdmin
    @PatchMapping("/{planId}")
    public ResponseEntity<Void> updatePlan(@PathVariable int planId, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        planService.updatePlan(planId, patch);
        return ResponseEntity.noContent().build();
    }

    @PlanOwnerOrAdmin
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable int planId) {
        planService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }

    @PublicPlanOrOwnerOrAdmin
    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponseDto> getPlan(@PathVariable int planId) {
        PlanResponseDto plan = planService.getPlan(planId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping({"/private", "/private/{showPrivate}"})
    public ResponseEntity<Page<PlanSummaryDto>> getAllPlans(
            @PathVariable(required = false) Boolean showPrivate,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PlanSummaryDto> plans = planService.getAllPlans(showPrivate, pageable);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<PlanSummaryDto>> getFilteredPlans(
            @Valid @RequestBody FilterDto filterDto,
            @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PlanSummaryDto> filtered = planService.getFilteredPlans(filterDto, pageable);
        return ResponseEntity.ok(filtered);
    }

    @PatchMapping("/{planId}/view")
    public ResponseEntity<Void> incrementViews(@PathVariable int planId) {
        planService.incrementViews(planId);
        return ResponseEntity.noContent().build();
    }
}