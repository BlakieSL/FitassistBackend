package source.code.controller.Plan;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.response.PlanResponseDto;
import source.code.service.declaration.PlanService;
import source.code.service.declaration.UserPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {
  private final PlanService planService;
  private final UserPlanService userPlanService;

  public PlanController(PlanService planService, UserPlanService userPlanService) {
    this.planService = planService;
    this.userPlanService = userPlanService;
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

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PlanResponseDto>> getPlansByUser(@PathVariable int userId) {
    List<PlanResponseDto> plans = planService.getPlansByUser(userId);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/{id}/likes-and-saves")
  public ResponseEntity<LikesAndSavesResponseDto> getPlanLikesAndSaves(@PathVariable int id) {
    LikesAndSavesResponseDto dto = userPlanService.calculatePlanLikesAndSaves(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<PlanResponseDto> createPlan(@Valid @RequestBody PlanCreateDto planDto) {
    PlanResponseDto response = planService.createPlan(planDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}