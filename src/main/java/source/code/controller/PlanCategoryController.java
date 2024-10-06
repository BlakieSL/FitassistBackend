package source.code.controller;

import source.code.dto.PlanCategoryDto;
import source.code.dto.PlanDto;
import source.code.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/plan-categories")
public class PlanCategoryController {
    private final PlanService planService;

    public PlanCategoryController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanCategoryDto>> getAllPlanCategories() {
        return ResponseEntity.ok(planService.getCategories());
    }

    @GetMapping("/{categoryId}/categories")
    public ResponseEntity<List<PlanDto>> getPlansByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(planService.getPlansByCategory(categoryId));
    }

    @GetMapping("/{typeId}/type")
    public ResponseEntity<List<PlanDto>> getPlansByType(@PathVariable int typeId) {
        List<PlanDto> plans = planService.getPlansByType(typeId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{durationId}/duration")
    public ResponseEntity<List<PlanDto>> getPlansByDuration(@PathVariable int durationId) {
        List<PlanDto> plans = planService.getPlansByDuration(durationId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{equipmentId}/equipment")
    public ResponseEntity<List<PlanDto>> getPlansByEquipment(@PathVariable int equipmentId) {
        List<PlanDto> plans = planService.getPlansByEquipment(equipmentId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{expertiseLevelId}/expertise-level")
    public ResponseEntity<List<PlanDto>> getPlansByExpertiseLevel(@PathVariable int expertiseLevelId) {
        List<PlanDto> plans = planService.getPlansByExpertiseLevel(expertiseLevelId);
        return ResponseEntity.ok(plans);
    }
}
