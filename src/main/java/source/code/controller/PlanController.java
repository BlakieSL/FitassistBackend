package source.code.controller;

import source.code.dto.response.LikesAndSavesResponseDto;
import source.code.dto.request.PlanCreateDto;
import source.code.dto.response.PlanResponseDto;
import source.code.service.PlanService;
import source.code.service.UserPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PlanResponseDto> getPlanById(@PathVariable int id) {
        PlanResponseDto plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    public ResponseEntity<List<PlanResponseDto>> getPlans() {
        List<PlanResponseDto> plans = planService.getPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlanResponseDto>> getPlansByUserID(@PathVariable int userId) {
        List<PlanResponseDto> plans = planService.getPlansByUserID(userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavesResponseDto> getLikesAndSavesPlan(@PathVariable int id) {
        LikesAndSavesResponseDto dto = userPlanService.calculateLikesAndSavesByPlanId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PlanResponseDto> savePlan(@Valid @RequestBody PlanCreateDto planDto) {
        PlanResponseDto response = planService.savePlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}