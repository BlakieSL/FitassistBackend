package source.code.controller.Plan;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.service.declaration.PlanService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/plan-categories")
public class PlanCategoryController {
  private final PlanService planService;

  public PlanCategoryController(PlanService planService) {
    this.planService = planService;
  }

  @GetMapping
  public ResponseEntity<List<PlanCategoryResponseDto>> getAllPlanCategories() {
    return ResponseEntity.ok(planService.getAllCategories());
  }

  @GetMapping("/{categoryId}/categories")
  public ResponseEntity<List<PlanResponseDto>> getPlansByCategory(@PathVariable int categoryId) {
    return ResponseEntity.ok(planService.getPlansByCategory(categoryId));
  }

  @GetMapping("/{typeId}/type")
  public ResponseEntity<List<PlanResponseDto>> getPlansByType(@PathVariable int typeId) {
    List<PlanResponseDto> plans = planService.getPlansByType(typeId);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/{durationId}/duration")
  public ResponseEntity<List<PlanResponseDto>> getPlansByDuration(@PathVariable int durationId) {
    List<PlanResponseDto> plans = planService.getPlansByDuration(durationId);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/{equipmentId}/equipment")
  public ResponseEntity<List<PlanResponseDto>> getPlansByEquipment(@PathVariable int equipmentId) {
    List<PlanResponseDto> plans = planService.getPlansByEquipment(equipmentId);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/{expertiseLevelId}/expertise-level")
  public ResponseEntity<List<PlanResponseDto>> getPlansByExpertiseLevel(
          @PathVariable int expertiseLevelId) {
    List<PlanResponseDto> plans = planService.getPlansByExpertiseLevel(expertiseLevelId);
    return ResponseEntity.ok(plans);
  }
}
