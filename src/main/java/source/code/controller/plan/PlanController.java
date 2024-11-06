package source.code.controller.plan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.Request.Filter.FilterDto;
import source.code.dto.Request.Plan.PlanCreateDto;
import source.code.dto.Response.Category.EquipmentResponseDto;
import source.code.dto.Response.PlanResponseDto;
import source.code.service.declaration.plan.PlanService;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDto> getPlan(@PathVariable int id) {
        PlanResponseDto plan = planService.getPlan(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    public ResponseEntity<List<PlanResponseDto>> getAllPlans() {
        List<PlanResponseDto> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}/equipment")
    public ResponseEntity<List<EquipmentResponseDto>> getAllEquipment(@PathVariable int id) {
        List<EquipmentResponseDto> equipment = planService.getAllEquipment(id);
        return ResponseEntity.ok(equipment);
    }


    @GetMapping("/{categoryId}/categories")
    public ResponseEntity<List<PlanResponseDto>> getPlansByCategory(@PathVariable int categoryId) {
        List<PlanResponseDto> plans = planService.getPlansByCategory(categoryId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<PlanResponseDto>> getFilteredPlans(
            @Valid @RequestBody FilterDto filterDto) {
        List<PlanResponseDto> filtered = planService.getFilteredPlans(filterDto);
        return ResponseEntity.ok(filtered);
    }

    @PostMapping
    public ResponseEntity<PlanResponseDto> createPlan(@Valid @RequestBody PlanCreateDto planDto) {
        PlanResponseDto response = planService.createPlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePlan(@PathVariable int id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        planService.updatePlan(id, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable int id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}