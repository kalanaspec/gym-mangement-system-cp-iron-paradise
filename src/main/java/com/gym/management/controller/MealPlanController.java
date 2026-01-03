package com.gym.management.controller;

import com.gym.management.entity.MealPlan;
import com.gym.management.service.MealPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mealplans")
public class MealPlanController {
    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    public ResponseEntity<MealPlan> create(@RequestBody MealPlan plan) {
        return ResponseEntity.ok(mealPlanService.save(plan));
    }

    @GetMapping
    public ResponseEntity<List<MealPlan>> all() {
        return ResponseEntity.ok(mealPlanService.findAll());
    }
}

