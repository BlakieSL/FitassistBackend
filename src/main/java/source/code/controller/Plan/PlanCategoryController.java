package source.code.controller.Plan;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import source.code.dto.response.PlanCategoryResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.helper.enumerators.PlanField;
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

  @GetMapping("/field/{field}/{value}")
  public ResponseEntity<List<PlanResponseDto>> getPlansByField(
          @PathVariable PlanField field,
          @PathVariable int value) {
    List<PlanResponseDto> plans = planService.getPlansByField(field, value);
    return ResponseEntity.ok(plans);
  }
}
