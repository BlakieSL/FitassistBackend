package source.code.controller.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.annotation.plan.PlanOwnerOrAdmin;
import source.code.annotation.plan.PublicPlanOrOwnerOrAdmin;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.plan.PlanCreateDto;
import source.code.dto.response.plan.PlanResponseDto;
import source.code.service.declaration.plan.PlanService;

import java.util.List;

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

    @GetMapping({"/private", "/private/{isPrivate}"})
    public ResponseEntity<List<PlanResponseDto>> getAllPlans(
            @PathVariable(required = false) Boolean isPrivate) {
        List<PlanResponseDto> plans = planService.getAllPlans(isPrivate);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<PlanResponseDto>> getFilteredPlans(
            @Valid @RequestBody FilterDto filterDto) {
        List<PlanResponseDto> filtered = planService.getFilteredPlans(filterDto);
        return ResponseEntity.ok(filtered);
    }
}