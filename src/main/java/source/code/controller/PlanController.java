package source.code.controller;

import source.code.dto.LikesAndSavedDto;
import source.code.dto.PlanAdditionDto;
import source.code.dto.PlanDto;
import source.code.service.PlanService;
import source.code.exception.ValidationException;
import source.code.service.UserPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<PlanDto> getPlanById(@PathVariable int id) {
        PlanDto plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    public ResponseEntity<List<PlanDto>> getPlans() {
        List<PlanDto> plans = planService.getPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlanDto>> getPlansByUserID(@PathVariable int userId) {
        List<PlanDto> plans = planService.getPlansByUserID(userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}/likes-and-saves")
    public ResponseEntity<LikesAndSavedDto> getLikesAndSavesPlan(@PathVariable int id) {
        LikesAndSavedDto dto = userPlanService.calculateLikesAndSavesByPlanId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PlanDto> savePlan(@Valid @RequestBody PlanAdditionDto planDto) {
        PlanDto response = planService.savePlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}