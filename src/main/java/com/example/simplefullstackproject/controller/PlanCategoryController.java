package com.example.simplefullstackproject.controller;

import com.example.simplefullstackproject.dto.PlanCategoryDto;
import com.example.simplefullstackproject.dto.PlanDto;
import com.example.simplefullstackproject.service.PlanService;
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

    @GetMapping("/{categoryId}/plans")
    public ResponseEntity<List<PlanDto>> getPlansByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(planService.getPlansByCategory(categoryId));
    }
}
