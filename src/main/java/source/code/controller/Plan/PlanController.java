package source.code.controller.Plan;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.Plan.PlanCreateDto;
import source.code.dto.response.PlanResponseDto;
import source.code.service.declaration.PlanService;

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

  @PostMapping
  public ResponseEntity<PlanResponseDto> createPlan(@Valid @RequestBody PlanCreateDto planDto) {
    PlanResponseDto response = planService.createPlan(planDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}