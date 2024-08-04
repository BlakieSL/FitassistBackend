package com.example.simplefullstackproject.controllers;

import com.example.simplefullstackproject.dtos.PlanDto;
import com.example.simplefullstackproject.services.PlanService;
import com.example.simplefullstackproject.exceptions.ValidationException;
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

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<PlanDto> savePlan(
            @Valid @RequestBody PlanDto planDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        PlanDto response = planService.savePlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> getPlanById(@PathVariable Integer id) {
        PlanDto plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    public ResponseEntity<List<PlanDto>> getPlans() {
        List<PlanDto> plans = planService.getPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlanDto>> getPlansByUserID(@PathVariable Integer userId) {
        List<PlanDto> plans = planService.getPlansByUserID(userId);
        return ResponseEntity.ok(plans);
    }
}